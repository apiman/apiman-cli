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

package io.apiman.cli.command.common;

import io.apiman.cli.managerapi.core.common.model.ServerAction;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public interface ActionApi {
    @POST("/actions")
    Response doAction(@Body ServerAction action);
}
