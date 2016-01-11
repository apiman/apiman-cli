package io.apiman.cli.core.service;

import io.apiman.cli.api.action.common.ModelAction;
import io.apiman.cli.core.service.model.Service;

/**
 * @author Pete
 */
public interface ServiceMixin extends ModelAction<Service, ServiceApi> {
    @Override
    default Class<ServiceApi> getApiClass() {
        return ServiceApi.class;
    }

    @Override
    default Class<Service> getModelClass() {
        return Service.class;
    }
}
