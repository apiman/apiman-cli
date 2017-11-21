///*
// * Copyright 2017 Pete Cornish
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *    http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package io.apiman.cli.command.declarative.command;
//
//import io.apiman.cli.command.common.model.ManagementApiVersion;
//import io.apiman.cli.command.core.AbstractFinalCommand;
//import io.apiman.cli.command.declarative.DeclarativeUtil;
//import io.apiman.cli.command.declarative.model.Declaration;
//import io.apiman.cli.exception.CommandException;
//import io.apiman.cli.service.DeclarativeService;
//import io.apiman.cli.service.ManagementApiService;
//import io.apiman.cli.service.PluginService;
//import io.apiman.cli.util.BeanUtil;
//import io.apiman.cli.util.MappingUtil;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.kohsuke.args4j.CmdLineParser;
//import org.kohsuke.args4j.Option;
//
//import javax.inject.Inject;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.StandardOpenOption;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//
//import static java.util.Optional.ofNullable;
//
///**
// * Applies an API environment declaration.
// *
// * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
// */
//public class ApplyCommand extends AbstractFinalCommand {
//    private static final Logger LOGGER = LogManager.getLogger(ApplyCommand.class);
//    private static final String JSON_EXTENSION = ".json";
//
//    @Option(name = "--declarationFile", aliases = {"-f"}, usage = "Declaration file")
//    private Path declarationFile;
//
//    @Option(name = "-P", usage = "Set property (key=value)")
//    private List<String> properties;
//
//    @Option(name = "--propertiesFile", usage = "Properties file")
//    private List<Path> propertiesFiles;
//
//    @Option(name = "--serverVersion", aliases = {"-sv"}, usage = "Management API server version")
//    private ManagementApiVersion serverVersion = ManagementApiVersion.DEFAULT_VERSION;
//
//    private DeclarativeService declarativeService;
//    private PluginService pluginService;
//
//    @Inject
//    public ApplyCommand(ManagementApiService managementApiService,
//                        DeclarativeService declarativeService, PluginService pluginService) {
//
//        super(managementApiService);
//        this.declarativeService = declarativeService;
//        this.pluginService = pluginService;
//    }
//
//    @Override
//    protected String getCommandDescription() {
//        return "Apply declaration";
//    }
//
//    @Override
//    public void performAction(CmdLineParser parser) throws CommandException {
//        applyDeclaration();
//    }
//
//    /**
//     * Load and then apply the Declaration.
//     */
//    void applyDeclaration() {
//        final Map<String, String> parsedProperties = loadProperties();
//
//        // parse declaration
//        final Declaration declaration;
//
//        if (declarationFile.endsWith(JSON_EXTENSION)) {
//            declaration = DeclarativeUtil.loadDeclaration(declarationFile, MappingUtil.JSON_MAPPER, parsedProperties);
//        } else {
//            // default is YAML
//            declaration = DeclarativeUtil.loadDeclaration(declarationFile, MappingUtil.YAML_MAPPER, parsedProperties);
//        }
//
//        LOGGER.info("Loaded declaration: {}", declarationFile);
//        LOGGER.debug("Declaration loaded: {}", () -> MappingUtil.safeWriteValueAsJson(declaration));
//
//        try {
//            applyDeclaration(declaration);
//        } catch (Exception e) {
//            throw new CommandException("Error applying declaration", e);
//        }
//    }
//
//    /**
//     * Load the properties from command line arguments and/or file.
//     *
//     * @return the properties.
//     */
//    private Map<String, String> loadProperties() {
//        final Map<String, String> parsedProperties = BeanUtil.parseReplacements(properties);
//
//        // check for properties file
//        ofNullable(propertiesFiles).ifPresent(propertiesFiles -> propertiesFiles.forEach(propertiesFile -> {
//            LOGGER.trace("Loading properties file: {}", propertiesFile);
//
//            final Properties fileProperties = new Properties();
//            try (final InputStream propertiesIn = Files.newInputStream(propertiesFile, StandardOpenOption.READ)) {
//                if (propertiesFile.toAbsolutePath().toString().toLowerCase().endsWith(".xml")) {
//                    fileProperties.loadFromXML(propertiesIn);
//                } else {
//                    fileProperties.load(propertiesIn);
//                }
//            } catch (IOException e) {
//                throw new CommandException(String.format("Error loading properties file: %s", propertiesFile), e);
//            }
//
//            fileProperties.forEach((key, value) -> parsedProperties.put((String) key, (String) value));
//        }));
//
//        return parsedProperties;
//    }
//
//    /**
//     * Apply the given Declaration.
//     *
//     * @param declaration the Declaration to apply.
//     */
//    private void applyDeclaration(Declaration declaration) {
//        LOGGER.debug("Applying declaration");
//
//        // add gateways
//        ofNullable(declaration.getSystem().getGateways()).ifPresent(declarativeService::applyGateways);
//
//        // add plugins
//        ofNullable(declaration.getSystem().getPlugins()).ifPresent(pluginService::addPlugins);
//
//        // add org and APIs
//        ofNullable(declaration.getOrg()).ifPresent(org -> {
//            declarativeService.applyOrg(org);
//
//            ofNullable(org.getApis()).ifPresent(apis ->
//                    declarativeService.applyApis(serverVersion, apis, org.getName()));
//        });
//
//        LOGGER.info("Applied declaration");
//    }
//
//    public void setDeclarationFile(Path declarationFile) {
//        this.declarationFile = declarationFile;
//    }
//
//    public void setProperties(List<String> properties) {
//        this.properties = properties;
//    }
//
//    public void setPropertiesFiles(List<Path> propertiesFiles) {
//        this.propertiesFiles = propertiesFiles;
//    }
//
//    public void setServerAddress(String serverAddress) {
//        this.serverAddress = serverAddress;
//    }
//
//    public void setServerVersion(ManagementApiVersion serverVersion) {
//        this.serverVersion = serverVersion;
//    }
//}
