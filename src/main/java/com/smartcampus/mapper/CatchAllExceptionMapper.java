package com.smartcampus.mapper;

import com.smartcampus.model.ApiErrorPayload;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class CatchAllExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(CatchAllExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable ex) {
        LOG.log(Level.SEVERE, "Unhandled exception caught by safety net: " + ex.getMessage(), ex);
        ApiErrorPayload payload = new ApiErrorPayload(
                500,
                "Internal Server Error",
                "An unexpected error occurred. Please contact the system administrator."
        );
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(payload)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
