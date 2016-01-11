package io.apiman.cli.core.service.action;

import io.apiman.cli.api.action.common.AbstractModelAction;
import io.apiman.cli.api.exception.ActionException;
import io.apiman.cli.core.common.ActionApi;
import io.apiman.cli.core.common.model.ApimanAction;
import io.apiman.cli.core.service.ServiceApi;
import io.apiman.cli.core.service.ServiceMixin;
import io.apiman.cli.core.service.model.Service;
import io.apiman.cli.util.ApiUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.net.HttpURLConnection;
import java.text.MessageFormat;

/**
 * @author Pete
 */
public class ServicePublishAction extends AbstractModelAction<Service, ServiceApi> implements ServiceMixin {

    private static final Logger LOGGER = LogManager.getLogger(ServicePublishAction.class);

    @Option(name = "--name", aliases = {"-n"}, usage = "Service name", required = true)
    private String name;

    @Option(name = "--version", aliases = {"-v"}, usage = "Service version", required = true)
    private String version;

    @Option(name = "--orgName", aliases = {"-o"}, usage = "Organisation name", required = true)
    private String orgName;

    @Override
    protected String getActionName() {
        return MessageFormat.format("Create {0}", getModelName());
    }

    @Override
    protected void performAction(CmdLineParser parser) throws ActionException {
        LOGGER.debug("Publishing {}", this::getModelName);

        final ActionApi apiClient = buildApiClient(ActionApi.class);

        ApiUtil.invokeAndCheckResponse(HttpURLConnection.HTTP_NO_CONTENT, () -> {
            final ApimanAction action = new ApimanAction(
                    "publishService",
                    orgName,
                    name,
                    version
            );

            return apiClient.doAction(action);
        });
    }
}
