/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.arquillian.testcontainers.spi.event;

import org.arquillian.testcontainers.api.TestcontainerEventContext;

/**
 * Fired before a testcontainer is stopped.
 *
 * @author Radoslav Husar
 */
public class BeforeTestcontainerStop extends TestcontainerEvent {

    public BeforeTestcontainerStop(final TestcontainerEventContext context) {
        super(context);
    }
}
