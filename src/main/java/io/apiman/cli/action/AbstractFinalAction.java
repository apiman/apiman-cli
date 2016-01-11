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

package io.apiman.cli.action;

import org.kohsuke.args4j.CmdLineParser;

import java.util.List;
import java.util.Map;

/**
 * An Action that has no child Actions.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public abstract class AbstractFinalAction extends AbstractAction {
    /**
     * Indicates that there is no child action and this instance should handle the request.
     *
     * @param args
     * @param parser
     * @return <code>null</code>
     */
    @Override
    protected Action getChildAction(List<String> args, CmdLineParser parser) {
        return null;
    }

    @Override
    protected void populateActions(Map<String, Class<? extends Action>> actionMap) {
        // no child actions
    }
}
