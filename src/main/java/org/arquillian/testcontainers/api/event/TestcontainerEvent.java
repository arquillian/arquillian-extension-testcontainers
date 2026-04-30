/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.arquillian.testcontainers.api.event;

import org.testcontainers.containers.GenericContainer;

/**
 * Base class for testcontainer lifecycle events.
 *
 * @author Radoslav Husar
 */
public abstract class TestcontainerEvent {

    private final GenericContainer<?> container;

    protected TestcontainerEvent(final GenericContainer<?> container) {
        this.container = container;
    }

    /**
     * Returns the container instance associated with this event.
     *
     * @return the container instance
     */
    public GenericContainer<?> getContainer() {
        return container;
    }
}
