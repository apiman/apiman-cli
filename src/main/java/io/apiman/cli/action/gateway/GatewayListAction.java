package io.apiman.cli.action.gateway;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.apiman.cli.action.AbstractFinalAction;
import io.apiman.cli.exception.ActionException;
import io.apiman.cli.model.Gateway;
import io.apiman.cli.util.JsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;

import java.util.List;

/**
 * @author Pete
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GatewayListAction extends AbstractFinalAction {
    private static final Logger LOGGER = LogManager.getLogger(GatewayListAction.class);

    @Override
    protected void performAction(CmdLineParser parser) throws ActionException {
        final List<Gateway> gateway = getApiClient(GatewayApi.class).list();
        try {
            LOGGER.info(JsonUtil.MAPPER.writeValueAsString(gateway));
        } catch (JsonProcessingException e) {
            throw new ActionException(e);
        }
    }

    @Override
    protected String getActionName() {
        return "List gateways";
    }
}
