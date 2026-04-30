/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.arquillian.testcontainers;

import java.lang.reflect.Field;
import java.util.List;

import org.arquillian.testcontainers.api.Testcontainer;
import org.arquillian.testcontainers.common.SimpleTestContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

/**
 * Plain-JUnit unit tests for {@link TestcontainerRegistry} that exercise lookup semantics without
 * starting any Docker containers. Annotation instances are obtained by reading the annotations off
 * fixture fields on this class so the registry can be driven through {@code lookupOrCreate}
 * outside an Arquillian context.
 */
public class TestcontainerRegistryTest {

    @Testcontainer(name = "alpha")
    private SimpleTestContainer alphaFixture;

    @Test
    public void sameNameViaLookupOrCreateReturnsSameInstance() throws Exception {
        TestcontainerRegistry registry = new TestcontainerRegistry();
        GenericContainer<?> first = registry.lookupOrCreate(simpleType(), annotation("alphaFixture"), List.of());
        GenericContainer<?> second = registry.lookupOrCreate(simpleType(), annotation("alphaFixture"), List.of());

        Assertions.assertSame(first, second);
    }

    @Test
    public void lookupReturnsNullForUnknownName() throws Exception {
        TestcontainerRegistry registry = new TestcontainerRegistry();
        registry.lookupOrCreate(simpleType(), annotation("alphaFixture"), List.of());

        Assertions.assertNull(registry.lookup("missing"));
    }

    @Test
    public void emptyNameLookupDoesNotMatchNamedContainers() throws Exception {
        TestcontainerRegistry registry = new TestcontainerRegistry();
        registry.lookupOrCreate(simpleType(), annotation("alphaFixture"), List.of());

        Assertions.assertNull(registry.lookup(""),
                "Empty-string lookup should not return any registered container");
    }

    @Test
    public void lookupTreatsNullNameAsAbsent() throws Exception {
        TestcontainerRegistry registry = new TestcontainerRegistry();
        registry.lookupOrCreate(simpleType(), annotation("alphaFixture"), List.of());

        Assertions.assertNull(registry.lookup((String) null));
    }

    @Test
    public void typedLookupReturnsTypedContainer() throws Exception {
        TestcontainerRegistry registry = new TestcontainerRegistry();
        GenericContainer<?> alpha = registry.lookupOrCreate(simpleType(), annotation("alphaFixture"), List.of());

        SimpleTestContainer typed = registry.lookup("alpha", SimpleTestContainer.class);
        Assertions.assertSame(alpha, typed);
        Assertions.assertNull(registry.lookup("missing", SimpleTestContainer.class));
    }

    @Test
    public void typedLookupThrowsOnTypeMismatch() throws Exception {
        TestcontainerRegistry registry = new TestcontainerRegistry();
        registry.lookupOrCreate(simpleType(), annotation("alphaFixture"), List.of());

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> registry.lookup("alpha", IncompatibleContainer.class));
    }

    /** Distinct {@link GenericContainer} subtype used to exercise the type-mismatch path on typed lookup. */
    private static class IncompatibleContainer extends GenericContainer<IncompatibleContainer> {
    }

    @SuppressWarnings("unchecked")
    private static Class<GenericContainer<?>> simpleType() {
        return (Class<GenericContainer<?>>) (Class<?>) SimpleTestContainer.class;
    }

    private Testcontainer annotation(final String fieldName) throws NoSuchFieldException {
        final Field field = TestcontainerRegistryTest.class.getDeclaredField(fieldName);
        return field.getAnnotation(Testcontainer.class);
    }
}
