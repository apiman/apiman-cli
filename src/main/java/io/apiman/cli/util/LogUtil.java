package io.apiman.cli.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Pete
 */
public class LogUtil {
    /**
     * Used for printing output to the user; typically this is mapped to stdout.
     */
    public static final Logger OUTPUT = LogManager.getLogger("output");
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
}
