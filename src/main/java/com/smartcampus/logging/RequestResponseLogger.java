package com.smartcampus.logging;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

@Provider
public class RequestResponseLogger implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(RequestResponseLogger.class.getName());

    @Override
    public void filter(ContainerRequestContext requestCtx) throws IOException {
        LOG.info(String.format("[IN]  %s %s",
                requestCtx.getMethod(),
                requestCtx.getUriInfo().getRequestUri()));
    }

    @Override
    public void filter(ContainerRequestContext requestCtx,
                       ContainerResponseContext responseCtx) throws IOException {
        LOG.info(String.format("[OUT] %s %s -> HTTP %d",
                requestCtx.getMethod(),
                requestCtx.getUriInfo().getRequestUri(),
                responseCtx.getStatus()));
    }
}
