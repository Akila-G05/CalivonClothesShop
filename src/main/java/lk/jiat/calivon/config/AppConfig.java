package lk.jiat.calivon.config;

import org.glassfish.jersey.server.ResourceConfig;

public class AppConfig extends ResourceConfig {
    public AppConfig(){
        packages("lk.jiat.calivon.controller");
        packages("lk.jiat.calivon.middleware");
        register(org.glassfish.jersey.media.multipart.MultiPartFeature.class);
    }
}
