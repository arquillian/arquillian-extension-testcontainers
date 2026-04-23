/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.arquillian.testcontainers;

import java.lang.annotation.Annotation;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

/**
 * Resource provider enabling {@code @ArquillianResource TestcontainerRegistry} field injection.
 */
public class TestcontainerRegistryResourceProvider implements ResourceProvider {

    @Inject
    private Instance<TestcontainerRegistry> registry;

    @Override
    public boolean canProvide(final Class<?> type) {
        return TestcontainerRegistry.class.isAssignableFrom(type);
    }

    @Override
    public Object lookup(final ArquillianResource resource, final Annotation... qualifiers) {
        final TestcontainerRegistry current = registry.get();
        if (current == null) {
            throw new IllegalStateException(
                    "No TestcontainerRegistry is available in the current context. Ensure the test class is annotated with @TestcontainersRequired.");
        }
        return current;
    }
}
