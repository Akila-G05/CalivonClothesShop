package lk.jiat.calivon;

import lk.jiat.calivon.config.AppConfig;
import lk.jiat.calivon.listener.ContextPathListner;
import lk.jiat.calivon.util.Env;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.File;

public class Main {
    private static final int SERVER_PORT = 8080;
    private static final String CONTEXT_PATH = "/calivon";

    public static void main(String[] args) {
        try{
            Tomcat tomcat = new Tomcat();
            tomcat.setPort(SERVER_PORT);
            tomcat.getConnector();

            Context context = tomcat.addWebapp(CONTEXT_PATH, new File("src/main/webapp").getAbsolutePath());
            Tomcat.addServlet(context, "JerseyServlet", new ServletContainer(new AppConfig()));
            context.addServletMappingDecoded("/api/*", "JerseyServlet");

            context.addApplicationListener(ContextPathListner.class.getName());

            tomcat.start();

            System.out.println("Tomcat Server Successfully Deployed. App URL : http://localhost:" + SERVER_PORT + CONTEXT_PATH);
            System.out.println("Tomcat Server Successfully Deployed. App URL : " + Env.get("app.public.url"));
            tomcat.getServer().await();
        }catch (LifecycleException e){
            throw new RuntimeException("Tomcat Embeded Sever Deployed Faild. Error : " + e.getMessage());
        }
    }
}
