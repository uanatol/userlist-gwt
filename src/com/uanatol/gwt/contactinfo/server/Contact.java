package com.uanatol.gwt.contactinfo.server;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

public class Contact {
	static private DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();

	private static Key parentKey = KeyFactory.createKey("Contact",1);
			
	//public Contact() {
	//};

	//public Contact(int id, String userName, String birthDate) {
	//	this.userName = userName;
	//	this.birthDate = birthDate;
	//	this.id = id;
	//}

	static public void store(String userName, String birthDate) {
		Entity contactEntity = new Entity("Contact", userName+birthDate, parentKey);
		contactEntity.setProperty("userName", userName);
		contactEntity.setProperty("birthDate", birthDate);
		datastore.put(contactEntity);
	}

	static public List<Entity> retrieveAll() {
		Query query = new Query("Contact", parentKey)
        .setAncestor(parentKey)
        .addSort("userName", Query.SortDirection.ASCENDING);
		
		List<Entity> result = datastore.prepare(query).asList(
				FetchOptions.Builder.withLimit(100));
		return result;
	}

	static public void delete(List<String> keyList) {
		ArrayList<Key> keys = new ArrayList<Key>();
		for (String keyString : keyList) {
			Key key = KeyFactory.createKey(parentKey, "Contact", keyString);
			keys.add(key);
		}
		datastore.delete(keys);
	}
	/*
	MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
	Object dataToStore;
	cache.put("myKey", dataToStore);
	Object dataRetreived = ca
	*/
	/*
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	*/
}