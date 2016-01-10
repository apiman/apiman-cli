package io.apiman.cli.action.gateway;

import io.apiman.cli.model.Gateway;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

import java.util.List;

/**
 * @author Pete
 */
public interface GatewayApi<T extends Gateway> {
    @POST("/gateways")
    Response create(@Body T organisation);

    @GET("/gateways")
    List<T> list();

    @GET("/gateways/{gatewayId}")
    Response fetch(@Path("gatewayId") String gatewayId);
}
