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
import io.apiman.cli.command.declarative.model.SharedItems;
import io.apiman.cli.core.declarative.model.BaseDeclaration;
import io.apiman.cli.exception.DeclarativeException;
import io.apiman.cli.util.BeanUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

import static java.util.Optional.ofNullable;

/**
 * Shared utility functions for API declarations.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class DeclarativeUtil {
    private static final Logger LOGGER = LogManager.getLogger(DeclarativeUtil.class);

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

            return declaration;

        } catch (IOException e) {
            throw new DeclarativeException(e);
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
}
