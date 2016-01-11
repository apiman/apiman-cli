package io.apiman.cli.api.action.common;

import io.apiman.cli.api.exception.ActionException;
import io.apiman.cli.util.ApiUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;
import retrofit.client.Response;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;

/**
 * @author Pete
 */
public abstract class ModelCreateAction<M, A> extends AbstractModelAction<M, A> {
    private static final Logger LOGGER = LogManager.getLogger(ModelCreateAction.class);

    @Override
    protected String getActionName() {
        return MessageFormat.format("Create {0}", getModelName());
    }

    @Override
    protected void performAction(CmdLineParser parser) throws ActionException {
        LOGGER.debug("Creating {}", this::getModelName);

        ApiUtil.invokeAndCheckResponse(() -> {
            try {
                final A apiClient = buildApiClient(getApiClass());
                final Method createMethod = apiClient.getClass().getMethod("create", getModelClass());
                return (Response) createMethod.invoke(apiClient, buildModelInstance());

            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new ActionException(e);
            }
        });
    }

    protected abstract M buildModelInstance() throws ActionException;
}
