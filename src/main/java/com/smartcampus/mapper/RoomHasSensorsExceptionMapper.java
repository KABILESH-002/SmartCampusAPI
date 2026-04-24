package com.smartcampus.mapper;

import com.smartcampus.exception.RoomHasSensorsException;
import com.smartcampus.model.ApiErrorPayload;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RoomHasSensorsExceptionMapper implements ExceptionMapper<RoomHasSensorsException> {

    @Override
    public Response toResponse(RoomHasSensorsException ex) {
        ApiErrorPayload payload = new ApiErrorPayload(409, "Conflict", ex.getMessage());
        return Response.status(Response.Status.CONFLICT)
                .entity(payload)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
