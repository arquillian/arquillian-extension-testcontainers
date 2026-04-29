/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.arquillian.testcontainers;

import java.util.List;

import org.arquillian.testcontainers.api.Testcontainer;
import org.arquillian.testcontainers.api.TestcontainersRequired;
import org.arquillian.testcontainers.api.event.AfterTestcontainerStart;
import org.arquillian.testcontainers.api.event.BeforeTestcontainerStart;
import org.arquillian.testcontainers.api.event.TestcontainerEvent;
import org.arquillian.testcontainers.common.SimpleTestContainer;
import org.arquillian.testcontainers.common.TestcontainerEventObserver;
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
 * @author Radoslav Husar
 */
@ExtendWith(ArquillianExtension.class)
@TestcontainersRequired(TestAbortedException.class)
@RunAsClient
public class ContainerEventTest {

    @Testcontainer
    private SimpleTestContainer container;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void startEventsAreFired() {
        List<TestcontainerEvent> events = TestcontainerEventObserver.events();
        Assertions.assertTrue(events.stream().anyMatch(e -> e instanceof BeforeTestcontainerStart),
                "Expected BeforeTestcontainerStart event");
        Assertions.assertTrue(events.stream().anyMatch(e -> e instanceof AfterTestcontainerStart),
                "Expected AfterTestcontainerStart event");
    }

    @Test
    public void startEventsContainContainer() {
        List<TestcontainerEvent> events = TestcontainerEventObserver.events();
        for (TestcontainerEvent event : events) {
            if (event instanceof BeforeTestcontainerStart || event instanceof AfterTestcontainerStart) {
                Assertions.assertSame(container, event.getContainer(),
                        "Expected event to reference the injected container");
            }
        }
    }

    @Test
    public void startEventsFireInOrder() {
        List<TestcontainerEvent> events = TestcontainerEventObserver.events();
        int beforeIdx = -1;
        int afterIdx = -1;
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i) instanceof BeforeTestcontainerStart) {
                beforeIdx = i;
            } else if (events.get(i) instanceof AfterTestcontainerStart) {
                afterIdx = i;
            }
        }
        Assertions.assertTrue(beforeIdx >= 0, "Expected BeforeTestcontainerStart event");
        Assertions.assertTrue(afterIdx >= 0, "Expected AfterTestcontainerStart event");
        Assertions.assertTrue(beforeIdx < afterIdx,
                "Expected BeforeTestcontainerStart to fire before AfterTestcontainerStart");
    }
}
