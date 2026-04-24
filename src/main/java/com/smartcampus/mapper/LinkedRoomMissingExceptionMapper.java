package com.smartcampus.mapper;

import com.smartcampus.exception.LinkedRoomMissingException;
import com.smartcampus.model.ApiErrorPayload;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class LinkedRoomMissingExceptionMapper implements ExceptionMapper<LinkedRoomMissingException> {

    @Override
    public Response toResponse(LinkedRoomMissingException ex) {
        ApiErrorPayload payload = new ApiErrorPayload(422, "Unprocessable Entity", ex.getMessage());
        return Response.status(422)
                .entity(payload)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
