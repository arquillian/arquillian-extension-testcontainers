/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.arquillian.testcontainers.spi.event;

import org.arquillian.testcontainers.api.TestcontainerEventContext;

/**
 * Fired after a testcontainer has been stopped.
 *
 * @author Radoslav Husar
 */
public class AfterTestcontainerStop extends TestcontainerEvent {

    public AfterTestcontainerStop(final TestcontainerEventContext context) {
        super(context);
    }
}
