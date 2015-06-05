package com.uanatol.gwt.contactinfo.server;

public class ContactInfo {

	private String userName;
	private String birthDate;

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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("***** Employee Details *****\n");
		sb.append("Name=" + getUserName() + "\n");
		sb.append("BirthDate=" + getBirthDate() + "\n");
		sb.append("*****************************");

		return sb.toString();
	}

}