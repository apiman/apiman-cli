package io.apiman.cli.util;

import io.apiman.cli.common.IntegrationTest;
import io.apiman.common.plugin.PluginCoordinates;
import io.apiman.manager.api.beans.policies.PolicyDefinitionBean;
import io.apiman.manager.api.core.exceptions.InvalidPluginException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

/**
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
@Category(IntegrationTest.class)
public class PluginRegistryTest {
    private PolicyResolver policyResolver;
    private static final String TEST_PLUGIN_POLICYIMPL = "plugin:io.apiman.plugins:apiman-plugins-test-policy:1.3.1.Final:war/io.apiman.plugins.test_policy.TestPolicy";
    private static final PluginCoordinates TEST_PLUGIN_COORDS = new PluginCoordinates("io.apiman.plugins",
            "apiman-plugins-test-policy",
            "1.3.1.Final");

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        this.policyResolver = new PolicyResolver();
    }

    @Test
    public void testGetPluginPolicy_InferPolicyId() throws Exception {
        PolicyDefinitionBean policyDef = policyResolver.getPolicyDefinition(TEST_PLUGIN_COORDS);
        // Policy should be found in plugin
        Assert.assertNotNull(policyDef);
        Assert.assertEquals("test-policy", policyDef.getId());
        Assert.assertEquals(TEST_PLUGIN_POLICYIMPL, policyDef.getPolicyImpl());
    }

    @Test
    public void testGetPluginPolicy_ExplicitPolicyId() throws Exception {
        PolicyDefinitionBean policyDef = policyResolver.getPolicyDefinition(TEST_PLUGIN_COORDS, "test-policy");
        // Policy should be found in plugin
        Assert.assertNotNull(policyDef);
        Assert.assertEquals("test-policy", policyDef.getId());
        Assert.assertEquals(TEST_PLUGIN_POLICYIMPL, policyDef.getPolicyImpl());
    }

    @Test
    public void testGetPluginPolicy_BadPolicyId() throws Exception {
        exception.expect(PolicyResolver.NoSuchPolicyException.class);
        policyResolver.getPolicyDefinition(TEST_PLUGIN_COORDS, "foo-bar");
    }

    @Test
    public void testGetPluginPolicy_NoSuchPlugin() throws Exception {
        exception.expect(InvalidPluginException.class);
        policyResolver.getPolicyDefinition(
                new PluginCoordinates("bad", "coord", "s"),
                "foo-bar");
    }

    @Test
    public void testGetBuiltinPolicy_ValidPolicy() throws Exception {
        PolicyDefinitionBean pdb = policyResolver.getInbuiltPolicy("BasicAuthenticationPolicy");
        Assert.assertNotNull(pdb);
        Assert.assertEquals("BASICAuthenticationPolicy", pdb.getId());
        Assert.assertEquals("class:io.apiman.gateway.engine.policies.BasicAuthenticationPolicy", pdb.getPolicyImpl());
    }

    @Test
    public void testGetBuiltinPolicy_ValidPolicyHumanName() throws Exception {
        PolicyDefinitionBean pdb = policyResolver.getInbuiltPolicy("Basic Authentication Policy");
        Assert.assertNotNull(pdb);
        Assert.assertEquals("BASICAuthenticationPolicy", pdb.getId());
        Assert.assertEquals("class:io.apiman.gateway.engine.policies.BasicAuthenticationPolicy", pdb.getPolicyImpl());
    }

    @Test
    public void testGetBuiltinPolicy_InvalidPolicyName() throws Exception {
        exception.expect(PolicyResolver.NoSuchBuiltInPolicyException.class);
        policyResolver.getInbuiltPolicy("This Policy Does Not Exist");
    }

}
