package io.apiman.cli.core.plugin;

import io.apiman.cli.api.action.common.ModelAction;
import io.apiman.cli.core.plugin.model.Plugin;

/**
 * @author Pete
 */
public interface PluginMixin extends ModelAction<Plugin, PluginApi> {
    @Override
    default Class<PluginApi> getApiClass() {
        return PluginApi.class;
    }

    @Override
    default Class<Plugin> getModelClass() {
        return Plugin.class;
    }
}
