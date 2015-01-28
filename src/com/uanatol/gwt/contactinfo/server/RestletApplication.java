package com.uanatol.gwt.contactinfo.server;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class RestletApplication extends Application {
	/**
	 * Creates a root Restlet that will receive all incoming calls.
	 */
	@Override
	public Restlet createInboundRoot() {
		// Create a router
		Router router = new Router(getContext());
		router.attach("/users/{name}", ContactServerResource.class);		
		return router;
	}
}