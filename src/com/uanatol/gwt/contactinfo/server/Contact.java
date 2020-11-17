package com.uanatol.gwt.contactinfo.server;

import java.util.ArrayList;
import java.util.List;

import com.google.cloud.NoCredentials;
import com.google.cloud.ServiceOptions;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;

public class Contact {

	//static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	static Datastore datastore = DatastoreOptions.newBuilder()
			.setProjectId("xxxxxxxxx-xxxx-xxx")
			.setHost("http://localhost:8081").setCredentials(NoCredentials.getInstance())
			.setRetrySettings(ServiceOptions.getNoRetrySettings()).build().getService();

	private static String parentKey = "Contact";

	static public void store(String firstName, String lastName) {
		// Prepares the new entity
		Key contactKey = datastore.newKeyFactory().setKind(parentKey).newKey(firstName + ':' + lastName);
		Entity contactEntity = Entity.newBuilder(contactKey).set("firstName", firstName).set("lastName", lastName)
				.build();
		// Saves the entity
		datastore.put(contactEntity);
		Key key = datastore.newKeyFactory().setKind(parentKey).newKey(firstName + ':' + lastName);
		Entity entity = datastore.get(key);
		assert contactEntity == entity;
	}

	static public List<Entity> retrieveAll() {
		// Retrieve entity
		Query<Entity> query = Query.newEntityQueryBuilder().setKind(parentKey).setOrderBy(OrderBy.asc("firstName"))
				.build();

		QueryResults<Entity> results = datastore.run(query);
		List<Entity> result = new ArrayList<Entity>();
		while (results.hasNext()) {
			Entity entity = results.next();
			result.add(entity);
		}
		return result;
	}

	static public void delete(List<String> keyList) {
		if (keyList != null) {
			for (String keyString : keyList) {
				Key key = datastore.newKeyFactory().setKind(parentKey).newKey(keyString);
				datastore.delete(key);
			}
		}
	}
}