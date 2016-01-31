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

package io.apiman.cli.core.org.command;

import io.apiman.cli.core.common.command.ModelCreateCommand;
import io.apiman.cli.core.org.OrgApi;
import io.apiman.cli.core.org.OrgMixin;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.core.org.model.Org;
import org.kohsuke.args4j.Option;

/**
 * Create an organisation.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class OrgCreateCommand extends ModelCreateCommand<Org, OrgApi>
        implements OrgMixin {

    @Option(name = "--name", aliases = {"-n"}, usage = "Name", required = true)
    private String name;

    @Option(name = "--description", aliases = {"-d"}, usage = "Description")
    private String description;

    @Override
    protected Org buildModelInstance() throws CommandException {
        return new Org(name, description);
    }
}
