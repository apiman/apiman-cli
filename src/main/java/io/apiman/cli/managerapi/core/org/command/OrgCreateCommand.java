/*
 * Copyright 2016 Pete Cornish
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

package io.apiman.cli.managerapi.core.org.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import io.apiman.cli.managerapi.command.ModelCreateCommand;
import io.apiman.cli.managerapi.core.org.OrgApi;
import io.apiman.cli.managerapi.core.org.OrgMixin;
import io.apiman.cli.managerapi.core.org.model.Org;
import io.apiman.cli.exception.CommandException;

/**
 * Create an organisation.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@Parameters(commandDescription = "Create organisation")
public class OrgCreateCommand extends ModelCreateCommand<Org, OrgApi>
        implements OrgMixin {

    @Parameter(names = { "--name", "-n"}, description = "Name", required = true)
    private String name;

    @Parameter(names = { "--description", "-d"}, description = "Description")
    private String description;

    @Override
    protected Org buildModelInstance() throws CommandException {
        return new Org(name, description);
    }
}
