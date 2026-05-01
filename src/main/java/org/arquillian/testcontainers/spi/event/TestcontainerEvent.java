/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.arquillian.testcontainers.spi.event;

import org.arquillian.testcontainers.api.TestcontainerEventContext;
import org.jboss.arquillian.core.spi.event.Event;
import org.testcontainers.containers.GenericContainer;

/**
 * Base class for testcontainer lifecycle events.
 *
 * @author Radoslav Husar
 */
public abstract class TestcontainerEvent implements Event {

    private final TestcontainerEventContext context;

    protected TestcontainerEvent(final TestcontainerEventContext context) {
        this.context = context;
    }

    /**
     * Returns the context for the container associated with this event.
     *
     * @return the container event context
     */
    public TestcontainerEventContext getContext() {
        return context;
    }

    /**
     * Returns the container instance associated with this event.
     *
     * @return the container instance
     */
    public GenericContainer<?> getContainer() {
        return context.getContainer();
    }
}
