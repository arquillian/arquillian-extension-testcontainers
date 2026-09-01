/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.arquillian.testcontainers.api;

/**
 * Defines the scope for a container managed by the Arquillian Testcontainers extension.
 *
 * @author Radoslav Husar
 */
public enum TestcontainerScope {

    /**
     * The container is started before each test class and stopped after the test class completes. This is the default
     * scope.
     */
    CLASS,

    /**
     * The container is started once on first encounter and stopped when the test suite ends. The same container
     * instance is shared across all test classes that request the same container type with suite scope.
     */
    SUITE,

}
