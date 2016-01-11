package io.apiman.cli.core.gateway.action;

import io.apiman.cli.api.action.common.ModelListAction;
import io.apiman.cli.core.gateway.GatewayApi;
import io.apiman.cli.core.gateway.GatewayMixin;
import io.apiman.cli.core.gateway.model.Gateway;

/**
 * @author Pete
 */
public class GatewayListAction extends ModelListAction<Gateway, GatewayApi>
        implements GatewayMixin {
}
