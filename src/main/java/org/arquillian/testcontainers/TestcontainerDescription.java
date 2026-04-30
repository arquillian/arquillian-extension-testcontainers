/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.arquillian.testcontainers;

import org.arquillian.testcontainers.api.Testcontainer;
import org.testcontainers.containers.GenericContainer;

/**
 * A holder for information about the Testcontainer being injected into a field.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class TestcontainerDescription {

    /**
     * The annotation that was on the field
     */
    final Testcontainer testcontainer;

    /**
     * The name of the container, or an empty string for unnamed containers
     */
    final String name;

    /**
     * The instance of the container created
     */
    final GenericContainer<?> instance;

    TestcontainerDescription(final Testcontainer testcontainer, final GenericContainer<?> instance) {
        this.testcontainer = testcontainer;
        this.name = testcontainer.name();
        this.instance = instance;
    }
}
