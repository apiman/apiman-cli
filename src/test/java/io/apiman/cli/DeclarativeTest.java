package io.apiman.cli;

import io.apiman.cli.core.declarative.action.ApplyAction;
import io.apiman.cli.core.declarative.model.Declaration;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;

/**
 * @author pete
 */
public class DeclarativeTest {
    private ApplyAction action;

    @Before
    public void setUp() {
        action = new ApplyAction();
        action.setServerAddress("http://192.168.99.100:8080/apiman");
        action.setLogDebug(true);
    }

    @Test
    public void testLoadDeclarationJson() throws Exception {
        final Declaration declaration = action.loadDeclarationJson(
                Paths.get(DeclarativeTest.class.getResource("/simple.json").toURI()));

        assertLoadedModel(declaration);
    }

    @Test
    public void testLoadDeclarationYaml() throws Exception {
        final Declaration declaration = action.loadDeclarationYaml(
                Paths.get(DeclarativeTest.class.getResource("/simple.yml").toURI()));

        assertLoadedModel(declaration);
    }

    @Test
    public void testApplyDeclarationYaml() throws Exception {
        final Declaration declaration = action.loadDeclarationYaml(
                Paths.get(DeclarativeTest.class.getResource("/simple.yml").toURI()));

        action.applyDeclaration(declaration);
    }

    private void assertLoadedModel(Declaration declaration) {
        assertNotNull(declaration);
        assertNotNull(declaration.getSystem());
        assertNotNull(declaration.getOrg());
    }
}
