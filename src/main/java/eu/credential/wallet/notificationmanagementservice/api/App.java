package eu.credential.wallet.notificationmanagementservice.api;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class App {

	private Logger logger = LoggerFactory.getLogger(App.class);
	private AppProperties properties;

	private Options options = new Options();
	private CommandLineParser parser = new DefaultParser();

	private String confFile = null;

	public static void main(String[] args) {
		App myApp = new App(args);
		myApp.start();
	}

	private App(String[] args) {

		Option optConf = Option.builder("conf").desc("configuration").hasArg().argName("conf").build();
		options.addOption(optConf);

		try {
			CommandLine cmd = parser.parse(options, args, true);

			if (cmd.hasOption("conf")) {
				confFile = cmd.getOptionValue("conf");
			}
		} catch (Exception e) {
			//tbd
		}
	}

	private void start() {

		logger.info("Starting Application");

		if (confFile != null) {
			// load properties from configured file
			properties = AppProperties.getInstance(confFile);
		} else {
			// load properties from baked in configuration file
			properties = AppProperties.getInstance();
		}

		if (properties != null) {
		
			String port = properties.getValue("server.port");
			Server server = new Server(new Integer(port).intValue());
	
			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
			context.setContextPath("/");
	
			SessionManager sessionManager = new HashSessionManager();
			sessionManager.setSessionIdPathParameterName("none");
	
			SessionHandler sessionHandler = new SessionHandler();
			sessionHandler.setSessionManager(sessionManager);
			context.setSessionHandler(sessionHandler);
	
			server.setHandler(context);
	
			ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/v1/*");
			jerseyServlet.setInitOrder(1);
			jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", "org.glassfish.jersey.filter.LoggingFilter;org.glassfish.jersey.media.multipart.MultiPartFeature");
			jerseyServlet.setInitParameter("jersey.config.server.provider.packages", "io.swagger.jaxrs.listing,io.swagger.sample.resource,eu.credential.wallet.notificationmanagementservice.api,org.codehaus.jackson.jaxrs");
	
			ServletHolder staticServlet = context.addServlet(DefaultServlet.class,"/*");
			String webDir = this.getClass().getClassLoader().getResource("webapp").toExternalForm();
			staticServlet.setInitParameter("resourceBase", webDir);
			staticServlet.setInitParameter("pathInfoOnly", "true");
	
			try {
				server.start();
				server.join();
			} catch (Throwable t) {
				t.printStackTrace(System.err);
			}

		} else {
			logger.error("Error on loading configuration.");
		}

	}
}