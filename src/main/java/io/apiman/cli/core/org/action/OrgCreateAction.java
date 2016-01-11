package io.apiman.cli.core.org.action;

import io.apiman.cli.api.action.common.ModelCreateAction;
import io.apiman.cli.core.org.OrgApi;
import io.apiman.cli.core.org.OrgMixin;
import io.apiman.cli.api.exception.ActionException;
import io.apiman.cli.core.org.model.Org;
import org.kohsuke.args4j.Option;

/**
 * @author Pete
 */
public class OrgCreateAction extends ModelCreateAction<Org, OrgApi>
        implements OrgMixin {

    @Option(name = "--name", aliases = {"-n"}, usage = "Name", required = true)
    private String name;

    @Option(name = "--description", aliases = {"-d"}, usage = "Description")
    private String description;

    @Override
    protected Org buildModelInstance() throws ActionException {
        return new Org(name, description);
    }
}
