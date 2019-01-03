package com.horde;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {

    Server server = new Server(8080);

    ServletHolder jerseyServlet = new ServletHolder(new ServletContainer(new MyResourceConfig()));
    ServletContextHandler context = new ServletContextHandler(server, "/");
    context.addServlet(jerseyServlet, "/*");

    try {
      server.start();
      server.join();
    } catch (Exception ex) {
      logger.error("Failed to start server", ex);
    } finally {
      server.destroy();
    }
  }
}
