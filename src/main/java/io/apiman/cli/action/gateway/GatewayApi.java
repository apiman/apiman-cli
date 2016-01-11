package io.apiman.cli.action.gateway;

import io.apiman.cli.model.Gateway;
import io.apiman.cli.model.GatewayTestResponse;
import retrofit.client.Response;
import retrofit.http.*;

import java.util.List;

/**
 * @author Pete
 */
public interface GatewayApi {
    @POST("/gateways")
    Response create(@Body Gateway gateway);

    @GET("/gateways")
    List<Gateway> list();

    @GET("/gateways/{gatewayId}")
    Gateway fetch(@Path("gatewayId") String gatewayId);

    @PUT("/gateways")
    GatewayTestResponse test(@Body Gateway gateway);
}
