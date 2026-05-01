/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.arquillian.testcontainers.api;

import org.testcontainers.containers.GenericContainer;

/**
 * Describes a testcontainer in the context of a lifecycle event. Provides the container name, whether it is managed
 * by Arquillian, and the container instance.
 *
 * @author Radoslav Husar
 */
public class TestcontainerEventContext {

    private final String name;
    private final boolean managed;
    private final GenericContainer<?> container;

    public TestcontainerEventContext(final String name, final boolean managed, final GenericContainer<?> container) {
        this.name = name;
        this.managed = managed;
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
     * Returns whether this container's lifecycle is managed by Arquillian. This will be refactored with the
     * implementation of <a href="https://github.com/arquillian/arquillian-extension-testcontainers/issues/126">#126</a>.
     *
     * @return {@code true} if Arquillian manages start/stop
     */
    public boolean isManaged() {
        return managed;
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
