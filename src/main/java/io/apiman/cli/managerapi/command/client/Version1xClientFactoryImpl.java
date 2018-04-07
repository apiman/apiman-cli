package io.apiman.cli.managerapi.command.client;

import io.apiman.cli.command.api.model.ApiPolicy;
import io.apiman.cli.command.api.model.EntityVersion;
import io.apiman.cli.command.client.model.ApiKey;
import io.apiman.cli.command.client.model.Client;
import io.apiman.cli.command.client.model.Contract;
import io.apiman.cli.managerapi.management.factory.AbstractManagementApiFactory;
import io.apiman.cli.managerapi.management.factory.ManagementApiFactory;
import io.apiman.manager.api.beans.clients.ClientVersionBean;
import io.apiman.manager.api.beans.clients.NewClientVersionBean;
import retrofit.client.Response;

import java.util.List;

/**
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
public class Version1xClientFactoryImpl
        extends AbstractManagementApiFactory<ClientApi, ClientApiVersion1x>
        implements ManagementApiFactory<ClientApi> {

    @Override
    public ClientApi build(String endpoint, String username, String password, boolean debugLogging) {
        final ClientApiVersion1x delegate = buildClient(ClientApiVersion1x.class, endpoint, username, password, debugLogging);

        return new ClientApi() {

            @Override
            public Response addPolicy(String orgName, String entityName, String version, ApiPolicy policyConfig) {
                return delegate.addPolicy(orgName, entityName, version, policyConfig);
            }

            @Override
            public ApiPolicy fetchPolicy(String orgName, String entityName, String version, Long policyId) {
                return delegate.fetchPolicy(orgName, entityName, version, policyId);
            }

            @Override
            public Response configurePolicy(String orgName, String entityName, String apiVersion, Long policyId, ApiPolicy policyConfig) {
                return delegate.configurePolicy(orgName, entityName, apiVersion, policyId, policyConfig);
            }

            @Override
            public List<ApiPolicy> fetchPolicies(String orgName, String entityName, String version) {
                return delegate.fetchPolicies(orgName, entityName, version);
            }

            @Override
            public List<Client> list(String orgName) {
                return delegate.list(orgName);
            }

            @Override
            public List<Client> listVersions(String orgName, String clientName) {
                return delegate.listVersions(orgName, clientName);
            }

            @Override
            public Response create(String orgName, Client client) {
                return delegate.create(orgName, client);
            }

            @Override
            public Client fetch(String orgName, String clientName) {
                return delegate.fetch(orgName, clientName);
            }

            @Override
            public Client createVersion(String orgName, String clientName, EntityVersion client) {
                return delegate.createVersion(orgName, clientName, client);
            }

            public Client createVersion(String orgName, String clientName, NewClientVersionBean client) {
                return delegate.createVersion(orgName, clientName, client);
            }

            @Override
            public ClientVersionBean fetchVersion(String orgName, String clientName, String version) {
                return delegate.fetchVersion(orgName, clientName, version);
            }

            @Override
            public ApiKey getApiKey(String orgName, String clientName, String version) {
                return delegate.getApiKey(orgName, clientName, version);
            }

            @Override
            public Response createContract(String orgName, String clientName, String version, Contract contract) {
                return delegate.createContract(orgName, clientName, version, contract);
            }

            @Override
            public List<Contract> listContracts(String orgName, String clientName, String version) {
                return delegate.listContracts(orgName, clientName, version);
            }
        };

    }
}
