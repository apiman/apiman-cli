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

package io.apiman.cli.command.declarative.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import io.apiman.cli.command.core.AbstractFinalCommand;
import io.apiman.cli.command.declarative.DeclarativeUtil;
import io.apiman.cli.command.declarative.model.BaseDeclaration;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.services.WaitService;
import io.apiman.cli.util.MappingUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Applies an API environment declaration.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@Parameters(commandDescription = "Apply declaration")
public abstract class AbstractApplyCommand extends AbstractFinalCommand {
    private static final Logger LOGGER = LogManager.getLogger(AbstractApplyCommand.class);
    protected static final String JSON_EXTENSION = ".json";

    @Parameter(names = {"--declarationFile", "-f"}, description = "Declaration file")
    protected List<Path> declarationFiles;

    @Parameter(names = "-P", description = "Set property (key=value)")
    protected List<String> properties;

    @Parameter(names = "--propertiesFile", description = "Properties file")
    protected List<Path> propertiesFiles;

    public AbstractApplyCommand(WaitService waitService) {
        super(waitService);
    }

    public AbstractApplyCommand() {
        super();
    }

    @Override
    public void performFinalAction(JCommander parser) throws CommandException {
        try {
            applyDeclarations();
        } catch (Exception e) {
            throw new CommandException("Error applying declaration", e);
        }
    }

    /**
     * @return load all the {@link #declarationFiles}
     */
    private List<BaseDeclaration> loadDeclarations() {
        return declarationFiles.stream()
                .map(this::loadDeclaration)
                .collect(Collectors.toList());
    }

    /**
     * Load and then apply the Declaration.
     *
     * @param declarationFile
     */
    private BaseDeclaration loadDeclaration(Path declarationFile) {
        final Map<String, String> parsedProperties = DeclarativeUtil.parseProperties(properties, propertiesFiles);
        final BaseDeclaration declaration = loadDeclaration(declarationFile, parsedProperties);
        LOGGER.info("Loaded declaration: {}", declarationFile);
        LOGGER.debug("Declaration loaded: {}", () -> MappingUtil.safeWriteValueAsJson(declaration));
        return declaration;
    }

    public void applyDeclarations() {
        applyDeclarations(loadDeclarations());
    }

    protected abstract void applyDeclarations(List<BaseDeclaration> declaration);

    protected BaseDeclaration loadDeclaration(Path declarationFile, Map<String, String> parsedProperties) {
        // parse declaration
        if (declarationFile.endsWith(JSON_EXTENSION)) {
            return DeclarativeUtil.loadDeclaration(declarationFile, MappingUtil.JSON_MAPPER, parsedProperties);
        } else {
            // default is YAML
            return DeclarativeUtil.loadDeclaration(declarationFile, MappingUtil.YAML_MAPPER, parsedProperties);
        }
    }

    public void setDeclarationFiles(List<Path> declarationFiles) {
        this.declarationFiles = declarationFiles;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }

    public void setPropertiesFiles(List<Path> propertiesFiles) {
        this.propertiesFiles = propertiesFiles;
    }
}
