package io.apiman.cli.action.org;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.apiman.cli.action.AbstractFinalAction;
import io.apiman.cli.exception.ActionException;
import io.apiman.cli.model.Organisation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.net.HttpURLConnection;

/**
 * @author Pete
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrgCreateAction extends AbstractFinalAction {
    private static final Logger LOGGER = LogManager.getLogger(OrgCreateAction.class);

    @Option(name = "--name", aliases = {"-n"}, usage = "Name", required = true)
    private String name;

    @Option(name = "--description", aliases = {"-d"}, usage = "Description")
    private String description;

    @Override
    protected void performAction(CmdLineParser parser) throws ActionException {
        invokeAndCheckResponse(() -> getApiClient(OrgApi.class)
                .create(new Organisation(name, description)), HttpURLConnection.HTTP_OK);
    }

    @Override
    protected String getActionName() {
        return "Create organisation";
    }
}
