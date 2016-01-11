package io.apiman.cli.core.plugin.action;

import io.apiman.cli.api.action.common.ModelCreateAction;
import io.apiman.cli.api.exception.ActionException;
import io.apiman.cli.core.plugin.PluginApi;
import io.apiman.cli.core.plugin.PluginMixin;
import io.apiman.cli.core.plugin.model.Plugin;
import org.kohsuke.args4j.Option;

/**
 * @author Pete
 */
public class PluginCreateAction extends ModelCreateAction<Plugin, PluginApi>
        implements PluginMixin {

    @Option(name = "--groupId", aliases = {"-g"}, usage = "Group ID", required = true)
    private String groupId;

    @Option(name = "--artifactId", aliases = {"-a"}, usage = "Artifact ID", required = true)
    private String artifactId;

    @Option(name = "--version", aliases = {"-v"}, usage = "Version", required = true)
    private String version;

    @Option(name = "--classifier", aliases = {"-c"}, usage = "Classifier")
    private String classifier;

    @Override
    protected Plugin buildModelInstance() throws ActionException {
        return new Plugin(groupId, artifactId, classifier, version);
    }
}
