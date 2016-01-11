package io.apiman.cli.core.gateway;

import io.apiman.cli.api.action.common.ModelAction;
import io.apiman.cli.core.gateway.model.Gateway;

/**
 * @author Pete
 */
public interface GatewayMixin extends ModelAction<Gateway, GatewayApi> {
    @Override
    default Class<GatewayApi> getApiClass() {
        return GatewayApi.class;
    }

    @Override
    default Class<Gateway> getModelClass() {
        return Gateway.class;
    }
}
