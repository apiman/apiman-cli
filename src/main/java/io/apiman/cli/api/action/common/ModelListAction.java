package io.apiman.cli.api.action.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.apiman.cli.api.exception.ActionException;
import io.apiman.cli.util.JsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.List;

import static io.apiman.cli.util.LogUtil.OUTPUT;

/**
 * @author Pete
 */
public abstract class ModelListAction<M, A> extends AbstractModelAction<M, A> {
    private static final Logger LOGGER = LogManager.getLogger(ModelListAction.class);

    @Override
    protected String getActionName() {
        return MessageFormat.format("List {0}s", getModelName());
    }

    @Override
    protected boolean permitNoArgs() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void performAction(CmdLineParser parser) throws ActionException {
        LOGGER.debug("Listing {}", this::getModelName);

        try {
            final A apiClient = getApiClient(getApiClass());
            final Method listMethod = apiClient.getClass().getMethod("list");
            processList((List<M>) listMethod.invoke(apiClient));

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new ActionException(e);
        }
    }

    protected void processList(List<M> model) {
        try {
            OUTPUT.info(JsonUtil.MAPPER.writeValueAsString(model));
        } catch (JsonProcessingException e) {
            throw new ActionException(e);
        }
    }
}
