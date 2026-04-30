/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.arquillian.testcontainers.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import org.testcontainers.containers.GenericContainer;

/**
 * Used to annotate a field which <strong>must</strong> be an instance of a {@link GenericContainer}. A
 * {@link TestcontainersRequired} annotation must be present on the type to use Testcontainer injection.
 *
 * <pre>
 * &#064;RunWith(Arquillian.class)
 * &#064;TestcontainersRequired
 * &#064;RunAsClient
 * public class ContainerTest {
 *
 *     &#064;Testcontainer
 *     private CustomTestContainer container;
 *
 *     &#064;Deployment
 *     public static JavaArchive createDeployment() {
 *         return ShrinkWrap.create(JavaArchive.class)
 *                 .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
 *     }
 *
 *     &#064;Test
 *     public void testContainerInjected() {
 *         Assertions.assertNotNull(container, "Expected the container to be injected.");
 *         Assertions.assertTrue(container.isRunning(), "Expected the container to be running");
 *     }
 * }
 * </pre>
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Testcontainer {

    /**
     * Defines the lifecycle scope for this Testcontainer.
     * <ul>
     * <li>{@link TestcontainerLifecycle#SUITE SUITE} - the container is started once on first encounter and stopped
     * when the test suite ends; the same instance is shared across test classes</li>
     * <li>{@link TestcontainerLifecycle#CLASS CLASS} - the container is started before each test class and stopped
     * after it completes (default)</li>
     * <li>{@link TestcontainerLifecycle#MANUAL MANUAL} - the container is created and injected but never started or
     * stopped by the extension</li>
     * </ul>
     *
     * @return the lifecycle scope for this Testcontainer
     */
    TestcontainerLifecycle value() default TestcontainerLifecycle.CLASS;

    /**
     * An optional name for the container. When set, the container is registered and looked up by this name, allowing
     * multiple containers of the same type to coexist in a single test class.
     * <p>
     * If left as the default empty string, the container is looked up by type and qualifiers as usual.
     * </p>
     *
     * @return the name of the container, or an empty string for unnamed containers
     */
    String name() default "";

    /**
     * The type used to create the value for the field. The type must have a no-arg constructor.
     * <p>
     * If left as the default value, {@link GenericContainer}, the type to construct is derived from the
     * {@linkplain Field#getType() field}.
     * </p>
     *
     * @return the type to construct
     */
    Class<? extends GenericContainer> type() default GenericContainer.class;
}
