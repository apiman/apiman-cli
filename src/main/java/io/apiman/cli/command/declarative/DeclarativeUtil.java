/*
 * Copyright 2017 Pete Cornish
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

package io.apiman.cli.command.declarative;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;
import io.apiman.cli.command.declarative.model.BaseDeclaration;
import io.apiman.cli.command.declarative.model.DeclarativeOrgCommon;
import io.apiman.cli.command.declarative.model.DeclarativePolicy;
import io.apiman.cli.command.declarative.model.SharedItems;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.exception.DeclarativeException;
import io.apiman.cli.util.BeanUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

/**
 * Shared utility functions for API declarations.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class DeclarativeUtil {
    private static final Logger LOGGER = LogManager.getLogger(DeclarativeUtil.class);

    /**
     * Parse the provided properties list and combine with those in the properties files.
     *
     * @param properties      the properties in 'key=value' format
     * @param propertiesFiles files in Java Properties format
     * @return the combined properties
     */
    public static Map<String, String> parseProperties(List<String> properties, List<Path> propertiesFiles) {
        final Map<String, String> parsedProperties = BeanUtil.parseReplacements(properties);

        // check for properties file
        ofNullable(propertiesFiles).ifPresent(pf -> pf.forEach(propertiesFile -> {
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
        return parsedProperties;
    }

    /**
     * Load the Declaration from the given Path, using the mapper provided.
     *
     * @param path       the Path to the declaration
     * @param mapper     the Mapper to use
     * @param properties property placeholders to resolve
     * @return the Declaration
     */
    public static BaseDeclaration loadDeclaration(Path path, ObjectMapper mapper, Map<String, String> properties) {
        try (InputStream is = Files.newInputStream(path)) {
            String fileContents = CharStreams.toString(new InputStreamReader(is));
            LOGGER.trace("Declaration file raw: {}", fileContents);

            BaseDeclaration declaration = loadDeclaration(mapper, fileContents, properties);

            // check for the presence of shared properties in the declaration
            final Map<String, String> sharedProperties = ofNullable(declaration.getShared())
                    .map(SharedItems::getProperties)
                    .orElse(Collections.emptyMap());

            if (sharedProperties.size() > 0) {
                LOGGER.trace("Resolving {} shared placeholders", sharedProperties.size());
                final Map<String, String> mutableProperties = Maps.newHashMap(properties);
                mutableProperties.putAll(sharedProperties);

                // this is not very efficient, as it requires parsing the declaration twice
                declaration = loadDeclaration(mapper, fileContents, mutableProperties);
            }

            if (nonNull(declaration.getOrg())) {
                // ensure APIs and Policies are non-null
                if (isNull(declaration.getOrg().getApis())) {
                    declaration.getOrg().setApis(newArrayList());
                }
                declaration.getOrg().getApis().forEach(api -> {
                    if (isNull(api.getPolicies())) {
                        api.setPolicies(newArrayList());
                    }
                });
                applyCommonOrgElements(declaration);
            }

            return declaration;

        } catch (IOException e) {
            throw new DeclarativeException("Unable to load declaration: " + path, e);
        }
    }

    /**
     * Parses the {@link BaseDeclaration} from the {@code fileContents}, using the specified {@code properties}.
     *
     * @param mapper     the Mapper to use
     * @param unresolved the contents of the file
     * @param properties the property placeholders
     * @return the Declaration
     * @throws IOException
     */
    private static BaseDeclaration loadDeclaration(ObjectMapper mapper, String unresolved,
                                                   Map<String, String> properties) throws IOException {

        final String resolved = BeanUtil.resolvePlaceholders(unresolved, properties);
        LOGGER.trace("Declaration file after resolving {} placeholders: {}", properties.size(), resolved);
        return mapper.readValue(resolved, BaseDeclaration.class);
    }

    /**
     * Applies common elements such as configuration and policies to each API in the organisation.
     *
     * @param declaration the API declaration to update
     */
    private static void applyCommonOrgElements(BaseDeclaration declaration) {
        ofNullable(declaration.getOrg()).flatMap(org -> ofNullable(org.getCommon())).ifPresent(common -> {
            applyCommonConfig(declaration, common);
            applyCommonPolicies(declaration, common);
        });
    }

    private static void applyCommonConfig(BaseDeclaration declaration, DeclarativeOrgCommon common) {
        ofNullable(common.getConfig()).ifPresent(commonConfig -> {
            LOGGER.trace("Injecting common configuration into all APIs");
            declaration.getOrg().getApis().forEach(api -> {
                BeanUtil.shallowCopyToNonNullFields(commonConfig, api.getConfig());
            });
        });
    }

    private static void applyCommonPolicies(BaseDeclaration declaration, DeclarativeOrgCommon common) {
        ofNullable(common.getPolicies()).ifPresent(commonPolicies -> {
            final List<DeclarativePolicy> first = ofNullable(commonPolicies.getFirst()).orElse(emptyList());
            final List<DeclarativePolicy> last = ofNullable(commonPolicies.getLast()).orElse(emptyList());

            if (!first.isEmpty() || !last.isEmpty()) {
                LOGGER.trace("Injecting common policies into all APIs [{} first, {} last]", first.size(), last.size());
                declaration.getOrg().getApis().forEach(api -> {
                    final List<DeclarativePolicy> policies = api.getPolicies();
                    policies.addAll(0, first);
                    policies.addAll(last);
                });
            }
        });
    }
}
