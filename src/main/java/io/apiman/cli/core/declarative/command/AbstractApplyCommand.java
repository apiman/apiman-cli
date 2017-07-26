/*
 * Copyright 2016 Pete Cornish
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apiman.cli.core.declarative.command;

import static java.util.Optional.ofNullable;

import io.apiman.cli.command.AbstractFinalCommand;
import io.apiman.cli.core.declarative.model.BaseDeclaration;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.util.BeanUtil;
import io.apiman.cli.util.DeclarativeUtil;
import io.apiman.cli.util.MappingUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * Applies an API environment declaration.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public abstract class AbstractApplyCommand extends AbstractFinalCommand {
    private static final Logger LOGGER = LogManager.getLogger(AbstractApplyCommand.class);
    protected static final String JSON_EXTENSION = ".json";

    @Option(name = "--declarationFile", aliases = {"-f"}, usage = "Declaration file")
    protected Path declarationFile;

    @Option(name = "-P", usage = "Set property (key=value)")
    protected List<String> properties;

    @Option(name = "--propertiesFile", usage = "Properties file")
    protected List<Path> propertiesFiles;

    @Override
    protected String getCommandDescription() {
        return "Apply declaration";
    }

    @Override
    public void performAction(CmdLineParser parser) throws CommandException {
        try {
            applyDeclaration(loadDeclaration());
        } catch (Exception e) {
            throw new CommandException("Error applying declaration", e);
        }
    }

    /**
     * Load and then apply the Declaration.
     */
    private BaseDeclaration loadDeclaration() {
        final Map<String, String> parsedProperties = BeanUtil.parseReplacements(properties);

        // check for properties file
        ofNullable(propertiesFiles).ifPresent(propertiesFiles -> propertiesFiles.forEach(propertiesFile -> {
            LOGGER.trace("Loading properties file: {}", propertiesFile);

            final Properties fileProperties = new Properties();
            try (final InputStream propertiesIn = Files.newInputStream(propertiesFile, StandardOpenOption.READ)) {
                if (propertiesFile.toAbsolutePath().toString().toLowerCase().endsWith(".xml")) {
                    fileProperties.loadFromXML(propertiesIn);
                } else {
                    fileProperties.load(propertiesIn);
                }
            } catch (IOException e) {
                throw new CommandException(String.format("Error loading properties file: %s", propertiesFile), e);
            }

            fileProperties.forEach((key, value) -> parsedProperties.put((String) key, (String) value));
        }));

        final BaseDeclaration declaration = loadDeclaration(declarationFile, parsedProperties);
        LOGGER.info("Loaded declaration: {}", declarationFile);
        LOGGER.debug("Declaration loaded: {}", () -> MappingUtil.safeWriteValueAsJson(declaration));
        return declaration;
    }

    public void applyDeclaration() {
        applyDeclaration(loadDeclaration());
    }

    protected abstract void applyDeclaration(BaseDeclaration declaration);

    protected BaseDeclaration loadDeclaration(Path declarationFile, Map<String, String> parsedProperties) {
      // parse declaration
      if (declarationFile.endsWith(JSON_EXTENSION)) {
          return DeclarativeUtil.loadDeclaration(declarationFile, MappingUtil.JSON_MAPPER, parsedProperties);
      } else {
          // default is YAML
          return DeclarativeUtil.loadDeclaration(declarationFile, MappingUtil.YAML_MAPPER, parsedProperties);
      }
    }

    public void setDeclarationFile(Path declarationFile) {
        this.declarationFile = declarationFile;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }

    public void setPropertiesFiles(List<Path> propertiesFiles) {
        this.propertiesFiles = propertiesFiles;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }
}
