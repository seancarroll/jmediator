package com.seanthomascarroll.jmediator.jersey;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class DefaultExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {
        System.out.println(e);
        return Response.status(500).entity(e.getMessage()).type("text/plain").build();
    }
}
