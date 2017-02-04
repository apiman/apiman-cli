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

package io.apiman.cli.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;

/**
 * Common logging functionality.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public final class LogUtil {
    /**
     * Used for printing output to the user; typically this is mapped to stdout.
     */
    public static final Logger OUTPUT = LogManager.getLogger("output");

    /**
     * Platform line separator.
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * Whether to log at debug level.
     */
    private static boolean logDebug;

    private LogUtil() {
    }

    /**
     * Configure the logging subsystem.
     *
     * @param logDebug whether debug logging is enabled
     */
    public static void configureLogging(boolean logDebug) {
        LogUtil.logDebug = logDebug;

        final LoggerContext context = (LoggerContext) LogManager.getContext(false);
        final LoggerConfig rootLogger = context.getConfiguration().getRootLogger();

        // remove existing appenders
        rootLogger.getAppenders().forEach((appenderName, appender) -> rootLogger.removeAppender(appenderName));

        final Appender appender;
        if (logDebug) {
            rootLogger.setLevel(Level.DEBUG);
            appender = context.getConfiguration().getAppender("ConsoleVerbose");

        } else {
            appender = context.getConfiguration().getAppender("ConsoleTerse");
        }

        rootLogger.addAppender(appender, null, null);
        context.updateLoggers();
    }

    public static boolean isLogDebug() {
        return logDebug;
    }
}
