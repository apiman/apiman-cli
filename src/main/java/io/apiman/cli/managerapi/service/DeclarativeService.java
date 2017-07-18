/*
 * Copyright 2017 Pete Cornish
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apiman.cli.managerapi.service;

import io.apiman.cli.command.declarative.model.DeclarativeApi;
import io.apiman.cli.command.declarative.model.DeclarativeGateway;
import io.apiman.cli.command.declarative.model.DeclarativeOrg;
import io.apiman.cli.managerapi.command.common.model.ManagementApiVersion;

import java.util.List;

/**
 * Applies changes in a declarative fashion.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public interface DeclarativeService {
    /**
     * Add gateways if they are not present.
     *
     * @param gateways the gateways to add.
     */
    void applyGateways(List<DeclarativeGateway> gateways);

    /**
     * Add the organisation if it is not present.
     *
     * @param org the organisation to add.
     */
    void applyOrg(DeclarativeOrg org);

    /**
     * Add APIs to the specified organisation, if they are not present, then configure them.
     *
     * @param serverVersion the management server version.
     * @param apis          the APIs to add.
     * @param orgName       the name of the organisation.
     */
    void applyApis(ManagementApiVersion serverVersion, List<DeclarativeApi> apis, String orgName);
}
