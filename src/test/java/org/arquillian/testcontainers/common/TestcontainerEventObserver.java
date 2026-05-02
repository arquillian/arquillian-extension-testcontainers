/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.arquillian.testcontainers.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.arquillian.testcontainers.spi.event.AfterTestcontainerStart;
import org.arquillian.testcontainers.spi.event.AfterTestcontainerStop;
import org.arquillian.testcontainers.spi.event.BeforeTestcontainerStart;
import org.arquillian.testcontainers.spi.event.BeforeTestcontainerStop;
import org.arquillian.testcontainers.spi.event.TestcontainerEvent;
import org.jboss.arquillian.core.api.annotation.Observes;

/**
 * @author Radoslav Husar
 */
@SuppressWarnings("unused")
public class TestcontainerEventObserver {

    private static final List<TestcontainerEvent> events = new ArrayList<>();

    public void beforeStart(@Observes BeforeTestcontainerStart event) {
        events.add(event);
    }

    public void afterStart(@Observes AfterTestcontainerStart event) {
        events.add(event);
    }

    public void beforeStop(@Observes BeforeTestcontainerStop event) {
        events.add(event);
    }

    public void afterStop(@Observes AfterTestcontainerStop event) {
        events.add(event);
    }

    public static List<TestcontainerEvent> events() {
        return Collections.unmodifiableList(events);
    }

    public static void clear() {
        events.clear();
    }
}
