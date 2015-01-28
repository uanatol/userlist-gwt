package com.uanatol.gwt.contactinfo.client;

import com.uanatol.gwt.contactinfo.shared.FieldVerifier;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ContactGwtWebApp implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	public void onModuleLoad() {

		final FlexTable usersFlexTable = new FlexTable();
		// Create the table for user data.
		usersFlexTable.setText(0, 0, "User Name");
		usersFlexTable.setText(0, 1, "Birth Date");
		final Button addButton = new Button("Add");
		usersFlexTable.setWidget(0, 2, addButton);
		final Button removeButton = new Button("Remove");
		usersFlexTable.setWidget(0, 3, removeButton);
		final TextBox userNameField = new TextBox();
		final TextBox birthDateNameField = new TextBox();
		usersFlexTable.setWidget(1, 0, userNameField);
		usersFlexTable.setWidget(1, 1, birthDateNameField);
		final Label errorLabel = new Label();
		RootPanel.get("nameFieldContainer").add(usersFlexTable);
		RootPanel.get("errorLabelContainer").add(errorLabel);

		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				addButton.setEnabled(true);
				addButton.setFocus(true);
			}
		});
		
		// Create a handler for the addButton and TextBoxes fields
		class MyHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				sendUserDataToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendUserDataToServer();
				}
			}

			/**
			 * Send the name from the nameField to the server and wait for a
			 * response.
			 */
			private void sendUserDataToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				String userNameToServer = userNameField.getText();
				if (!FieldVerifier.isValidName(userNameToServer)) {
					errorLabel.setText("Please enter at least four characters");
					return;
				}
				String birthDateToServer = birthDateNameField.getText();
				if (!FieldVerifier.isValidName(birthDateToServer)) {
					errorLabel.setText("Please enter at least four characters");
					return;
				}

				// Then, we send the input to the server.
				addButton.setEnabled(false);
				// Json string
				String json = "\"{\"userName\": \"" + userNameToServer + "\","
						+ "\"birthDate\": \"" + birthDateToServer + "\"}\"";

				textToServerLabel.setText(json);
				serverResponseLabel.setText("");

				String url=GWT.getHostPageBaseURL() + "rest/users/all";
				RequestBuilder builder = new RequestBuilder(RequestBuilder.PUT,
						URL.encode(url));
				try {
					Request request = builder.sendRequest(json,
							new RequestCallback() {
								public void onError(Request request,
										Throwable exception) {
									// Couldn't connect to server (could be
									// timeout, SOP violation, etc.)
								}

								public void onResponseReceived(Request request,
										Response response) {
									if (200 == response.getStatusCode()) {
										// Process the response in
										// response.getText()
										dialogBox.setText("Http request");
										serverResponseLabel
												.removeStyleName("serverResponseLabelError");
										serverResponseLabel.setHTML(response
												.getText());
										dialogBox.center();
										closeButton.setFocus(true);
									} else {
										// Handle the error. Can get the status
										// text from response.getStatusText()
										dialogBox
												.setText("Http request - Failure");
										serverResponseLabel
												.addStyleName("serverResponseLabelError");
										serverResponseLabel
												.setHTML(response.getStatusText());
										dialogBox.center();
										closeButton.setFocus(true);
									}
								}
							});
				} catch (RequestException e) {
					// Couldn't connect to server
				}
			}
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		addButton.addClickHandler(handler);
		birthDateNameField.addKeyUpHandler(handler);
	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoadOld() {
		final Button sendButton = new Button("Send");
		final TextBox nameField = new TextBox();
		nameField.setText("GWT User");
		final Label errorLabel = new Label();

		// We can add style names to widgets
		sendButton.addStyleName("sendButton");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get("nameFieldContainer").add(nameField);
		RootPanel.get("sendButtonContainer").add(sendButton);
		RootPanel.get("errorLabelContainer").add(errorLabel);

		// Focus the cursor on the name field when the app loads
		nameField.setFocus(true);
		nameField.selectAll();

		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				sendButton.setEnabled(true);
				sendButton.setFocus(true);
			}
		});

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendNameToServer();
				}
			}

			/**
			 * Send the name from the nameField to the server and wait for a
			 * response.
			 */
			private void sendNameToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				String textToServer = nameField.getText();
				if (!FieldVerifier.isValidName(textToServer)) {
					errorLabel.setText("Please enter at least four characters");
					return;
				}

				// Then, we send the input to the server.
				sendButton.setEnabled(false);
				textToServerLabel.setText(textToServer);
				serverResponseLabel.setText("");
				greetingService.greetServer(textToServer,
						new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								dialogBox
										.setText("Remote Procedure Call - Failure");
								serverResponseLabel
										.addStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(SERVER_ERROR);
								dialogBox.center();
								closeButton.setFocus(true);
							}

							public void onSuccess(String result) {
								dialogBox.setText("Remote Procedure Call");
								serverResponseLabel
										.removeStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(result);
								dialogBox.center();
								closeButton.setFocus(true);
							}
						});
			}
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendButton.addClickHandler(handler);
		nameField.addKeyUpHandler(handler);
	}
}