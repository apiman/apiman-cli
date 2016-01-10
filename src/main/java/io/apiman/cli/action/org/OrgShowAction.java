package io.apiman.cli.action.org;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.apiman.cli.action.AbstractFinalAction;
import io.apiman.cli.exception.ActionException;
import io.apiman.cli.model.Organisation;
import io.apiman.cli.util.JsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * @author Pete
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrgShowAction extends AbstractFinalAction {
    private static final Logger LOGGER = LogManager.getLogger(OrgShowAction.class);

    @Option(name = "--name", aliases = {"-n"}, usage = "Name")
    private String name;

    @Override
    protected void performAction(CmdLineParser parser) throws ActionException {
        final Organisation org = getApiClient(OrgApi.class).fetch(name);
        try {
            LOGGER.info(JsonUtil.MAPPER.writeValueAsString(org));
        } catch (JsonProcessingException e) {
            throw new ActionException(e);
        }
    }

    @Override
    protected String getActionName() {
        return "Show organisation";
    }
}
