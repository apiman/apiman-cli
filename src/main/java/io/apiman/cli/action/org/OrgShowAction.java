package io.apiman.cli.action.org;

import io.apiman.cli.action.common.ModelShowAction;
import io.apiman.cli.exception.ActionException;
import io.apiman.cli.model.Org;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.Option;

/**
 * @author Pete
 */
public class OrgShowAction extends ModelShowAction<Org, OrgApi> implements OrgMixin {
    private static final Logger LOGGER = LogManager.getLogger(OrgShowAction.class);

    @Option(name = "--name", aliases = {"-n"}, usage = "Name")
    private String name;

    @Override
    protected String getModelId() throws ActionException {
        return name;
    }
}
