/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.arquillian.testcontainers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.arquillian.testcontainers.api.TestcontainerEventContext;
import org.arquillian.testcontainers.api.TestcontainerLifecycle;
import org.arquillian.testcontainers.api.TestcontainersRequired;
import org.arquillian.testcontainers.spi.event.AfterTestcontainerStart;
import org.arquillian.testcontainers.spi.event.AfterTestcontainerStop;
import org.arquillian.testcontainers.spi.event.BeforeTestcontainerStart;
import org.arquillian.testcontainers.spi.event.BeforeTestcontainerStop;
import org.jboss.arquillian.container.spi.ContainerRegistry;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.arquillian.test.spi.annotation.ClassScoped;
import org.jboss.arquillian.test.spi.annotation.SuiteScoped;
import org.jboss.arquillian.test.spi.event.enrichment.AfterEnrichment;
import org.jboss.arquillian.test.spi.event.suite.AfterClass;
import org.jboss.arquillian.test.spi.event.suite.AfterSuite;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;
import org.jboss.arquillian.test.spi.event.suite.BeforeSuite;
import org.testcontainers.DockerClientFactory;

@SuppressWarnings("unused")
class TestContainersObserver {

    private static final String NO_DOCKER_MSG = "No Docker/podman environment is available.";

    @Inject
    @SuiteScoped
    private InstanceProducer<TestcontainerRegistry> suiteContainerRegistry;

    @Inject
    @ClassScoped
    private InstanceProducer<TestcontainerRegistry> classContainerRegistry;

    @Inject
    @ClassScoped
    private InstanceProducer<TestcontainerRegistryView> containerRegistries;

    @Inject
    private Instance<ContainerRegistry> registry;

    @Inject
    private Event<BeforeTestcontainerStart> beforeTestcontainerStart;

    @Inject
    private Event<AfterTestcontainerStart> afterTestcontainerStart;

    @Inject
    private Event<BeforeTestcontainerStop> beforeTestcontainerStop;

    @Inject
    private Event<AfterTestcontainerStop> afterTestcontainerStop;

    /**
     * Creates the suite-scoped {@link TestcontainerRegistry} once before the suite starts.
     *
     * @param beforeSuite the before suite event
     */
    public void createSuiteRegistry(@Observes BeforeSuite beforeSuite) {
        suiteContainerRegistry.set(new TestcontainerRegistry());
    }

    /**
     * This first checks if the {@link TestcontainersRequired} annotation is present on the test class failing if necessary. It
     * then creates the class-scoped {@link TestcontainerRegistry} and the combined {@link TestcontainerRegistryView}.
     *
     * @param beforeClass the before class event
     *
     * @throws Throwable if an error occurs
     */
    public void createContainer(@Observes(precedence = 500) BeforeClass beforeClass) throws Throwable {
        final TestClass javaClass = beforeClass.getTestClass();
        final TestcontainersRequired dockerRequired = javaClass.getAnnotation(TestcontainersRequired.class);
        if (dockerRequired != null) {
            if (!isDockerAvailable()) {
                var throwable = dockerRequired.value();
                final var overrideClass = System.getProperty("org.arquillian.testcontainers.docker.required.exception");
                if (overrideClass != null && !overrideClass.isBlank()) {
                    Class<?> override = Class.forName(overrideClass);
                    if (Throwable.class.isAssignableFrom(override)) {
                        throwable = override.asSubclass(Throwable.class);
                    }
                }
                throw createException(throwable);
            }
        }

        // Read suite registry before setting class registry — both InstanceProducers share the same generic type
        // (TestcontainerRegistry), and .get() resolves hierarchically (most-specific scope wins), so a subsequent
        // .get() on the suite producer would return the class-scoped value instead.
        final TestcontainerRegistry suiteRegistry = suiteContainerRegistry.get();
        final TestcontainerRegistry classRegistry = new TestcontainerRegistry();
        classContainerRegistry.set(classRegistry);

        containerRegistries.set(new TestcontainerRegistryView(suiteRegistry, classRegistry));
    }

    /**
     * Stops class-scoped containers after the test class is complete. Suite-scoped containers are not stopped here.
     *
     * @param afterClass the after class event
     */
    public void stopContainer(@Observes AfterClass afterClass) {
        TestcontainerRegistryView registries = containerRegistries.get();
        if (registries != null) {
            for (TestcontainerDescription container : registries.classRegistry()) {
                if (container.testcontainer.value() == TestcontainerLifecycle.CLASS) {
                    final TestcontainerEventContext context = createContext(container);
                    beforeTestcontainerStop.fire(new BeforeTestcontainerStop(context));
                    container.instance.stop();
                    afterTestcontainerStop.fire(new AfterTestcontainerStop(context));
                }
            }
        }
    }

    /**
     * Starts all containers after enrichment is done. This happens after the {@link ContainerInjectionTestEnricher} is
     * invoked. Suite-scoped containers are only started if they are not already running.
     *
     * @param event the after enrichment event
     */
    public void startContainer(@Observes(precedence = 500) final AfterEnrichment event) {
        TestcontainerRegistryView registries = containerRegistries.get();
        if (registries == null) {
            return;
        }

        for (TestcontainerDescription description : registries.classRegistry()) {
            if (description.testcontainer.value() == TestcontainerLifecycle.CLASS) {
                final TestcontainerEventContext context = createContext(description);
                beforeTestcontainerStart.fire(new BeforeTestcontainerStart(context));
                description.instance.start();
                afterTestcontainerStart.fire(new AfterTestcontainerStart(context));
            }
        }

        for (TestcontainerDescription description : registries.suiteRegistry()) {
            if (!description.instance.isRunning()) {
                final TestcontainerEventContext context = createContext(description);
                beforeTestcontainerStart.fire(new BeforeTestcontainerStart(context));
                description.instance.start();
                afterTestcontainerStart.fire(new AfterTestcontainerStart(context));
            }
        }
    }

    /**
     * Stops all suite-scoped containers when the test suite ends.
     *
     * @param afterSuite the after suite event
     */
    public void stopSuiteContainers(@Observes AfterSuite afterSuite) {
        TestcontainerRegistry suiteRegistry = suiteContainerRegistry.get();
        if (suiteRegistry != null) {
            for (TestcontainerDescription container : suiteRegistry) {
                final TestcontainerEventContext context = createContext(container);
                beforeTestcontainerStop.fire(new BeforeTestcontainerStop(context));
                container.instance.stop();
                afterTestcontainerStop.fire(new AfterTestcontainerStop(context));
            }
        }
    }

    private static TestcontainerEventContext createContext(final TestcontainerDescription description) {
        return new TestcontainerEventContext(description.name, description.testcontainer.value(), description.instance);
    }

    @SuppressWarnings({ "resource", "BooleanMethodIsAlwaysInverted" })
    private boolean isDockerAvailable() {
        try {
            DockerClientFactory.instance().client();
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    private static Throwable createException(final Class<? extends Throwable> value) {
        // First the common path, using the AssertionError(Object) c'tor
        if (value == AssertionError.class) {
            return new AssertionError(NO_DOCKER_MSG);
        }
        // Next try the String.class constructor if there is one
        try {
            final Constructor<? extends Throwable> constructor = value.getConstructor(String.class);
            return constructor.newInstance(NO_DOCKER_MSG);
        } catch (NoSuchMethodException ignore) {
            try {
                final Constructor<? extends Throwable> constructor = value.getConstructor();
                return constructor.newInstance();
            } catch (NoSuchMethodException unused) {
                throw new AssertionError(String.format(
                        NO_DOCKER_MSG + " (No String or no-arg constructor found for desired failure type %s)", value));
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new AssertionError(
                        String.format(NO_DOCKER_MSG + " (Failed to create exception for desired failure type %s)", value), e);
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new AssertionError(
                    String.format(NO_DOCKER_MSG + " (Failed to create exception for desired failure type %s)", value), e);
        }
    }
}
