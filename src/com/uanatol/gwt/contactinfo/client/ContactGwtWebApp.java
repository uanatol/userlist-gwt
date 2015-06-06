package com.uanatol.gwt.contactinfo.client;

import java.util.ArrayList;
import java.util.List;

import com.uanatol.gwt.contactinfo.shared.FieldVerifier;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
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
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
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

	final FlexTable usersFlexTable = new FlexTable();
	final Button addButton = new Button("Add");
	final Button removeButton = new Button("Remove");
	final Button reloadButton = new Button("Reload");
	final TextBox userNameField = new TextBox();
	final TextBox birthDateNameField = new TextBox();
	final CheckBox checkBox = new CheckBox();
	final Label errorLabel = new Label();
	final DialogBox dialogBox = new DialogBox();
	final Button closeButton = new Button("Close");
	final Label textToServerLabel = new Label();
	final HTML serverResponseLabel = new HTML();
	final AddDataHandler addHandler = new AddDataHandler();
	final ReloadHandler reloadHandler = new ReloadHandler();
	final RemoveHandler removeHandler = new RemoveHandler();

	public void onModuleLoad() {
		// Create the table for the input data data.
		usersFlexTable.setText(0, 0, "User Name");
		usersFlexTable.setText(0, 1, "Birth Date");
		usersFlexTable.setWidget(0, 2, addButton);
		usersFlexTable.setWidget(0, 3, removeButton);
		usersFlexTable.setWidget(0, 4, reloadButton);
		usersFlexTable.setWidget(1, 0, userNameField);
		usersFlexTable.setWidget(1, 1, birthDateNameField);
		checkBox.setValue(true);
		checkBox.setEnabled(false);
		usersFlexTable.setWidget(1, 2, checkBox);
		RootPanel.get("inputFieldContainer").add(usersFlexTable);
		RootPanel.get("errorLabelContainer").add(errorLabel);

		// Create the popup dialog box
		dialogBox.setText("Restlet Call");
		dialogBox.setAnimationEnabled(true);
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final VerticalPanel dialogVPanel = new VerticalPanel();
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
				userNameField.setFocus(true);
			}
		});

		// Add a handler to close the DialogBox
		reloadButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				userNameField.setFocus(true);				
			}
		});

		// Add handlers
		addButton.addClickHandler(addHandler);
		birthDateNameField.addKeyUpHandler(addHandler);
		userNameField.addKeyUpHandler(addHandler);
		reloadButton.addClickHandler(reloadHandler);
		removeButton.addClickHandler(removeHandler);
		reloadHandler.loadData();
	}

	// Create a handler for the addButton and TextBoxes fields
	class AddDataHandler implements ClickHandler, KeyUpHandler {

		final String restPath = "rest/users/add";

		/**
		 * Fired when the user clicks on the sendButton.
		 */
		public void onClick(ClickEvent event) {
			uploadData();
			userNameField.setFocus(true);	
		}

		/**
		 * Fired when the user types in text fields
		 */
		public void onKeyUp(KeyUpEvent event) {
			if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
				uploadData();
			}
			else {
				addButton.setEnabled(true);
			}
		}

		/**
		 * Send the name from the nameField to the server and wait for a
		 * response.
		 */
		private void uploadData() {
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
			String json = "{\"userName\": \"" + userNameToServer + "\","
					+ "\"birthDate\": \"" + birthDateToServer + "\"}";

			textToServerLabel.setText(json);
			serverResponseLabel.setText("");

			String url = GWT.getHostPageBaseURL() + restPath;
			RequestBuilder builder = new RequestBuilder(RequestBuilder.PUT,
					URL.encode(url));
			builder.setHeader("Content-Type", "application/json; charset=UTF-8");
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
								int responseCode = response.getStatusCode();
								if ((200 == responseCode)
										|| (204 == responseCode)) {
									reloadHandler.loadData();
								} else {
									// Handle the error. Can get the status
									// text from response.getStatusText()
									errorMessage(response);
									closeButton.setEnabled(true);
									closeButton.setFocus(true);	
								}
							}
						});
			} catch (RequestException e) {
				// Couldn't connect to server
				dialogBox.setText("Server Error");
				serverResponseLabel.addStyleName("serverResponseLabelError");
				serverResponseLabel.setHTML(SERVER_ERROR);
				dialogBox.center();
				closeButton.setFocus(true);
			}
		}
		
		private void errorMessage(Response response) {
			dialogBox.setText("Http request - Failure");
			serverResponseLabel
					.addStyleName("serverResponseLabelError");
			serverResponseLabel.setHTML(response
					.getStatusCode()
					+ " : "
					+ response.getStatusText());
			dialogBox.center();			
		}
	}

	// Create a handler for the addButton and TextBoxes fields
	class ReloadHandler implements ClickHandler {

		final String restPath = "rest/users/all";

		/**
		 * Fired when the user clicks on the sendButton.
		 */
		public void onClick(ClickEvent event) {
			loadData();
			userNameField.setFocus(true);	
		}

		/**
		 * Send the name from the nameField to the server and wait for a
		 * response.
		 */
		public void loadData() {
			// First, we validate the input.
			errorLabel.setText("");

			// Then, we send the input to the server.
			// Json string
			textToServerLabel.setText("");
			serverResponseLabel.setText("");

			String url = GWT.getHostPageBaseURL() + restPath;
			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
					URL.encode(url));
			// builder.setHeader("Content-Type",
			// "application/json; charset=UTF-8");
			try {
				Request request = builder.sendRequest(null,
						new RequestCallback() {
							public void onError(Request request,
									Throwable exception) {
								// Couldn't connect to server (could be
								// timeout, SOP violation, etc.)
							}

							public void onResponseReceived(Request request,
									Response response) {
								if (200 == response.getStatusCode()) {
									String jsonText = response.getText();
									JSONValue jsonValue = JSONParser
											.parseStrict(jsonText);
									JSONObject jsonObject = jsonValue
											.isObject();
									if (jsonObject == null) {
										throw new RuntimeException(
												"JSON payload did not describe an object");
									}
									ContactInfoArrayJso contacts = jsonObject
											.getJavaScriptObject().cast();

									JsArray<ContactInfoJso> cs = contacts
											.getContactsInfo();
									int count = usersFlexTable.getRowCount();
									for (int i = 2; i < count; i++) {
										usersFlexTable.removeRow(2);
									}

									if ((cs != null) && (cs.length() > 0)) {
										for (int i = 0, n = cs.length(); i < n; ++i) {
											int row = i + 2;
											usersFlexTable.setText(row, 0, cs
													.get(i).getUserName());
											usersFlexTable.setText(row, 1, cs
													.get(i).getBirthDate());
											final CheckBox checkBox = new CheckBox();
											checkBox.setValue(false);
											checkBox.setEnabled(true);
											usersFlexTable.setWidget(row, 3,
													checkBox);
										}
									}
								} else {
									// Handle the error. Can get the status
									// text from response.getStatusText()
									errorMessage(response);
									closeButton.setEnabled(true);
									closeButton.setFocus(true);	
								}
							}
						});
			} catch (RequestException e) {
				// Couldn't connect to server
			}
		}
		private void errorMessage(Response response) {
			dialogBox.setText("Http request - Failure");
			serverResponseLabel
					.addStyleName("serverResponseLabelError");
			serverResponseLabel.setHTML(response
					.getStatusCode()
					+ " : "
					+ response.getStatusText());
			dialogBox.center();			
		}
	}

	// Create a handler for the removeButton
	class RemoveHandler implements ClickHandler {

		final String restPath = "rest/users/remove";

		/**
		 * Fired when the user clicks on the sendButton.
		 */
		public void onClick(ClickEvent event) {
			removeData();
			userNameField.setFocus(true);	
		}

		/**
		 * Send the name from the nameField to the server and wait for a
		 * response.
		 */
		public void removeData() {
			// First, we validate the input.
			errorLabel.setText("");
			textToServerLabel.setText("");
			serverResponseLabel.setText("");

			List<String> userName = new ArrayList<String>();
			List<String> birthDate = new ArrayList<String>();
			int count = usersFlexTable.getRowCount();
			for (int i = 2; i < count; i++) {
				CheckBox checkBox = (CheckBox) usersFlexTable.getWidget(i, 3);
				if (checkBox.getValue() == true) {
					userName.add(usersFlexTable.getText(i, 0));
					birthDate.add(usersFlexTable.getText(i, 1));
				}
			}
			String json = "[";
			int size = userName.size();
			for (int i = 0; i < size - 1; i++) {
				json = json + "\"" + userName.get(i) + birthDate.get(i) + "\",";
			}
			json = json + "\"" + userName.get(size - 1)
					+ birthDate.get(size - 1) + "\"";
			json = json + "]";
			String url = GWT.getHostPageBaseURL() + restPath;
			RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
					URL.encode(url));
			builder.setHeader("Content-Type", "application/json; charset=UTF-8");
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
								int responseCode = response.getStatusCode();
								if ((200 == responseCode)
										|| (204 == responseCode)) {
									reloadHandler.loadData();
								} else {
									// Handle the error. Can get the status
									// text from response.getStatusText()
									errorMessage(response);
									closeButton.setEnabled(true);
									closeButton.setFocus(true);									
								}
							}
						});								
			} catch (RequestException e) {
				// Couldn't connect to server
			}
		}
		
		private void errorMessage(Response response) {
			dialogBox.setText("Http request - Failure");
			serverResponseLabel
					.addStyleName("serverResponseLabelError");
			serverResponseLabel.setHTML(response
					.getStatusCode()
					+ " : "
					+ response.getStatusText());
			dialogBox.center();			
		}		
	}
}
