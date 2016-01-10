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
public interface GatewayApi {
    @POST("/gateways")
    Response create(@Body Gateway organisation);

    @GET("/gateways")
    List<Gateway> list();

    @GET("/gateways/{gatewayId}")
    Gateway fetch(@Path("gatewayId") String gatewayId);
}
