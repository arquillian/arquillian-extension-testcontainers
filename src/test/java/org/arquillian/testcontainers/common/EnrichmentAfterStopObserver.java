/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.arquillian.testcontainers.common;

import java.util.ArrayList;
import java.util.List;

import org.arquillian.testcontainers.NoRestartAfterStopTest;
import org.arquillian.testcontainers.spi.event.AfterTestcontainerStop;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.EventContext;
import org.jboss.arquillian.test.spi.event.enrichment.AfterEnrichment;
import org.jboss.arquillian.test.spi.event.enrichment.EnrichmentEvent;
import org.jboss.arquillian.test.spi.event.suite.AfterClass;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;
import org.testcontainers.containers.GenericContainer;

/**
 * Observer which simulates other extensions enriching objects during the {@link AfterClass} event after the containers
 * were already stopped, e.g. WildFly Arquillian enriching server setup tasks before tearing them down. Fails the test
 * class if a stopped container was started again.
 *
 * @author Radoslav Husar
 */
@SuppressWarnings("unused")
public class EnrichmentAfterStopObserver {

    private final List<GenericContainer<?>> stoppedContainers = new ArrayList<>();

    @Inject
    private Event<EnrichmentEvent> enrichmentEvent;

    public void beforeClass(@Observes BeforeClass event) {
        stoppedContainers.clear();
    }

    public void afterStop(@Observes AfterTestcontainerStop event) {
        stoppedContainers.add(event.getContainer());
    }

    // Higher precedence wraps the interceptor stopping the containers, which have been stopped once this proceeds,
    // while lower precedence than the class context interceptor keeps the class scoped container registry available
    public void afterClass(@Observes(precedence = 50) EventContext<AfterClass> context) throws NoSuchMethodException {
        context.proceed();
        if (context.getEvent().getTestClass().getJavaClass() != NoRestartAfterStopTest.class) {
            return;
        }
        final Object instance = new Object();
        enrichmentEvent.fire(new AfterEnrichment(instance, Object.class.getMethod("toString")));
        for (GenericContainer<?> container : stoppedContainers) {
            if (container.isRunning()) {
                // Do not leak the container
                container.stop();
                throw new AssertionError(String.format("Container %s was started again after it was stopped",
                        container.getDockerImageName()));
            }
        }
    }
}
