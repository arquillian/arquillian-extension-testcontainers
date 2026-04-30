/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.arquillian.testcontainers.api.event;

import org.testcontainers.containers.GenericContainer;

/**
 * Fired after a testcontainer has been stopped.
 *
 * @author Radoslav Husar
 */
public class AfterTestcontainerStop extends TestcontainerEvent {

    public AfterTestcontainerStop(final GenericContainer<?> container) {
        super(container);
    }
}
