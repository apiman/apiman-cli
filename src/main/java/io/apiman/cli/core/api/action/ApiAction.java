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

package io.apiman.cli.core.api.action;

import io.apiman.cli.action.AbstractAction;
import io.apiman.cli.action.Action;

import java.util.Map;

/**
 * Root Action for managing APIs.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class ApiAction extends AbstractAction {
    @Override
    protected void populateActions(Map<String, Class<? extends Action>> actionMap) {
        actionMap.put("create", ApiCreateAction.class);
        actionMap.put("list", ApiListAction.class);
        actionMap.put("publish", ApiPublishAction.class);
        actionMap.put("policy", ApiPolicyAction.class);
    }

    @Override
    protected String getActionName() {
        return "Manage APIs";
    }
}
