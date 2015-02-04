package com.uanatol.gwt.contactinfo.server;

import java.util.ArrayList;
import java.util.List;

import org.restlet.resource.ServerResource;

import com.google.appengine.api.datastore.Entity;

public class ContactServerResource extends ServerResource implements
		ContactResource {

	public void remove(List<String> contactKeys) {
		Contact.delete(contactKeys);
	}

	public void store(ContactInfo contactInfo) {
		String userName = contactInfo.getUserName();
		String birthDate = contactInfo.getBirthDate();		
		if (!userName.isEmpty()) {
			Contact.store(userName, birthDate);
		}
	}

	public ContactInfoArray retrieve() {
		List<Entity> entityList = Contact.retrieveAll();
		List<ContactInfo> contactInfoList = new ArrayList<ContactInfo>();
		for (Entity entity : entityList) {
			Object usrname = entity.getProperty("userName");
			if (usrname == null) {
				continue;
			}
			Object birthdate = entity.getProperty("birthDate");
			if (birthdate == null) {
				continue;
			}
			ContactInfo contactInfo = new ContactInfo();
			contactInfo.setUserName(usrname.toString());
			contactInfo.setBirthDate(birthdate.toString());
			contactInfoList.add(contactInfo);
		}
		return new ContactInfoArray(contactInfoList);
	}

}
