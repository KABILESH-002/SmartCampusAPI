package com.smartcampus.config;

import com.smartcampus.logging.RequestResponseLogger;
import com.smartcampus.mapper.CatchAllExceptionMapper;
import com.smartcampus.mapper.LinkedRoomMissingExceptionMapper;
import com.smartcampus.mapper.RoomHasSensorsExceptionMapper;
import com.smartcampus.mapper.SensorOfflineExceptionMapper;
import com.smartcampus.resource.ApiDiscoveryResource;
import com.smartcampus.resource.RoomResource;
import com.smartcampus.resource.SensorResource;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class CampusApiApplication extends ResourceConfig {

    public CampusApiApplication() {
        register(ApiDiscoveryResource.class);
        register(RoomResource.class);
        register(SensorResource.class);

        register(RoomHasSensorsExceptionMapper.class);
        register(LinkedRoomMissingExceptionMapper.class);
        register(SensorOfflineExceptionMapper.class);
        register(CatchAllExceptionMapper.class);

        register(RequestResponseLogger.class);
        register(JacksonFeature.class);
    }
}
