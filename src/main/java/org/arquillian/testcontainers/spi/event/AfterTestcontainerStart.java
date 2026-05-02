/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.arquillian.testcontainers.spi.event;

import org.arquillian.testcontainers.api.TestcontainerEventContext;

/**
 * Fired after a managed testcontainer has been started.
 *
 * @author Radoslav Husar
 */
public class AfterTestcontainerStart extends TestcontainerEvent {

    public AfterTestcontainerStart(final TestcontainerEventContext context) {
        super(context);
    }
}
