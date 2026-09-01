/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.arquillian.testcontainers;

import java.lang.annotation.Annotation;
import java.util.List;

import org.arquillian.testcontainers.api.Testcontainer;
import org.arquillian.testcontainers.api.TestcontainerScope;
import org.testcontainers.containers.GenericContainer;

/**
 * A unified view over both the suite-scoped and class-scoped {@link TestcontainerRegistry} instances, routing
 * container lookup and creation to the appropriate registry based on the container's configured
 * {@link TestcontainerScope scope}.
 * <p>
 * This class exists because Arquillian's {@link org.jboss.arquillian.core.api.Instance Instance&lt;T&gt;} injection
 * in {@link org.jboss.arquillian.test.spi.TestEnricher TestEnricher} services cannot carry scope annotations
 * ({@code @SuiteScoped}/{@code @ClassScoped}). Injecting {@code Instance<TestcontainerRegistry>} directly would
 * always resolve to the most-specific (class) scope, making it impossible to reach the suite registry. This distinct
 * type is produced as {@code @ClassScoped} and provides a single injection point that holds references to both
 * registries.
 *
 * @author Radoslav Husar
 */
class TestcontainerRegistryView {

    private final TestcontainerRegistry suiteRegistry;
    private final TestcontainerRegistry classRegistry;

    TestcontainerRegistryView(final TestcontainerRegistry suiteRegistry, final TestcontainerRegistry classRegistry) {
        this.suiteRegistry = suiteRegistry;
        this.classRegistry = classRegistry;
    }

    GenericContainer<?> lookupOrCreate(final Class<GenericContainer<?>> type, final Testcontainer testcontainer,
            final List<Annotation> qualifiers) {
        if (testcontainer.scope() == TestcontainerScope.SUITE) {
            return suiteRegistry.lookupOrCreate(type, testcontainer, qualifiers);
        }
        return classRegistry.lookupOrCreate(type, testcontainer, qualifiers);
    }

    TestcontainerRegistry suiteRegistry() {
        return suiteRegistry;
    }

    TestcontainerRegistry classRegistry() {
        return classRegistry;
    }
}
