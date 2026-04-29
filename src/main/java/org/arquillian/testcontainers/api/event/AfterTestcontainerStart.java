/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.arquillian.testcontainers.api.event;

import org.testcontainers.containers.GenericContainer;

/**
 * Fired after a managed testcontainer has been started.
 *
 * @author Radoslav Husar
 */
public class AfterTestcontainerStart extends TestcontainerEvent {

    public AfterTestcontainerStart(final GenericContainer<?> container) {
        super(container);
    }
}
