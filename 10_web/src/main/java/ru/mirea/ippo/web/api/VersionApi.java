package ru.mirea.ippo.web.api;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static ru.mirea.ippo.web.Constants.APPLICATION_JSON_UTF8;

@Path("/version")
public interface VersionApi {
    @GET
    @Path("/{product}")
    @Produces(APPLICATION_JSON_UTF8)
    Response version(@PathParam("product") String product, @QueryParam("type") @DefaultValue("json") String type);
}
