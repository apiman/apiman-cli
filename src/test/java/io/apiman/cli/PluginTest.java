package io.apiman.cli;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author Pete
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PluginTest extends BaseTest {

    @Test
    public void test1_create() {
        Cli.main("plugin",
                "create",
                "--debug",
                "--server", APIMAN_URL,
                "--groupId", "io.apiman.plugins",
                "--artifactId", "apiman-plugins-test-policy",
                "--version", "1.1.9.Final");
    }

    @Test
    public void test2_fetch() {
        Cli.main("plugin",
                "show",
                "--debug",
                "--server", APIMAN_URL,
                "--id", "1");
    }

    @Test
    public void test3_list() {
        Cli.main("plugin",
                "list",
                "--debug",
                "--server", APIMAN_URL);
    }
}
