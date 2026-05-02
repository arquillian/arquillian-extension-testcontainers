/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.arquillian.testcontainers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.arquillian.testcontainers.api.Testcontainer;
import org.arquillian.testcontainers.api.TestcontainersRequired;
import org.arquillian.testcontainers.common.SimpleTestContainer;
import org.arquillian.testcontainers.common.TestcontainerEventObserver;
import org.arquillian.testcontainers.spi.event.BeforeTestcontainerStart;
import org.arquillian.testcontainers.spi.event.TestcontainerEvent;
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

@ExtendWith(ArquillianExtension.class)
@TestcontainersRequired(TestAbortedException.class)
@RunAsClient
public class NamedContainerTest {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Testcontainer(name = "first")
    private SimpleTestContainer first;

    @Testcontainer(name = "first")
    private SimpleTestContainer firstAgain;

    @Testcontainer(name = "second")
    private SimpleTestContainer second;

    @Testcontainer
    private SimpleTestContainer unnamed;

    @Test
    public void checkAllInjected() {
        Assertions.assertNotNull(first, "Expected the first container to be injected.");
        Assertions.assertNotNull(second, "Expected the second container to be injected.");
        Assertions.assertNotNull(unnamed, "Expected the unnamed container to be injected.");
    }

    @Test
    public void checkAllRunning() {
        Assertions.assertTrue(first.isRunning(), "Expected the first container to be running.");
        Assertions.assertTrue(second.isRunning(), "Expected the second container to be running.");
        Assertions.assertTrue(unnamed.isRunning(), "Expected the unnamed container to be running.");
    }

    @Test
    public void checkAllDifferentInstances() {
        Assertions.assertNotSame(first, second, "Expected named containers to be different instances.");
        Assertions.assertNotSame(first, unnamed, "Expected named and unnamed containers to be different instances.");
        Assertions.assertNotSame(second, unnamed, "Expected named and unnamed containers to be different instances.");
    }

    @Test
    public void sameNameYieldsSameInstance() {
        Assertions.assertSame(first, firstAgain, "Fields sharing a name must resolve to the same instance.");
    }

    @Test
    public void startEventsCarryContainerNames() {
        List<TestcontainerEvent> events = TestcontainerEventObserver.events();
        Set<String> names = events.stream()
                .filter(e -> e instanceof BeforeTestcontainerStart)
                .map(e -> e.getContext().getName())
                .collect(Collectors.toSet());
        Assertions.assertTrue(names.contains("first"), "Expected event with name 'first'");
        Assertions.assertTrue(names.contains("second"), "Expected event with name 'second'");
        Assertions.assertTrue(names.contains(""), "Expected event with empty name for unnamed container");
    }
}
