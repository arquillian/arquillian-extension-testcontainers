/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.arquillian.testcontainers.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.arquillian.testcontainers.StopAfterClassTest;
import org.arquillian.testcontainers.spi.event.AfterTestcontainerStart;
import org.arquillian.testcontainers.spi.event.AfterTestcontainerStop;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.EventContext;
import org.jboss.arquillian.test.spi.event.suite.AfterClass;
import org.testcontainers.containers.GenericContainer;

/**
 * Simulates another extension using the containers during the {@link AfterClass} event and then failing, e.g. a failed
 * undeployment, followed by a checked exception while the containers are stopped. Fails the {@link StopAfterClassTest}
 * if the containers were stopped too early, were not stopped at all, or the original failure was not preserved.
 *
 * @author Radoslav Husar
 */
@SuppressWarnings("unused")
public class StopAfterClassObserver {

    private final List<GenericContainer<?>> containers = new ArrayList<>();
    private RuntimeException failure;

    public void afterStart(@Observes AfterTestcontainerStart event) {
        containers.add(event.getContainer());
    }

    // Negative precedence ensures this is invoked after the observers with the default precedence regardless of the
    // extension registration order
    public void afterClass(@Observes(precedence = -50) AfterClass event) {
        // No container was started, e.g. if Docker is not available
        if (event.getTestClass().getJavaClass() != StopAfterClassTest.class || containers.isEmpty()) {
            return;
        }
        for (GenericContainer<?> container : containers) {
            if (!container.isRunning()) {
                throw new AssertionError("Container was stopped before other AfterClass observers");
            }
        }
        failure = new RuntimeException("Simulated AfterClass failure");
        throw failure;
    }

    // Arquillian propagates checked exceptions thrown by observers as they are
    public void afterStop(@Observes AfterTestcontainerStop event) throws IOException {
        if (failure != null) {
            throw new IOException("Simulated failure while stopping the container");
        }
    }

    // Higher precedence wraps the interceptor stopping the containers, while lower precedence than the class context
    // interceptor keeps the class scoped container registry available
    public void verify(@Observes(precedence = 50) EventContext<AfterClass> context) {
        try {
            try {
                context.proceed();
            } catch (RuntimeException e) {
                if (failure == null) {
                    throw e;
                }
            }
            if (failure == null) {
                return;
            }
            for (GenericContainer<?> container : containers) {
                if (container.isRunning()) {
                    // Do not leak the containers
                    containers.forEach(GenericContainer::stop);
                    throw new AssertionError("Container was not stopped after an AfterClass observer failed");
                }
            }
            if (failure.getSuppressed().length != containers.size()
                    || !Arrays.stream(failure.getSuppressed()).allMatch(IOException.class::isInstance)) {
                throw new AssertionError("Expected the failures while stopping the containers to be suppressed");
            }
        } finally {
            // Reset here rather than on BeforeClass, which is aborted for test classes skipped without Docker
            containers.clear();
            failure = null;
        }
    }
}
