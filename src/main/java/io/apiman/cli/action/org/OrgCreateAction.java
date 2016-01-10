package io.apiman.cli.action.org;

import io.apiman.cli.action.common.ModelCreateAction;
import io.apiman.cli.exception.ActionException;
import io.apiman.cli.model.Org;
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
