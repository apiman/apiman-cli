package io.apiman.cli.action.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import io.apiman.cli.exception.ActionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;
import retrofit.client.Response;

import java.lang.reflect.Method;
import java.text.MessageFormat;

import static io.apiman.cli.util.JsonUtil.MAPPER;
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
            final A apiClient = this.getApiClient(getApiClass());
            final Method fetchMethod = apiClient.getClass().getMethod("fetch", String.class);

            @SuppressWarnings("unchecked")
            final Response response = (Response) fetchMethod.invoke(apiClient, getModelId());

            // convert to model type (required, as Retrofit's type inference does't work here)
            final JavaType modelType = MAPPER.getTypeFactory().constructType(getModelClass());
            final M model = MAPPER.readValue(response.getBody().in(), modelType);

            LOGGER.debug("{} received: {}", this::getModelName, () -> model);
            processModel(model);

        } catch (Exception e) {
            throw new ActionException(e);
        }
    }

    protected void processModel(M model) {
        try {
            OUTPUT.info(MAPPER.writeValueAsString(model));
        } catch (JsonProcessingException e) {
            throw new ActionException(e);
        }
    }

    protected abstract String getModelId() throws ActionException;
}
