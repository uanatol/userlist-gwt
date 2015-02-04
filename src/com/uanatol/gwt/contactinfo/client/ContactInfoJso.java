package com.uanatol.gwt.contactinfo.client;

import com.google.gwt.core.client.JavaScriptObject;

public class ContactInfoJso extends JavaScriptObject {

	// Overlay types always have protected, zero-arguments constructors
	protected ContactInfoJso() {
	}

	// Typically, methods on overlay types are JSNI
	public final native String getUserName() /*-{
		return this.userName;
	}-*/;

	public final native String getBirthDate() /*-{
		return this.birthDate;
	}-*/;

}