package io.apiman.cli;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author Pete
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GatewayTest extends BaseTest {

    @Test
    public void test1_gatewayTest() {
        Cli.main("gateway",
                "test",
                "--debug",
                "--server", APIMAN_URL,
                "--endpoint", "http://localhost:8080/apiman-gateway-api",
                "--username", "apimanager",
                "--password", "apiman123!",
                "--type", "REST");
    }

    @Test
    public void test2_gatewayCreate() {
        Cli.main("gateway",
                "create",
                "--debug",
                "--server", APIMAN_URL,
                "--name", "test",
                "--description", "example",
                "--endpoint", "http://localhost:1234",
                "--username", "apimanager",
                "--password", "apiman123!",
                "--type", "REST");
    }

    @Test
    public void test3_gatewayFetch() {
        Cli.main("gateway",
                "show",
                "--debug",
                "--server", APIMAN_URL,
                "--name", "test");
    }

    @Test
    public void test4_gatewayList() {
        Cli.main("gateway",
                "list",
                "--debug",
                "--server", APIMAN_URL);
    }
}
