/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.arquillian.testcontainers.api.event;

import org.testcontainers.containers.GenericContainer;

/**
 * Fired before a managed testcontainer is started.
 *
 * @author Radoslav Husar
 */
public class BeforeTestcontainerStart extends TestcontainerEvent {

    public BeforeTestcontainerStart(final GenericContainer<?> container) {
        super(container);
    }
}
