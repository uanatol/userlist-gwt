package com.uanatol.gwt.contactinfo.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class ContactInfoArrayJso extends JavaScriptObject {

	// Overlay types always have protected, zero-arguments constructors
	protected ContactInfoArrayJso() {
	}
		
	public final native JsArray<ContactInfoJso> getContactsInfo() /*-{
		return this.contacts;
	}-*/;
}