package com.uanatol.gwt.contactinfo.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class Contact {
	static private DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();
	static private MemcacheService memcache = MemcacheServiceFactory
			.getMemcacheService();

	private static Key parentKey = KeyFactory.createKey("Contact", 1);
	private static String KEYS = "keys";

	static public void store(String userName, String birthDate) {
		Entity contactEntity = new Entity("Contact", userName + birthDate,
				parentKey);
		contactEntity.setProperty("userName", userName);
		contactEntity.setProperty("birthDate", birthDate);
		datastore.put(contactEntity);

		// Update memcache
		memcache.put(contactEntity.getKey(), contactEntity);
		Set<Key> keys = (HashSet<Key>) memcache.get(KEYS);
		if (keys == null) {
			keys = new HashSet<Key>();
		}
		keys.add(contactEntity.getKey());
		memcache.put(KEYS, keys);
	}

	static public List<Entity> retrieveAll() {
		Set<Key> keys = (HashSet<Key>) (memcache.get(KEYS));
		List<Entity> result;
		if (keys == null || keys.isEmpty()) {
			keys = new HashSet<Key>();
			Query query = new Query("Contact", parentKey)
					.setAncestor(parentKey).addSort("userName",
							Query.SortDirection.ASCENDING);

			result = datastore.prepare(query).asList(
					FetchOptions.Builder.withLimit(1000));
			// Update memcache
			if (result != null && !result.isEmpty()) {
				for (Entity entity : result) {
					memcache.put(entity.getKey(), entity);
					keys.add(entity.getKey());
				}
				memcache.put(KEYS, keys);
			}
		} else {
			Map<Key, Object> results = memcache.getAll(keys);
			if (results != null && !results.isEmpty()) {
				result = new ArrayList<Entity>();
				for (Object value : results.values()) {
					result.add((Entity) (value));
				}
			}
			else {
				result = null;
			}
		}
		return result;
	}

	static public void delete(List<String> keyList) {
		ArrayList<Key> keys = new ArrayList<Key>();
		if (keyList != null) {
			for (String keyString : keyList) {
				Key key = KeyFactory.createKey(parentKey, "Contact", keyString);
				keys.add(key);
			}
			datastore.delete(keys);

			// Update memcache
			memcache.deleteAll(keys);
			Set<Key> memkeys = (HashSet<Key>) (memcache.get(KEYS));
			for (Key key : keys) {
				memkeys.remove(key);
			}
			memcache.put(KEYS, memkeys);
		}
	}
}