package com.smartcampus.resource;

import com.smartcampus.exception.SensorOfflineException;
import com.smartcampus.model.ApiErrorPayload;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.repository.CampusDataRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String targetSensorId;

    public SensorReadingResource(String targetSensorId) {
        this.targetSensorId = targetSensorId;
    }

    @GET
    public Response fetchHistory() {
        List<SensorReading> history = CampusDataRepository.getReadingsBySensor(targetSensorId);
        return Response.ok(history).build();
    }

    @POST
    public Response recordReading(SensorReading incomingReading) {
        Sensor parentSensor = CampusDataRepository.findSensor(targetSensorId);

        if ("MAINTENANCE".equalsIgnoreCase(parentSensor.getStatus())) {
            throw new SensorOfflineException(
                    "Sensor '" + targetSensorId + "' is currently under MAINTENANCE and cannot accept new readings."
            );
        }

        if (incomingReading == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiErrorPayload(400, "Bad Request", "Reading body is required.")).build();
        }

        if (incomingReading.getId() == null || incomingReading.getId().isBlank()) {
            incomingReading.setId(UUID.randomUUID().toString());
        }
        if (incomingReading.getTimestamp() == 0) {
            incomingReading.setTimestamp(System.currentTimeMillis());
        }

        CampusDataRepository.saveReading(targetSensorId, incomingReading);
        parentSensor.setCurrentValue(incomingReading.getValue());

        return Response.status(Response.Status.CREATED).entity(incomingReading).build();
    }
}
