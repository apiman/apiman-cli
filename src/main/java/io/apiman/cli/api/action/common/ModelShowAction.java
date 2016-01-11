package io.apiman.cli.api.action.common;

import io.apiman.cli.api.exception.ActionException;
import io.apiman.cli.util.JsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;

import java.lang.reflect.Method;
import java.text.MessageFormat;

import static io.apiman.cli.util.LogUtil.OUTPUT;

/**
 * @author Pete
 */
public abstract class ModelShowAction<M, A> extends AbstractModelAction<M, A> {
    private static final Logger LOGGER = LogManager.getLogger(ModelShowAction.class);

    @Override
    protected String getActionName() {
        return MessageFormat.format("Show {0}", getModelName());
    }

    @Override
    protected void performAction(CmdLineParser parser) throws ActionException {
        LOGGER.debug("Showing {}", this::getModelName);

        try {
            final A apiClient = this.buildApiClient(getApiClass());
            final Method fetchMethod = apiClient.getClass().getMethod("fetch", String.class);

            @SuppressWarnings("unchecked")
            final M model = (M) fetchMethod.invoke(apiClient, getModelId());
            LOGGER.debug("{} received: {}", this::getModelName, () -> JsonUtil.safeWriteValueAsString(model));

            processModel(model);

        } catch (Exception e) {
            throw new ActionException(e);
        }
    }

    protected void processModel(M model) {
        OUTPUT.info(JsonUtil.safeWriteValueAsString(model));
    }

    protected abstract String getModelId() throws ActionException;
}
