package io.apiman.cli.managerapi.command.api;

import io.apiman.cli.command.api.model.ApiPolicy;
import retrofit.client.Response;

import java.util.List;

/**
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
public interface PolicyApi {

    Response addPolicy(String orgName, String entityName,
                       String version, ApiPolicy policyConfig);

    ApiPolicy fetchPolicy(String orgName, String entityName,
                          String version, Long policyId);

    Response configurePolicy(String orgName, String entityName,
                             String apiVersion, Long policyId, ApiPolicy policyConfig);

    List<ApiPolicy> fetchPolicies(String orgName, String entityName,
                                  String version);
}
