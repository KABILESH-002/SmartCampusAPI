package com.smartcampus.resource;

import com.smartcampus.exception.LinkedRoomMissingException;
import com.smartcampus.model.ApiErrorPayload;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.repository.CampusDataRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    @GET
    public Response listSensors(@QueryParam("type") String filterType) {
        List<Sensor> sensorCollection = new ArrayList<>(CampusDataRepository.getAllSensors().values());
        if (filterType != null && !filterType.isBlank()) {
            sensorCollection = sensorCollection.stream()
                    .filter(s -> s.getType().equalsIgnoreCase(filterType))
                    .collect(Collectors.toList());
        }
        return Response.ok(sensorCollection).build();
    }

    @POST
    public Response registerSensor(Sensor incomingSensor) {
        if (incomingSensor == null || incomingSensor.getId() == null || incomingSensor.getId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiErrorPayload(400, "Bad Request", "Sensor 'id' is required.")).build();
        }
        if (incomingSensor.getRoomId() == null || incomingSensor.getRoomId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiErrorPayload(400, "Bad Request", "Sensor 'roomId' is required.")).build();
        }
        if (CampusDataRepository.findSensor(incomingSensor.getId()) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ApiErrorPayload(409, "Conflict",
                            "A sensor with id '" + incomingSensor.getId() + "' already exists.")).build();
        }
        Room parentRoom = CampusDataRepository.findRoom(incomingSensor.getRoomId());
        if (parentRoom == null) {
            throw new LinkedRoomMissingException(
                    "Cannot register sensor: room '" + incomingSensor.getRoomId() + "' does not exist."
            );
        }
        if (incomingSensor.getStatus() == null || incomingSensor.getStatus().isBlank()) {
            incomingSensor.setStatus("ACTIVE");
        }
        CampusDataRepository.saveSensor(incomingSensor);
        parentRoom.getSensorIds().add(incomingSensor.getId());
        return Response.status(Response.Status.CREATED).entity(incomingSensor).build();
    }

    @GET
    @Path("/{sensorId}")
    public Response fetchSensor(@PathParam("sensorId") String sensorId) {
        Sensor foundSensor = CampusDataRepository.findSensor(sensorId);
        if (foundSensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiErrorPayload(404, "Not Found",
                            "Sensor '" + sensorId + "' not found.")).build();
        }
        return Response.ok(foundSensor).build();
    }

    @DELETE
    @Path("/{sensorId}")
    public Response deregisterSensor(@PathParam("sensorId") String sensorId) {
        Sensor targetSensor = CampusDataRepository.findSensor(sensorId);
        if (targetSensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiErrorPayload(404, "Not Found",
                            "Sensor '" + sensorId + "' not found.")).build();
        }
        Room ownerRoom = CampusDataRepository.findRoom(targetSensor.getRoomId());
        if (ownerRoom != null) {
            ownerRoom.getSensorIds().remove(sensorId);
        }
        CampusDataRepository.removeSensor(sensorId);
        return Response.noContent().build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource delegateToReadings(@PathParam("sensorId") String sensorId) {
        Sensor existingSensor = CampusDataRepository.findSensor(sensorId);
        if (existingSensor == null) {
            throw new NotFoundException("Sensor '" + sensorId + "' not found.");
        }
        return new SensorReadingResource(sensorId);
    }
}
