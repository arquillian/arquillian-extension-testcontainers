/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.arquillian.testcontainers;

import org.arquillian.testcontainers.api.Testcontainer;
import org.arquillian.testcontainers.api.TestcontainersRequired;
import org.arquillian.testcontainers.common.SimpleTestContainer;
import org.arquillian.testcontainers.common.StopAfterClassObserver;
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
 * Verifies that containers are stopped after the other {@link org.jboss.arquillian.test.spi.event.suite.AfterClass}
 * observers, even if one of them fails. The checks are done by the {@link StopAfterClassObserver}.
 *
 * @author Radoslav Husar
 */
@ExtendWith(ArquillianExtension.class)
@TestcontainersRequired(TestAbortedException.class)
@RunAsClient
public class StopAfterClassTest {

    @Testcontainer
    private SimpleTestContainer container;

    @Testcontainer(name = "other")
    private SimpleTestContainer otherContainer;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void containerRunning() {
        Assertions.assertTrue(container.isRunning(), "Expected the container to be running");
        Assertions.assertTrue(otherContainer.isRunning(), "Expected the other container to be running");
        Assertions.assertNotSame(container, otherContainer, "Expected distinct container instances");
    }
}
