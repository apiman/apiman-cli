/*
 * Copyright 2017 Pete Cornish
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.apiman.cli.managerapi.command.org;

import io.apiman.cli.command.org.model.Org;
import io.apiman.cli.command.org.model.OrgSearchIn;
import io.apiman.cli.command.org.model.OrgSearchOut;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public interface OrgApi {

    @POST("/organizations")
    Response create(@Body Org organisation);

    @GET("/organizations/{orgName}")
    Org fetch(@Path("orgName") String orgName);
    
    @POST("/search/organizations/")
    OrgSearchOut search(@Body OrgSearchIn orgSearch);
}
