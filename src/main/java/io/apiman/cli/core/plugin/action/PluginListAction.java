package io.apiman.cli.core.plugin.action;

import io.apiman.cli.api.action.common.ModelListAction;
import io.apiman.cli.core.plugin.PluginApi;
import io.apiman.cli.core.plugin.PluginMixin;
import io.apiman.cli.core.plugin.model.Plugin;

/**
 * @author Pete
 */
public class PluginListAction extends ModelListAction<Plugin, PluginApi>
        implements PluginMixin {
}
