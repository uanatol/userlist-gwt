package com.uanatol.gwt.contactinfo.server;

import java.util.List;

public class ContactInfoArray {

	private List<ContactInfo> contacts;

	public ContactInfoArray(List<ContactInfo> contacts) {
		this.contacts = contacts;
	}

	public List<ContactInfo> getContacts() {
		return contacts;
	}

	public void setContacts(List<ContactInfo> contacts) {
		this.contacts = contacts;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("***** Employee Details *****\n");
		for (ContactInfo contact : contacts) {
			sb.append("FirstName=" + contact.getFirstName() + " ");
			sb.append("LastName=" + contact.getLastName() + "\n");
		}
		sb.append("*****************************");
		return sb.toString();
	}
}
