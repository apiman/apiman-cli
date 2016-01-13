package io.apiman.cli.action;

import io.apiman.cli.common.BaseTest;
import io.apiman.cli.core.declarative.action.ApplyAction;
import io.apiman.cli.core.declarative.model.Declaration;
import io.apiman.cli.util.JsonUtil;
import io.apiman.cli.util.YamlUtil;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;

/**
 * @author pete
 */
public class DeclarativeTest extends BaseTest {
    private ApplyAction action;

    @Before
    public void setUp() {
        action = new ApplyAction();
        action.setServerAddress(APIMAN_URL);
        action.setLogDebug(true);
    }

    @Test
    public void testLoadDeclarationJson() throws Exception {
        final Declaration declaration = action.loadDeclaration(
                Paths.get(DeclarativeTest.class.getResource("/simple.json").toURI()), JsonUtil.MAPPER);

        assertLoadedModel(declaration);
    }

    @Test
    public void testLoadDeclarationYaml() throws Exception {
        final Declaration declaration = action.loadDeclaration(
                Paths.get(DeclarativeTest.class.getResource("/simple.yml").toURI()), YamlUtil.MAPPER);

        assertLoadedModel(declaration);
    }

    @Test
    public void testApplyDeclarationYaml() throws Exception {
        final Declaration declaration = action.loadDeclaration(
                Paths.get(DeclarativeTest.class.getResource("/simple.yml").toURI()), YamlUtil.MAPPER);

        action.applyDeclaration(declaration);
    }

    private void assertLoadedModel(Declaration declaration) {
        assertNotNull(declaration);
        assertNotNull(declaration.getSystem());
        assertNotNull(declaration.getOrg());
    }
}
