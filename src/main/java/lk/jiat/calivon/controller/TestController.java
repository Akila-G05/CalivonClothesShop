package lk.jiat.calivon.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lk.jiat.calivon.annotation.IsUser;

@Path("/test")
public class TestController {
    @IsUser
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test(){
        return "test";
    }
}
