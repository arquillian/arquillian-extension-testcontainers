/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.arquillian.testcontainers;

import org.arquillian.testcontainers.api.Testcontainer;
import org.arquillian.testcontainers.api.TestcontainersRequired;
import org.arquillian.testcontainers.common.SimpleTestContainer;
import org.arquillian.testcontainers.common.WildFlyContainer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opentest4j.TestAbortedException;

@ExtendWith(ArquillianExtension.class)
@TestcontainersRequired(TestAbortedException.class)
@RunAsClient
public class RegistryInjectionTest {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Testcontainer(name = "alpha")
    private SimpleTestContainer alpha;

    @ArquillianResource
    private TestcontainerRegistry registry;

    @Test
    public void registryIsInjected() {
        Assertions.assertNotNull(registry, "Expected the TestcontainerRegistry to be injected");
    }

    @Test
    public void registryResolvesByName() {
        Assertions.assertSame(alpha, registry.lookup("alpha"));
        Assertions.assertSame(alpha, registry.lookup("alpha", SimpleTestContainer.class));
        Assertions.assertNull(registry.lookup("missing"));
        Assertions.assertNull(registry.lookup("missing", SimpleTestContainer.class));
    }

    @Test
    public void lookupGuardsNullAndEmptyName() {
        Assertions.assertNull(registry.lookup(null));
        Assertions.assertNull(registry.lookup(""));
    }

    @Test
    public void typedLookupRejectsMismatchedType() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> registry.lookup("alpha", WildFlyContainer.class));
    }
}
