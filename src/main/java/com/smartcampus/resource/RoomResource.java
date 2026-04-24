package com.smartcampus.resource;

import com.smartcampus.exception.RoomHasSensorsException;
import com.smartcampus.model.ApiErrorPayload;
import com.smartcampus.model.Room;
import com.smartcampus.repository.CampusDataRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    @GET
    public Response listAllRooms() {
        List<Room> allRooms = new ArrayList<>(CampusDataRepository.getAllRooms().values());
        return Response.ok(allRooms).build();
    }

    @POST
    public Response addRoom(Room incomingRoom) {
        if (incomingRoom == null || incomingRoom.getId() == null || incomingRoom.getId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiErrorPayload(400, "Bad Request", "Room 'id' is required.")).build();
        }
        if (incomingRoom.getName() == null || incomingRoom.getName().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiErrorPayload(400, "Bad Request", "Room 'name' is required.")).build();
        }
        if (CampusDataRepository.findRoom(incomingRoom.getId()) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ApiErrorPayload(409, "Conflict",
                            "A room with id '" + incomingRoom.getId() + "' already exists.")).build();
        }
        CampusDataRepository.saveRoom(incomingRoom);
        return Response.status(Response.Status.CREATED).entity(incomingRoom).build();
    }

    @GET
    @Path("/{roomId}")
    public Response fetchRoom(@PathParam("roomId") String roomId) {
        Room foundRoom = CampusDataRepository.findRoom(roomId);
        if (foundRoom == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiErrorPayload(404, "Not Found",
                            "Room '" + roomId + "' not found.")).build();
        }
        return Response.ok(foundRoom).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response removeRoom(@PathParam("roomId") String roomId) {
        Room targetRoom = CampusDataRepository.findRoom(roomId);
        if (targetRoom == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiErrorPayload(404, "Not Found",
                            "Room '" + roomId + "' not found.")).build();
        }
        if (!targetRoom.getSensorIds().isEmpty()) {
            throw new RoomHasSensorsException(
                    "Cannot delete room '" + roomId + "'. It still has " +
                    targetRoom.getSensorIds().size() + " sensor(s) assigned: " + targetRoom.getSensorIds()
            );
        }
        CampusDataRepository.removeRoom(roomId);
        return Response.noContent().build();
    }
}
