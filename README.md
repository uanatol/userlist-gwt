# userlist-gwt

GWT and GAE demo project

1. Build the project.  
(The project uses Google Cloud Platform libraries, including datastore client API library)
2. Compile the project using GWT compiler.
3. Run the project as App Engine based project.

IMPORTANT:

1. This project uses Google Datastore. Start the datastore simulator in Google Cloud SDK shell using the command:  
 `gcloud beta emulators datastore start`
2. When App Engine project starts, the web application is deployed by Eclipse in this folder:  
`.../workspace/.metadata/.plugins/org.eclipse.wst.server.core`  
and a local App Engine server is started to load the web application from there.
During the development, you may need to periodically delete the content of this folder to be sure a clean version of the application is deployed.
