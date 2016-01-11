package io.apiman.cli.core.org;

import io.apiman.cli.api.action.common.ModelAction;
import io.apiman.cli.core.org.model.Org;

/**
 * @author Pete
 */
public interface OrgMixin extends ModelAction<Org, OrgApi> {
    @Override
    default Class<OrgApi> getApiClass() {
        return OrgApi.class;
    }

    @Override
    default Class<Org> getModelClass() {
        return Org.class;
    }
}
