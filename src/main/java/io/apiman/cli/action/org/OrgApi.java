package io.apiman.cli.action.org;

import io.apiman.cli.model.Org;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * @author Pete
 */
public interface OrgApi<T extends Org> {
    @POST("/organizations")
    Response create(@Body T organisation);

    @GET("/organizations/{organizationId}")
    Response fetch(@Path("organizationId") String organizationId);
}
