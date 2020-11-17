package com.uanatol.gwt.contactinfo.server;

import java.util.ArrayList;
import java.util.List;

import org.restlet.resource.ServerResource;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.StringValue;
import com.uanatol.gwt.contactinfo.shared.FieldVerifier;

public class ContactServerResource extends ServerResource implements ContactResource {

	public void remove(List<String> contactKeys) {
		Contact.delete(contactKeys);
	}

	public void store(ContactInfo contactInfo) {
		String firstName = contactInfo.getFirstName();
		String lastName = contactInfo.getLastName();
		assert !FieldVerifier.isValidName(firstName) : "First name must be at least four characters";
		assert !FieldVerifier.isValidName(lastName) : "Last name must be at least four characters";
		Contact.store(firstName, lastName);
	}

	public ContactInfoArray retrieve() {
		List<Entity> entityList = Contact.retrieveAll();
		List<ContactInfo> contactInfoList = new ArrayList<ContactInfo>();
		for (Entity entity : entityList) {
			String firstName = null;
			String lastName = null;
			for (String name : entity.getNames()) {
				if ("firstName".equals(name)) {
					if (entity.getValue(name) instanceof StringValue) {
						firstName = entity.getString(name);
					}
				}
				if ("lastName".equals(name)) {
					if (entity.getValue(name) instanceof StringValue) {
						lastName = entity.getString(name);
					}
				}
			}
			if (firstName != null && lastName != null) {
				ContactInfo contactInfo = new ContactInfo();
				contactInfo.setFirstName(firstName);
				contactInfo.setLastName(lastName);
				contactInfoList.add(contactInfo);
			}
		}
		return new ContactInfoArray(contactInfoList);
	}
}
