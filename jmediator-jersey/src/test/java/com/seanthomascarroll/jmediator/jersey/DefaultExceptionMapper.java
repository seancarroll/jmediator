package com.seanthomascarroll.jmediator.jersey;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DefaultExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {
        System.out.println(e);
        return Response.status(500).entity(e.getMessage()).type("text/plain").build();
    }
}
