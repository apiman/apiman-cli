package io.apiman.cli.action.gateway;

import io.apiman.cli.action.common.ModelAction;
import io.apiman.cli.model.Gateway;

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
