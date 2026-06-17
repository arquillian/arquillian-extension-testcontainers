/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.arquillian.testcontainers;

import org.arquillian.testcontainers.api.Testcontainer;
import org.arquillian.testcontainers.api.TestcontainerScope;
import org.arquillian.testcontainers.api.TestcontainersRequired;
import org.arquillian.testcontainers.common.SimpleTestContainer;
import org.arquillian.testcontainers.common.WildFlyContainer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opentest4j.TestAbortedException;

/**
 * Tests that suite-scoped and class-scoped containers coexist correctly in a single test class.
 *
 * @author Radoslav Husar
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@TestcontainersRequired(TestAbortedException.class)
public class MixedLifecycleTest {

    @Testcontainer(scope = TestcontainerScope.SUITE)
    private SimpleTestContainer suiteContainer;

    @Testcontainer(scope = TestcontainerScope.CLASS)
    private WildFlyContainer classContainer;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void suiteContainerIsRunning() {
        Assertions.assertNotNull(suiteContainer, "Expected the suite container to be injected.");
        Assertions.assertTrue(suiteContainer.isRunning(), "Expected the suite container to be running");
    }

    @Test
    public void classContainerIsRunning() {
        Assertions.assertNotNull(classContainer, "Expected the class container to be injected.");
        Assertions.assertTrue(classContainer.isRunning(), "Expected the class container to be running");
    }

    @Test
    public void containersAreDifferentInstances() {
        Assertions.assertNotSame(suiteContainer, classContainer,
                "Suite and class containers should be different instances");
    }
}
