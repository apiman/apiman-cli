package io.apiman.cli.action.org;

import io.apiman.cli.model.Organisation;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * @author Pete
 */
public interface OrgApi {
    @POST("/organizations")
    Response create(@Body Organisation organisation);

    @GET("/organizations/{orgId}")
    Organisation fetch(@Path("orgId") String orgId);
}
