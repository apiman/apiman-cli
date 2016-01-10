package io.apiman.cli.action.common;

/**
 * @author Pete
 */
public interface ModelAction<M, A> {
    Class<M> getModelClass();

    Class<A> getApiClass();

    /**
     * @return the name for the model class
     */
    default String getModelName() {
        return getModelClass().getSimpleName();
    }
}
