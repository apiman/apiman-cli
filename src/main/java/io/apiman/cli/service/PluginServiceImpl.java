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

package io.apiman.cli.service;

import io.apiman.cli.managerapi.core.plugin.PluginApi;
import io.apiman.cli.managerapi.core.plugin.model.Plugin;
import io.apiman.cli.managerapi.management.ManagementApiUtil;
import io.apiman.cli.util.BeanUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.List;

/**
 * Manages plugins.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class PluginServiceImpl implements PluginService {
    private static final Logger LOGGER = LogManager.getLogger(PluginServiceImpl.class);

    private ManagementApiService managementApiService;

    @Inject
    public PluginServiceImpl(ManagementApiService managementApiService) {
        this.managementApiService = managementApiService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPlugins(List<Plugin> plugins) {
        LOGGER.debug("Adding plugins");

        plugins.forEach(plugin -> {
            final PluginApi apiClient = managementApiService.buildServerApiClient(PluginApi.class);

            if (checkPluginExists(plugin, apiClient)) {
                LOGGER.info("Plugin already installed: {}", plugin.getName());
            } else {
                LOGGER.info("Installing plugin: {}", plugin.getName());
                apiClient.create(plugin);
            }
        });
    }

    /**
     * Determine if the plugin is installed.
     *
     * @param plugin
     * @param apiClient
     * @return <code>true</code> if the plugin is installed, otherwise <code>false</code>
     */
    private boolean checkPluginExists(Plugin plugin, PluginApi apiClient) {
        return ManagementApiUtil.checkExists(apiClient::list)
                .map(apiPolicies -> apiPolicies.stream()
                        .anyMatch(installedPlugin ->
                                plugin.getArtifactId().equals(installedPlugin.getArtifactId()) &&
                                        plugin.getGroupId().equals(installedPlugin.getGroupId()) &&
                                        plugin.getVersion().equals(installedPlugin.getVersion()) &&
                                        BeanUtil.safeEquals(plugin.getClassifier(), installedPlugin.getClassifier())
                        ))
                .orElse(false);
    }
}
