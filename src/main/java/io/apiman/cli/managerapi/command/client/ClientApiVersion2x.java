package io.apiman.cli.managerapi.command.client;

import io.apiman.cli.command.api.model.ApiPolicy;
import io.apiman.cli.command.api.model.EntityVersion;
import io.apiman.cli.command.client.model.ApiKey;
import io.apiman.cli.command.client.model.Client;
import io.apiman.cli.command.client.model.Contract;
import io.apiman.manager.api.beans.clients.ClientVersionBean;
import io.apiman.manager.api.beans.clients.NewClientVersionBean;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

import java.util.List;

/**
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
public interface ClientApiVersion2x {

    @GET("/organizations/{orgName}/clients")
    List<Client> list(@Path("orgName") String orgName);

    @GET("/organizations/{orgName}/clients/{clientName}/versions")
    List<Client> listVersions(@Path("orgName") String orgName, @Path("clientName") String clientName);

    @POST("/organizations/{orgName}/clients")
    Response create(@Path("orgName") String orgName, @Body Client client);

    @GET("/organizations/{orgName}/clients/{clientName}")
    Client fetch(@Path("orgName") String orgName, @Path("clientName") String clientName);

    @POST("/organizations/{orgName}/clients/{clientName}/versions")
    Client createVersion(@Path("orgName") String orgName, @Path("clientName") String clientName,
                         @Body NewClientVersionBean client);

    @POST("/organizations/{orgName}/clients/{clientName}/versions")
    Client createVersion(@Path("orgName") String orgName, @Path("clientName") String clientName,
                         @Body EntityVersion client);

    @GET("/organizations/{orgName}/clients/{clientName}/versions/{version}")
    ClientVersionBean fetchVersion(@Path("orgName") String orgName, @Path("clientName") String clientName,
                                   @Path("version") String version);

    @GET("/organizations/{orgName}/clients/{clientName}/versions/{version}/apikey")
    ApiKey getApiKey(@Path("orgName") String orgName, @Path("clientName") String clientName,
                     @Path("version") String version);

    @POST("/organizations/{orgName}/clients/{clientName}/versions/{version}/contracts")
    Response createContract(@Path("orgName") String orgName, @Path("clientName") String clientName,
                            @Path("version") String version, @Body Contract contract);

    @GET("/organizations/{orgName}/clients/{clientName}/versions/{version}/contracts")
    List<Contract> listContracts(@Path("orgName") String orgName, @Path("clientName") String clientName,
                                 @Path("version") String version);


    @POST("/organizations/{orgName}/clients/{clientName}/versions/{version}/policies")
    Response addPolicy(@Path("orgName") String orgName, @Path("clientName") String clientName,
                       @Path("version") String version, @Body ApiPolicy policyConfig);

    @GET("/organizations/{orgName}/clients/{clientName}/versions/{version}/policies")
    List<ApiPolicy> fetchPolicies(@Path("orgName") String orgName, @Path("clientName") String clientName,
                                  @Path("version") String version);

    @GET("/organizations/{orgName}/clients/{clientName}/versions/{version}/policies/{policyId}")
    ApiPolicy fetchPolicy(@Path("orgName") String orgName, @Path("clientName") String clientName,
                          @Path("version") String version, @Path("policyId") Long policyId);

    @PUT("/organizations/{orgName}/clients/{clientName}/versions/{version}/policies/{policyId}")
    Response configurePolicy(@Path("orgName") String orgName, @Path("clientName") String clientName,
                             @Path("version") String version, @Path("policyId") Long policyId, @Body ApiPolicy policyConfig);
}
