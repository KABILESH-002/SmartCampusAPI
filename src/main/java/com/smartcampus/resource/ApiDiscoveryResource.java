package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class ApiDiscoveryResource {

    @GET
    public Response discover() {
        Map<String, Object> discoveryPayload = new HashMap<>();

        discoveryPayload.put("api", "Smart Campus Sensor & Room Management API");
        discoveryPayload.put("version", "1.0.0");
        discoveryPayload.put("description", "RESTful API for managing campus rooms and IoT sensors.");

        Map<String, String> contactDetails = new HashMap<>();
        contactDetails.put("name", "Campus Facilities Admin");
        contactDetails.put("email", "admin@smartcampus.ac.uk");
        discoveryPayload.put("contact", contactDetails);

        Map<String, String> resourceLinks = new HashMap<>();
        resourceLinks.put("rooms", "/api/v1/rooms");
        resourceLinks.put("sensors", "/api/v1/sensors");
        discoveryPayload.put("resources", resourceLinks);

        discoveryPayload.put("timestamp", System.currentTimeMillis());

        return Response.ok(discoveryPayload).build();
    }
}
