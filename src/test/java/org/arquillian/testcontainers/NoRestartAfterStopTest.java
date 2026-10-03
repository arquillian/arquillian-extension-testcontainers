/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.arquillian.testcontainers;

import org.arquillian.testcontainers.api.Testcontainer;
import org.arquillian.testcontainers.api.TestcontainersRequired;
import org.arquillian.testcontainers.common.EnrichmentAfterStopObserver;
import org.arquillian.testcontainers.common.SimpleTestContainer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opentest4j.TestAbortedException;

/**
 * Verifies that a stopped container is not started again by a subsequent enrichment. The container is first stopped
 * manually, which must survive the enrichment of the next test instance. The {@link EnrichmentAfterStopObserver} then
 * fails this test class if the container was started again by an enrichment after the test class.
 *
 * @author Radoslav Husar
 */
@ExtendWith(ArquillianExtension.class)
@TestcontainersRequired(TestAbortedException.class)
@RunAsClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NoRestartAfterStopTest {

    @Testcontainer
    private SimpleTestContainer container;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    @Order(1)
    public void containerRunning() {
        Assertions.assertTrue(container.isRunning(), "Expected the container to be running");
        container.stop();
    }

    @Test
    @Order(2)
    public void containerNotRestartedAfterManualStop() {
        Assertions.assertFalse(container.isRunning(), "Expected the manually stopped container not to be started again");
    }
}
