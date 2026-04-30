/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.arquillian.testcontainers.api;

import org.testcontainers.containers.GenericContainer;

/**
 * Describes a testcontainer in the context of a lifecycle event. Provides the container name, lifecycle scope,
 * and the container instance.
 *
 * @author Radoslav Husar
 */
public class TestcontainerEventContext {

    private final String name;
    private final TestcontainerLifecycle lifecycle;
    private final GenericContainer<?> container;

    public TestcontainerEventContext(final String name, final TestcontainerLifecycle lifecycle,
            final GenericContainer<?> container) {
        this.name = name;
        this.lifecycle = lifecycle;
        this.container = container;
    }

    /**
     * Returns the name of the container, or an empty string if unnamed.
     *
     * @return the container name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the lifecycle scope of this container.
     *
     * @return the lifecycle scope
     */
    public TestcontainerLifecycle getLifecycle() {
        return lifecycle;
    }

    /**
     * Returns whether this container's lifecycle is managed by Arquillian (i.e. not {@link TestcontainerLifecycle#MANUAL}).
     *
     * @return {@code true} if Arquillian manages start/stop
     */
    public boolean isManaged() {
        return lifecycle != TestcontainerLifecycle.MANUAL;
    }

    /**
     * Returns the container instance.
     *
     * @return the container instance
     */
    public GenericContainer<?> getContainer() {
        return container;
    }
}
