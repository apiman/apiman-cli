package io.apiman.cli;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author Pete
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ServiceTest extends BaseTest {

    @Test
    public void test1_create() {
        Cli.main("service",
                "create",
                "--debug",
                "--server", APIMAN_URL,
                "--name", "example",
                "--endpoint", "http://example.com",
                "--initialVersion", "1.0",
                "--publicService",
                "--orgName", "test");
    }
}
