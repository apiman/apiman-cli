package io.apiman.cli.core.plugin.action;

import io.apiman.cli.api.action.common.ModelShowAction;
import io.apiman.cli.api.exception.ActionException;
import io.apiman.cli.core.plugin.PluginApi;
import io.apiman.cli.core.plugin.PluginMixin;
import io.apiman.cli.core.plugin.model.Plugin;
import org.kohsuke.args4j.Option;

/**
 * @author Pete
 */
public class PluginShowAction extends ModelShowAction<Plugin, PluginApi>
        implements PluginMixin {

    @Option(name = "--id", aliases = {"-i"}, usage = "Plugin ID")
    private String id;

    @Override
    protected String getModelId() throws ActionException {
        return id;
    }
}
