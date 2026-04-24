package com.smartcampus.mapper;

import com.smartcampus.exception.SensorOfflineException;
import com.smartcampus.model.ApiErrorPayload;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SensorOfflineExceptionMapper implements ExceptionMapper<SensorOfflineException> {

    @Override
    public Response toResponse(SensorOfflineException ex) {
        ApiErrorPayload payload = new ApiErrorPayload(403, "Forbidden", ex.getMessage());
        return Response.status(Response.Status.FORBIDDEN)
                .entity(payload)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
