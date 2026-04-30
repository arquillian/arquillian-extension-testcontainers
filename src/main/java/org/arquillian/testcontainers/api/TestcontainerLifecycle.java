/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.arquillian.testcontainers.api;

/**
 * Defines the lifecycle scope for a container managed by the Arquillian Testcontainers extension.
 *
 * @author Radoslav Husar
 */
public enum TestcontainerLifecycle {

    /**
     * The container is started once on first encounter and stopped when the test suite ends. The same container
     * instance is shared across all test classes that request the same container type with suite lifecycle.
     */
    SUITE,

    /**
     * The container is started before each test class and stopped after the test class completes. This is the default
     * behavior.
     */
    CLASS,

    /**
     * The container is created and injected but never started or stopped by the framework. The user is responsible for
     * managing the container lifecycle.
     */
    MANUAL,
}
