package com.example;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }

    @POST
    @Path("{p1}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response mypost(Request req, @PathParam("p1") String p1) {
        System.out.println("query:" + req.getQuery());
        System.out.println("p1:" + p1);
        Response resp = new Response();
        resp.setRespCode(0);
        resp.setRespDesc(req.getQuery());
        return resp;
    }
}
