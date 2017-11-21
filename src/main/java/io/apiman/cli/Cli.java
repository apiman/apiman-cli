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

package io.apiman.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.google.common.collect.Lists;
import io.apiman.cli.command.core.AbstractCommand;
import io.apiman.cli.command.core.Command;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.exception.ExitWithCodeException;
import io.apiman.cli.util.InjectionUtil;
import io.apiman.cli.util.LogUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * The main class; the root of all Commands.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@Parameters(commandDescription = "Apiman CLI")
public class Cli extends AbstractCommand {
    private static final Logger LOGGER = LogManager.getLogger(Cli.class);

    @Override
    public void run(List<String> args, JCommander jc) {
        jc.setAcceptUnknownOptions(false);
        jc.setProgramName("apiman-cli");
        jc.addObject(this);
        build(jc);
        try {
            jc.parse(args.toArray(new String[]{}));
            super.run(args, jc);
        } catch (ParameterException | CommandException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(e.getMessage(), e);
            } else {
                if (e.getCause() == null) {
                    LOGGER.error(e.getMessage());
                } else {
                    LOGGER.error("{}: {}", e.getMessage(), e.getCause().getMessage());
                }
            }
            printUsage(jc, false);
        } catch (ExitWithCodeException ec) {
            // print the message and exit with the given code
            LogUtil.OUTPUT.error(ec.getMessage());
            if (ec.isPrintUsage()) {
                printUsage(jc, ec.getExitCode());
            } else {
                System.exit(ec.getExitCode());
            }
        }
    }

    @Override
    protected void populateCommands(Map<String, Class<? extends Command>> commandMap) {
        commandMap.put("manager", ManagerCli.class);
        commandMap.put("gateway", GatewayCli.class);
    }

    public static void main(String... args) {
        InjectionUtil.getInjector().getInstance(Cli.class).run(Lists.newArrayList(args), new JCommander());
    }
}
