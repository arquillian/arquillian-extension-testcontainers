/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.arquillian.testcontainers.api.event;

import org.testcontainers.containers.GenericContainer;

/**
 * Fired before a testcontainer is stopped.
 *
 * @author Radoslav Husar
 */
public class BeforeTestcontainerStop extends TestcontainerEvent {

    public BeforeTestcontainerStop(final GenericContainer<?> container) {
        super(container);
    }
}
