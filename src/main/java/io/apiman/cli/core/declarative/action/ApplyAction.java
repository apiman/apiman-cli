package io.apiman.cli.core.declarative.action;

import com.google.common.collect.Lists;
import io.apiman.cli.action.AbstractFinalAction;
import io.apiman.cli.core.api.ServiceApi;
import io.apiman.cli.core.api.model.Api;
import io.apiman.cli.core.api.model.ApiGateway;
import io.apiman.cli.core.api.model.ApiPolicy;
import io.apiman.cli.core.api.model.ServiceConfig;
import io.apiman.cli.core.common.ActionApi;
import io.apiman.cli.core.common.model.ServerAction;
import io.apiman.cli.core.declarative.model.Declaration;
import io.apiman.cli.core.gateway.GatewayApi;
import io.apiman.cli.core.gateway.model.Gateway;
import io.apiman.cli.core.org.OrgApi;
import io.apiman.cli.core.org.model.Org;
import io.apiman.cli.core.plugin.PluginApi;
import io.apiman.cli.exception.ActionException;
import io.apiman.cli.exception.DeclarativeException;
import io.apiman.cli.util.JsonUtil;
import io.apiman.cli.util.YamlUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Optional.ofNullable;

/**
 * @author pete
 */
public class ApplyAction extends AbstractFinalAction {
    private static final Logger LOGGER = LogManager.getLogger(ApplyAction.class);

    @Option(name = "--declarationFile", aliases = {"-f"}, usage = "Declaration file")
    private Path declarationFile;

    @Override
    protected String getActionName() {
        return "Apply declaration";
    }

    @Override
    public void performAction(CmdLineParser parser) throws ActionException {
        final Declaration declaration;

        // parse declaration
        if (declarationFile.endsWith(".json")) {
            declaration = loadDeclarationJson(declarationFile);
        } else {
            // default is YAML
            declaration = loadDeclarationYaml(declarationFile);
        }

        LOGGER.info("Loaded declaration: {}", declarationFile);
        LOGGER.debug("Declaration loaded: {}", () -> JsonUtil.safeWriteValueAsString(declaration));

        applyDeclaration(declaration);
    }

    public void applyDeclaration(Declaration declaration) {
        LOGGER.debug("Applying declaration");

        // add gateways
        ofNullable(declaration.getSystem().getGateways()).ifPresent(gateways -> {
            LOGGER.debug("Adding gateways");

            gateways.forEach(declarativeGateway -> {
                LOGGER.info("Adding gateway: {}", declarativeGateway.getName());

                final Gateway gateway = copy(declarativeGateway, Gateway.class);
                gateway.setConfiguration(JsonUtil.safeWriteValueAsString(declarativeGateway.getConfig()));
                buildApiClient(GatewayApi.class).create(gateway);
            });
        });

        // add plugins
        ofNullable(declaration.getSystem().getPlugins()).ifPresent(plugins -> {
            LOGGER.debug("Adding plugins");

            plugins.forEach(plugin -> {
                LOGGER.info("Adding plugin: {}", plugin.getName());
                buildApiClient(PluginApi.class).create(plugin);
            });
        });

        // add org
        ofNullable(declaration.getOrg()).ifPresent(declarativeOrg -> {
            final String orgName = declaration.getOrg().getName();
            LOGGER.info("Adding org: {}", orgName);
            buildApiClient(OrgApi.class).create(copy(declaration.getOrg(), Org.class));

            // add apis
            ofNullable(declaration.getOrg().getApis()).ifPresent(declarativeApis -> {
                LOGGER.debug("Adding APIs");

                declarativeApis.forEach(declarativeApi -> {
                    LOGGER.info("Adding API: {}", declarativeApi.getName());

                    // create API
                    final Api api = copy(declarativeApi, Api.class);
                    final ServiceApi apiClient = buildApiClient(ServiceApi.class);
                    apiClient.create(orgName, api);

                    // configure API
                    LOGGER.info("Configuring API: {}", declarativeApi.getName());
                    final ServiceConfig apiConfig = copy(declarativeApi.getConfig(), ServiceConfig.class);
                    apiConfig.setGateways(Lists.newArrayList(new ApiGateway(declarativeApi.getConfig().getGateway())));
                    apiClient.configure(orgName, api.getName(), api.getInitialVersion(), apiConfig);

                    // add policies
                    ofNullable(declarativeApi.getPolicies()).ifPresent(declarativePolicies -> {
                        LOGGER.debug("Adding policies to API: {}", api.getName());

                        declarativePolicies.forEach(declarativePolicy -> {
                            LOGGER.info("Adding policy '{}' to API: {}", declarativePolicy.getName(), api.getName());

                            // add policy
                            final ApiPolicy apiPolicy = new ApiPolicy(
                                    declarativePolicy.getName(),
                                    JsonUtil.safeWriteValueAsString(declarativePolicy.getConfig()));

                            apiClient.addPolicy(orgName, api.getName(), api.getInitialVersion(), apiPolicy);
                        });
                    });

                    // publish API
                    if (declarativeApi.isPublished()) {
                        LOGGER.info("Publishing API: {}", api.getName());

                        final ServerAction serverAction = new ServerAction(
                                "publishService",
                                orgName,
                                api.getName(),
                                api.getInitialVersion());

                        buildApiClient(ActionApi.class).doAction(serverAction);
                    }
                });
            });
        });
    }

    private <D, O> D copy(O original, Class<D> destinationClass) {
        try {
            final D destination = destinationClass.newInstance();

            final ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration()
                    .setFieldMatchingEnabled(true)
                    .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);

            modelMapper.map(original, destination);

            return destination;

        } catch (InstantiationException | IllegalAccessException e) {
            throw new DeclarativeException(e);
        }
    }

    public Declaration loadDeclarationJson(Path path) {
        try (InputStream is = Files.newInputStream(path)) {
            return JsonUtil.MAPPER.readValue(is, Declaration.class);
        } catch (IOException e) {
            throw new DeclarativeException(e);
        }
    }

    public Declaration loadDeclarationYaml(Path path) {
        try (InputStream is = Files.newInputStream(path)) {
            return YamlUtil.MAPPER.readValue(is, Declaration.class);
        } catch (IOException e) {
            throw new DeclarativeException(e);
        }
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }
}
