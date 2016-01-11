package io.apiman.cli.core.common;

import io.apiman.cli.core.common.model.ApimanAction;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * @author Pete
 */
public interface ActionApi {
    @POST("/actions")
    Response doAction(@Body ApimanAction action);
}
