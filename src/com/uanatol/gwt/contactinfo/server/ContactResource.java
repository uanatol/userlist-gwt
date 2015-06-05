package com.uanatol.gwt.contactinfo.server;

import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

public interface ContactResource {
	@Get("json")
	public ContactInfoArray retrieve();

	@Put("json")
	public void store(ContactInfo contact);

	@Post("json")
	public void remove(List<String> contactKeys);
}
