/*
 * Copyright 2017 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.apiman.cli.gatewayapi.command.generate;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import io.apiman.cli.command.declarative.model.DeclarativeGateway;
import io.apiman.cli.core.declarative.command.AbstractApplyCommand;
import io.apiman.cli.core.declarative.model.BaseDeclaration;
import io.apiman.cli.gatewayapi.GatewayHelper;
import io.apiman.cli.gatewayapi.model.GatewayApiDataModel;
import io.apiman.cli.util.MappingUtil;
import io.apiman.cli.util.PolicyResolver;
import io.apiman.gateway.engine.beans.Api;
import io.apiman.gateway.engine.beans.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Generates config for API Gateway's headless JSON registry.
 *
 * For <tt>-o/--outputFile</tt>:
 * <ul>
 *  <li>If a file (or new) reference is provided, that will be used (e.g. <tt>-o /tmp/foo.json</tt>)</li>
 *  <li>If a directory is specified filenames will be inferred and written to the directory. (e.g.<tt>-o /tmp</tt>)</li>
 *  <li>If multiple configurations are generated, then multiple filenames can be provided and separated with
 *  commas: (e.g. <tt>-o /tmp/foo.json,/tmp/bar.json</tt>)</li>
 *  <li>In certain circumstances where a name cannot be inferred one will be procedurally generated</li>
 * </ul>
 *
 * @see GatewayApiDataModel
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
@Parameters(commandDescription = "Generate config for API Gateway's headless JSON registry")
public class GenerateHeadless extends AbstractApplyCommand implements GatewayHelper {
    private static final Logger LOGGER = LogManager.getLogger(GenerateHeadless.class);
    private PolicyResolver policyResolver;
    private JsonWriter jsonWriter = (outputPath, headlessConfig) -> Files.write(outputPath, headlessConfig.toJson().getBytes());

    @Parameter(names = {"--outputFile", "-o"}, description = "Output file(s) or directory. If a directory is provided a filename will be generated.")
    protected List<Path> outputFiles = new ArrayList<>();
    private int fileIndex = 0;

    @Parameter(names = {"--stdout"}, description = "Output definition to STDOUT (implied if no outputFile is specified)")
    protected boolean useStdout = false;

    @Inject
    public void setPolicyResolver(PolicyResolver policyResolver) {
        this.policyResolver = policyResolver;
    }

    /**
     * Set the JSON Writer. Mainly for testing purposes.
     *
     * @param jsonWriter the JSON writer implementation
     */
    public void setJsonWriter(JsonWriter jsonWriter) { this.jsonWriter = jsonWriter; }

    @Override
    protected void applyDeclaration(BaseDeclaration declaration) {
        GatewayApiDataModel dataModel = new GatewayApiDataModel(declaration, policyResolver);

        if (dataModel.getGatewaysMap().size() > 1) {
            LOGGER.info("{} gateway targets exist in declaration. " +
                    "Multiple configurations will be generated as a result.", dataModel.getGatewaysMap().size());
        }

        LOGGER.debug("Generating {} JSON configuration(s)", dataModel.getGatewaysMap().size());
        generateJsonConfig(dataModel);
    }

    private void generateJsonConfig(GatewayApiDataModel dataModel) {
        boolean directorySpecified = (outputFiles.size() == 1 && Files.isDirectory(outputFiles.get(0)));

        dataModel.getGatewayToApisMap().forEach((gateway, apis) -> {
            HeadlessConfigBean bean = new HeadlessConfigBean(apis, Collections.emptyList()); // TODO look up clients for the gateway

            if (useStdout || outputFiles.isEmpty()) {
                System.out.println(bean.toJson());
            } else {
                printToFile(gateway, bean, directorySpecified, fileIndex);
                fileIndex++;
            }
        });
    }

    private void printToFile(DeclarativeGateway gateway, HeadlessConfigBean config, boolean directorySpecified, int fileIndex) {
        Path directory = directorySpecified ? outputFiles.get(0) : Paths.get(System.getProperty("user.dir"));
        Path fullOutputPath;
        // If user has only provided a directory (i.e. no explicit name) or there aren't enough names provided
        // for the number of definitions being generated, then derive a filename from the gateway name.
        if (directorySpecified || fileIndex > outputFiles.size()) {
            String gatewayName = Optional.ofNullable(gateway.getName()).orElse("unnamed-config-" + fileIndex);
            String fileName = gatewayName.replaceAll("[\\s/]", "-");
            fullOutputPath = Paths.get(directory.toString(), fileName + ".json");
        } else {
            fullOutputPath = outputFiles.get(fileIndex);
        }
        try {
            jsonWriter.write(fullOutputPath, config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Ignore unused as this is to provide a template for JSON marshalling.
    @SuppressWarnings("unused")
    static final class HeadlessConfigBean {
        @JsonProperty("apis")
        private List<Api> apis;
        @JsonProperty("clients")
        private List<Client> clients;

        HeadlessConfigBean(){}

        HeadlessConfigBean(List<Api> apis, List<Client> clients) {
            this.apis = apis;
            this.clients = clients;
        }

        String toJson() {
            return MappingUtil.safeWriteValueAsJson(this);
        }
    }

    interface JsonWriter {
        void write(Path outputPath, HeadlessConfigBean headlessConfig) throws IOException;
    }
}
