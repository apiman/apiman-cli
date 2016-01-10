package io.apiman.cli.action.org;

import io.apiman.cli.action.common.ModelAction;
import io.apiman.cli.model.Org;

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
