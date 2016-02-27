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

package io.apiman.cli.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.CharStreams;
import io.apiman.cli.core.declarative.model.Declaration;
import io.apiman.cli.exception.DeclarativeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

/**
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
    public static Declaration loadDeclaration(Path path, ObjectMapper mapper, Collection<String> properties) {
        try (InputStream is = Files.newInputStream(path)) {
            String fileContents = CharStreams.toString(new InputStreamReader(is));
            LOGGER.trace("Declaration file raw: {}", fileContents);

            fileContents = BeanUtil.resolvePlaceholders(fileContents, properties);
            LOGGER.trace("Declaration file after resolving placeholders: {}", fileContents);

            return mapper.readValue(fileContents, Declaration.class);

        } catch (IOException e) {
            throw new DeclarativeException(e);
        }
    }
}
