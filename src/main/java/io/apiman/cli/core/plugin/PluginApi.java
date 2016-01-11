package io.apiman.cli.core.plugin;

import io.apiman.cli.core.plugin.model.Plugin;
import retrofit.client.Response;
import retrofit.http.*;

import java.util.List;

/**
 * @author Pete
 */
public interface PluginApi {
    @POST("/plugins")
    Response create(@Body Plugin plugin);

    @GET("/plugins")
    List<Plugin> list();

    @GET("/plugins/{pluginId}")
    Plugin fetch(@Path("pluginId") String pluginId);
}
