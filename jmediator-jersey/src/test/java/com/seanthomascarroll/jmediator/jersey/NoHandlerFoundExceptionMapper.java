package com.seanthomascarroll.jmediator.jersey;

import com.seanthomascarroll.jmediator.NoHandlerForRequestException;
//import jakarta.ws.rs.core.Response;
//import jakarta.ws.rs.ext.ExceptionMapper;
//import jakarta.ws.rs.ext.Provider;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NoHandlerFoundExceptionMapper implements ExceptionMapper<NoHandlerForRequestException> {

    @Override
    public Response toResponse(NoHandlerForRequestException exception) {
        return Response.status(500).entity(exception.getMessage()).type("text/plain").build();
    }

}
