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

import com.google.common.io.Files;
import com.google.common.io.Resources;
import io.apiman.cli.exception.CommandException;
import io.apiman.common.plugin.Plugin;
import io.apiman.common.plugin.PluginCoordinates;
import io.apiman.manager.api.beans.policies.PolicyDefinitionBean;
import io.apiman.manager.api.core.exceptions.InvalidPluginException;
import io.apiman.manager.api.core.plugin.AbstractPluginRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;


/**
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
public final class PolicyResolver extends AbstractPluginRegistry {

    private static final Logger LOGGER = LogManager.getLogger(PolicyResolver.class);
    private static final Map<String, PolicyDefinitionBean> inbuiltPolicyMap = new LinkedHashMap<>();

    public PolicyResolver() {
        super(Files.createTempDir());
        buildInbuiltPolicyMap();
        LOGGER.debug("Inbuilt policy map: {}", inbuiltPolicyMap);
    }

    private void buildInbuiltPolicyMap() {
        URL policyDefResource = Resources.getResource("data/all-policyDefs.json");
        @SuppressWarnings("unchecked")
        List<PolicyDefinitionBean> inbuilts = new ArrayList<>(MappingUtil.readJsonValue(policyDefResource, List.class, PolicyDefinitionBean.class));

        for (PolicyDefinitionBean policyDef : inbuilts) {
            inbuiltPolicyMap.put(policyDef.getId().toLowerCase(), policyDef); // TODO
            inbuiltPolicyMap.put(policyDef.getName().toLowerCase(), policyDef);
            String[] split = ofNullable(policyDef.getPolicyImpl().split("class:", 2))
                    .orElseThrow(() -> new CommandException("Unexpected format in built-in policyImpl field: " + policyDef.getPolicyImpl()));
            String policyFqdn = split[1];
            inbuiltPolicyMap.put(policyFqdn.toLowerCase(), policyDef);
        }
    }

    /**
     * We need the FQCN for the end of our policy URI.
     * <p>
     * To determine it we need to discover the respective *-policyDef.json:
     * <p>
     * First, download the plugin and inspect the metadata. If only a single policy implementation exists, just use it.
     * If multiple policy implementations exist then `name` *must* be provided to disambiguate.
     *
     * @param coordinates apiman plugin coordinates GAV(C)
     * @param policyId    the policy to select from the plugin.
     * @return the extracted policy definition
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
            throw new ResolverException("Contains no policies", coordinates, policyId);
        } else if (policyDefs.size() == 1) {
            selected = policyDefs.get(0);
            if (policyId != null && !selected.getId().equals(policyId)) {
                throw new NoSuchPolicyException(policyDefs, coordinates, policyId);
            }
            LOGGER.info("Automatically selecting policy: {}", selected.getName());
        } else {
            String name = ofNullable(policyId)
                    .orElseThrow(() -> new AmbiguousPluginException(policyDefs, coordinates, policyId));

            selected = policyDefs.stream()
                    .filter(def -> name.equals(def.getId()))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchPolicyException(policyDefs, coordinates, policyId));
        }
        LOGGER.debug("Selecting policy {} ({})", selected.getId(), selected.getName());
        return selected;
    }

    public PolicyDefinitionBean getInbuiltPolicy(String shortName) {
        return ofNullable(inbuiltPolicyMap.get(shortName.toLowerCase()))
                .orElseThrow(() -> new NoSuchBuiltInPolicyException(shortName));
    }

    public PolicyDefinitionBean getPolicyDefinition(PluginCoordinates coordinates) throws InvalidPluginException {
        return getPolicyDefinition(coordinates, null);
    }

    public static class ResolverException extends InvalidPluginException {

        public ResolverException(String message, PluginCoordinates coordinates, String policyId) {
            super(String.format("[Plugin: %s, PolicyID: %s] %s", coordinates, policyId, message));
        }

    }

    public static class NoSuchPolicyException extends ResolverException {

        public NoSuchPolicyException(List<PolicyDefinitionBean> policyDefs, PluginCoordinates coordinates, String policyId) {
            super("Does not contain indicated policy. Available: " + getPolicyIds(policyDefs), coordinates, policyId);
        }
    }

    public static class AmbiguousPluginException extends ResolverException {

        public AmbiguousPluginException(List<PolicyDefinitionBean> policyDefs, PluginCoordinates coordinates, String policyId) {
            super("Multiple policies encapsulated. Disambiguate by explicitly providing policy name. Available: " +
                    getPolicyIds(policyDefs), coordinates, policyId);
        }
    }

    public static class NoSuchBuiltInPolicyException extends RuntimeException {
        public NoSuchBuiltInPolicyException(String message) {
            super("Unknown built-in policy: " + message + " Available: " + getPolicyIds(inbuiltPolicyMap.values()));
        }
    }

    private static String getPolicyIds(Collection<PolicyDefinitionBean> policyDefs) {
        return policyDefs.stream()
                .map(PolicyDefinitionBean::getId)
                .collect(Collectors.joining(", "));
    }
}


