package io.apiman.cli;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author Pete
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrgTest extends BaseTest {

    @Test
    public void test1_create() {
        Cli.main("org",
                "create",
                "--debug",
                "--server", APIMAN_URL,
                "--serverUsername", "admin",
                "--serverPassword", "admin123!",
                "--name", "test",
                "--description", "example");
    }

    @Test
    public void test2_fetch() {
        Cli.main("org",
                "show",
                "--debug",
                "--server", APIMAN_URL,
                "--serverUsername", "admin",
                "--serverPassword", "admin123!",
                "--name", "test");
    }
}
