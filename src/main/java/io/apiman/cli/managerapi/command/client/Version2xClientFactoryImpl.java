package io.apiman.cli.managerapi.command.client;

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
public class Version2xClientFactoryImpl
        extends AbstractManagementApiFactory<ClientApi, ClientApiVersion2x>
        implements ManagementApiFactory<ClientApi> {

    @Override
    public ClientApi build(String endpoint, String username, String password, boolean debugLogging) {
        final ClientApiVersion2x delegate = buildClient(ClientApiVersion2x.class, endpoint, username, password, debugLogging);

        return new ClientApi() {

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
