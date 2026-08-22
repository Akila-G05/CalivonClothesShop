package lk.jiat.calivon.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.calivon.entity.User;
import lk.jiat.calivon.service.SessionService;
import lk.jiat.calivon.util.AppUtil;

@Path("/sessions")
public class SessionController {
    @Path("/sessions-data")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSessions(@Context HttpServletRequest request){
        String responseJson = new SessionService().getSessionData(request);
        return Response.ok().entity(responseJson).build();
    }
}
