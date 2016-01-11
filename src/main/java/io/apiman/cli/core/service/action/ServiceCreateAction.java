package io.apiman.cli.core.service.action;

import com.google.common.collect.Lists;
import io.apiman.cli.api.action.common.AbstractModelAction;
import io.apiman.cli.api.exception.ActionException;
import io.apiman.cli.core.service.ServiceApi;
import io.apiman.cli.core.service.ServiceMixin;
import io.apiman.cli.core.service.model.Service;
import io.apiman.cli.core.service.model.ServiceConfig;
import io.apiman.cli.core.service.model.ServiceGateway;
import io.apiman.cli.util.ApiUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.text.MessageFormat;

/**
 * @author Pete
 */
public class ServiceCreateAction extends AbstractModelAction<Service, ServiceApi> implements ServiceMixin {

    private static final Logger LOGGER = LogManager.getLogger(ServiceCreateAction.class);

    @Option(name = "--orgName", aliases = {"-o"}, usage = "Organisation name", required = true)
    private String orgName;

    @Option(name = "--name", aliases = {"-n"}, usage = "Service name", required = true)
    private String name;

    @Option(name = "--initialVersion", aliases = {"-v"}, usage = "Initial version", required = true)
    private String initialVersion;

    @Option(name = "--endpoint", aliases = {"-e"}, usage = "Endpoint", required = true)
    private String endpoint;

    @Option(name = "--endpointType", aliases = {"-t"}, usage = "Endpoint type")
    private String endpointType = "rest";

    @Option(name = "--publicService", aliases = {"-p"}, usage = "Public service", required = true)
    private boolean publicService;

    @Option(name = "--gateway", aliases = {"-g"}, usage = "Gateway")
    private String gateway = "TheGateway";

    @Override
    protected String getActionName() {
        return MessageFormat.format("Create {0}", getModelName());
    }

    @Override
    protected void performAction(CmdLineParser parser) throws ActionException {
        LOGGER.debug("Creating {}", this::getModelName);

        final ServiceApi apiClient = buildApiClient(getApiClass());

        // create
        ApiUtil.invokeAndCheckResponse(() -> {
            final Service service = new Service(
                    name,
                    initialVersion);

            return apiClient.create(orgName, service);
        });

        // configure
        ApiUtil.invokeAndCheckResponse(() -> {
            final ServiceConfig config = new ServiceConfig(
                    endpoint,
                    endpointType,
                    publicService,
                    Lists.newArrayList(new ServiceGateway(gateway)));

            return apiClient.configure(orgName, name, initialVersion, config);
        });
    }
}
