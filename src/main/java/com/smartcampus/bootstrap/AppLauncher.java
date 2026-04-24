package com.smartcampus.bootstrap;

import com.smartcampus.config.CampusApiApplication;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import java.util.logging.Logger;

public class AppLauncher {

    private static final Logger LOG = Logger.getLogger(AppLauncher.class.getName());
    public static final int SERVER_PORT = 8080;

    public static void main(String[] args) throws Exception {
        Server jettyServer = new Server(SERVER_PORT);

        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        contextHandler.setContextPath("/");

        ServletHolder jerseyHolder = contextHandler.addServlet(ServletContainer.class, "/api/v1/*");
        jerseyHolder.setInitOrder(0);
        jerseyHolder.setInitParameter(
                "javax.ws.rs.Application",
                CampusApiApplication.class.getCanonicalName()
        );

        jettyServer.setHandler(contextHandler);
        jettyServer.start();

        LOG.info("================================================");
        LOG.info(" Smart Campus API started on port " + SERVER_PORT);
        LOG.info(" Base URL : http://localhost:" + SERVER_PORT + "/api/v1");
        LOG.info("================================================");

        jettyServer.join();
    }
}
