package com.uanatol.gwt.contactinfo.server;

public class ContactInfo {

	private String firstName;
	private String lastName;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("***** Employee Details *****\n");
		sb.append("FirstName=" + getFirstName() + "\n");
		sb.append("LastName=" + getLastName() + "\n");
		sb.append("*****************************");

		return sb.toString();
	}

}