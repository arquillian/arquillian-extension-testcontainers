/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.arquillian.testcontainers.spi.event;

import org.arquillian.testcontainers.api.TestcontainerEventContext;

/**
 * Fired before a managed testcontainer is started.
 *
 * @author Radoslav Husar
 */
public class BeforeTestcontainerStart extends TestcontainerEvent {

    public BeforeTestcontainerStart(final TestcontainerEventContext context) {
        super(context);
    }
}
