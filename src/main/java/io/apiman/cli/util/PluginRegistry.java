/*
 * Copyright 2017 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apiman.cli.util;

import static java.util.Optional.ofNullable;

import io.apiman.common.plugin.Plugin;
import io.apiman.common.plugin.PluginCoordinates;
import io.apiman.manager.api.beans.policies.PolicyDefinitionBean;
import io.apiman.manager.api.core.exceptions.InvalidPluginException;
import io.apiman.manager.api.core.plugin.AbstractPluginRegistry;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.io.Files;
import com.google.common.io.Resources;


public class PluginRegistry {
    private static final PluginResolver PLUGIN_RESOLVER = new PluginResolver();

    public static final PluginResolver getResolver() {
        return PLUGIN_RESOLVER;
    }

    public static final class PluginResolver extends AbstractPluginRegistry {

        private static final Logger LOGGER = LogManager.getLogger(PluginResolver.class);
        private static final Map<String, PolicyDefinitionBean> inbuiltPolicyMap = new LinkedHashMap<>();

        public PluginResolver() {
            super(Files.createTempDir());
            buildInbuiltPolicyMap();
            LOGGER.debug("Inbuilt policy map: {}", inbuiltPolicyMap);
        }

        private void buildInbuiltPolicyMap() {
            URL policyDefResource = Resources.getResource("data/all-policyDefs.json");
            List<PolicyDefinitionBean> inbuilts = new ArrayList<>(MappingUtil.readJsonValue(policyDefResource, List.class, PolicyDefinitionBean.class));
            inbuilts.stream().forEach(policyDef -> {
                inbuiltPolicyMap.put(policyDef.getId().toLowerCase(), policyDef);
                inbuiltPolicyMap.put(policyDef.getName().toLowerCase(), policyDef);
                ofNullable(policyDef.getPolicyImpl().split("class:", 2))
                    .orElseThrow(() -> new RuntimeException("Unexpected format in built-in policyImpl field: " + policyDef.getPolicyImpl()));
                String policyFqdn = policyDef.getPolicyImpl().split("class:", 2)[1];
                inbuiltPolicyMap.put(policyFqdn.toLowerCase(), policyDef);
            });
        }

        /**
         * We need the FQCN for the end of our policy URI.
         *
         * To determine it we need to discover the respective *-policyDef.json:
         *
         * First, download the plugin and inspect the metadata.
         * If only a single policy implementation exists, just use it.
         * If multiple policy implementations exist then `name` *must* be provided to disambiguate.
         *
         * @param coordinates apiman plugin coordinates GAV(C)
         * @param policyId the policy to select from the plugin.
         * @return the extracted policy definition
         *
         * @throws InvalidPluginException if the plugin is invalid
         */
        public PolicyDefinitionBean getPolicyDefinition(PluginCoordinates coordinates, String policyId) throws InvalidPluginException {
            Plugin plugin = super.loadPlugin(coordinates);
            List<PolicyDefinitionBean> policyDefs = plugin.getPolicyDefinitions().stream()
                    .map(url -> MappingUtil.readJsonValue(url, PolicyDefinitionBean.class))
                    .collect(Collectors.toList()); // TODO Consider PluginResourceImpl L189 extract common validation aspects

            LOGGER.debug("Plugin {} contains {} policy definitions", plugin.getCoordinates(), policyDefs.size());
            PolicyDefinitionBean selected = null;

            if (policyDefs.isEmpty()) {
                throw new RuntimeException("Plugin contained no policies");
            } else if (policyDefs.size() == 1) {
                selected = policyDefs.get(0);
                LOGGER.info("Automatically selecting policy: {}", selected.getName());
            } else {
                String name = ofNullable(policyId) // TODO or Id
                        .map(String::toLowerCase)
                        .orElseThrow(() -> new RuntimeException("Multiple policyDefs exist in plugin. You must disambiguate "
                                + "by providing its name."));

                selected = policyDefs.stream()
                        .filter(def -> name.equals(def.getId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Plugin did not contain the indicated policy")); // TODO
            }
            LOGGER.debug("Selecting policy {} ({})", selected.getId(), selected.getName());
            return selected;
        }

        public PolicyDefinitionBean getInbuiltPolicy(String shortName) {
            return inbuiltPolicyMap.get(shortName.toLowerCase());
        }

        public PolicyDefinitionBean getPolicyDefinition(PluginCoordinates coordinates) throws InvalidPluginException {
            return getPolicyDefinition(coordinates, null);
        }
    }
}


