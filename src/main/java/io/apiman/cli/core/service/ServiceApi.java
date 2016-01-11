package io.apiman.cli.core.service;

import io.apiman.cli.core.service.model.Service;
import io.apiman.cli.core.service.model.ServiceConfig;
import retrofit.client.Response;
import retrofit.http.*;

import java.util.List;

/**
 * @author Pete
 */
public interface ServiceApi {
    @POST("/organizations/{orgName}/services")
    Response create(@Path("orgName") String orgName, @Body Service service);

    @GET("/organizations/{orgName}/services")
    List<Service> list();

    @GET("/organizations/{orgName}/services/{serviceName}/versions/{version}")
    Service fetch(@Path("orgName") String orgName, @Path("serviceName") String serviceName, @Path("version") String version);

    @PUT("/organizations/{orgName}/services/{serviceName}/versions/{version}")
    Response configure(@Path("orgName") String orgName, @Path("serviceName") String serviceName, @Path("version") String version, @Body ServiceConfig config);
}
