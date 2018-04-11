package io.apiman.cli.managerapi.service;

/**
 */
public interface PlanService {
    String STATE_READY = "READY";
    String STATE_CREATED = "CREATED";
    String STATE_LOCKED = "LOCKED";

    /**
     * Return the current state of the API.
     *
     * @param orgName       the organisation name
     * @param planName       the API name
     * @param planVersion    the API version
     * @return the API state
     */
    String fetchCurrentState(String orgName, String planName, String planVersion);

    /**
     * Lock the plan, if it is in the 'Ready' state.
     *
     * @param orgName       the organisation name
     * @param planName       the API name
     * @param planVersion    the API version
     * @return the API state
     */
    void lock(String orgName, String planName, String planVersion);
}
