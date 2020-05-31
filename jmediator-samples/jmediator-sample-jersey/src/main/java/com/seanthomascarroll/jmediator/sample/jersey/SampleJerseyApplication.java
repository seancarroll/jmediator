package com.seanthomascarroll.jmediator.sample.jersey;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;

public class SampleJerseyApplication {

    public static void main(String[] args) {
        Server server = new Server(8080);

        ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);

        ctx.setContextPath("/");
        server.setHandler(ctx);

        AppConfig config = new AppConfig();
        ServletHolder servlet = new ServletHolder(new ServletContainer(config));
        ctx.addServlet(servlet, "/*");

        ServletHolder serHol = ctx.addServlet(ServletContainer.class, "/*");
        serHol.setInitOrder(1);
        serHol.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "com.seanthomascarroll.jmediator.sample.jersey");

        try {
            server.start();
            server.join();
        } catch (Exception ex) {
            LogManager.getLogger(SampleJerseyApplication.class.getName()).log(Level.ERROR, ex);
        } finally {
            server.destroy();
        }
    }

}
