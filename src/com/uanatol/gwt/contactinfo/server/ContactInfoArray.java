package com.uanatol.gwt.contactinfo.server;

import java.util.List;

public class ContactInfoArray {

	private List<ContactInfo> contacts;
	
	public ContactInfoArray(List<ContactInfo> contacts) {
		//super();
		this.contacts = contacts;
	}

	public List<ContactInfo> getContacts() {
		return contacts;
	}

	public void setContacts(List<ContactInfo> contacts) {
		this.contacts = contacts;
	}

}
