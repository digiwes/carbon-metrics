/*
 * Copyright 2014 WSO2 Inc. (http://wso2.org)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.metrics.impl;

import junit.framework.TestCase;

import org.wso2.carbon.metrics.manager.Level;
import org.wso2.carbon.metrics.manager.Meter;
import org.wso2.carbon.metrics.manager.MetricManager;
import org.wso2.carbon.metrics.manager.MetricService;
import org.wso2.carbon.metrics.manager.internal.ServiceReferenceHolder;

/**
 * Test Cases for {@link MetricService}
 */
public class MetricServiceTest extends TestCase {

    private static MetricService metricService;

    protected void setUp() throws Exception {
        super.setUp();
        metricService = new MetricServiceImpl(Utils.getConfiguration(), Utils.getLevelConfiguration());
        metricService.setRootLevel(Level.OFF);
        ServiceReferenceHolder.getInstance().setMetricService(metricService);
    }

    public void testMeterInitialCount() {
        Meter meter = MetricManager.meter(Level.INFO, MetricManager.name(this.getClass(), "test-initial-count"));
        assertEquals("Initial count should be zero", 0, meter.getCount());
        assertTrue("Metrics Count is not zero", metricService.getMetricsCount() > 0);
    }

    public void testDuplicateMetric() {
        String name = "test-name";
        MetricManager.meter(Level.INFO, MetricManager.name(this.getClass(), name));

        try {
            // Different Level
            MetricManager.meter(Level.DEBUG, MetricManager.name(this.getClass(), name));
            fail("Meter should not be created");
        } catch (IllegalArgumentException e) {
            // Ignore
        }

        try {
            // Different Metric Type
            MetricManager.counter(Level.INFO, MetricManager.name(this.getClass(), name));
            fail("Counter should not be created");
        } catch (IllegalArgumentException e) {
            // Ignore
        }
    }

    public void testEnableDisable() {
        assertTrue("Metric Service is enabled", metricService.isEnabled());
        Meter meter = MetricManager.meter(Level.INFO, MetricManager.name(this.getClass(), "test-enabled"));

        metricService.setRootLevel(Level.TRACE);
        meter.mark();
        assertEquals("Count should be one", 1, meter.getCount());

        metricService.disable();
        assertFalse("Metric Service is disabled", metricService.isEnabled());

        meter.mark();
        assertEquals("Count should be one", 1, meter.getCount());

        metricService.enable();
        meter.mark();
        assertEquals("Count should be two", 2, meter.getCount());
    }

    public void testMetricSetLevel() {
        String name = MetricManager.name(this.getClass(), "test-metric-level");
        Meter meter = MetricManager.meter(Level.INFO, name);
        assertNull("There should no configured level", metricService.getMetricLevel(name));

        metricService.setRootLevel(Level.TRACE);
        meter.mark();
        assertEquals("Count should be one", 1, meter.getCount());

        metricService.setMetricLevel(name, Level.INFO);
        assertEquals("Configured level should be INFO", Level.INFO, metricService.getMetricLevel(name));
        meter.mark();
        assertEquals("Count should be two", 2, meter.getCount());

        metricService.setMetricLevel(name, Level.DEBUG);
        assertEquals("Configured level should be DEBUG", Level.DEBUG, metricService.getMetricLevel(name));
        meter.mark();
        assertEquals("Count should be three", 3, meter.getCount());

        metricService.setMetricLevel(name, Level.TRACE);
        assertEquals("Configured level should be TRACE", Level.TRACE, metricService.getMetricLevel(name));
        meter.mark();
        assertEquals("Count should be four", 4, meter.getCount());

        metricService.setMetricLevel(name, Level.ALL);
        assertEquals("Configured level should be ALL", Level.ALL, metricService.getMetricLevel(name));
        meter.mark();
        assertEquals("Count should be five", 5, meter.getCount());

        metricService.setMetricLevel(name, Level.OFF);
        assertEquals("Configured level should be OFF", Level.OFF, metricService.getMetricLevel(name));
        meter.mark();
        assertEquals("Count should be five", 5, meter.getCount());

        metricService.setMetricLevel(name, Level.INFO);
        assertEquals("Configured level should be INFO", Level.INFO, metricService.getMetricLevel(name));
        meter.mark();
        assertEquals("Count should be six", 6, meter.getCount());
    }

    public void testMetricServiceLevels() {
        Meter meter = MetricManager.meter(Level.INFO, MetricManager.name(this.getClass(), "test-levels"));
        meter.mark();
        // This is required as we need to check whether level changes are applied to existing metrics
        assertEquals("Count should be zero", 0, meter.getCount());

        metricService.setRootLevel(Level.TRACE);
        meter.mark();
        assertEquals("Count should be one", 1, meter.getCount());

        metricService.setRootLevel(Level.DEBUG);
        meter.mark();
        assertEquals("Count should be two", 2, meter.getCount());

        metricService.setRootLevel(Level.INFO);
        meter.mark();
        assertEquals("Count should be three", 3, meter.getCount());

        metricService.setRootLevel(Level.ALL);
        meter.mark();
        assertEquals("Count should be four", 4, meter.getCount());

        metricService.setRootLevel(Level.OFF);
        meter.mark();
        // There should be no change
        assertEquals("Count should be four", 4, meter.getCount());
    }

    public void testMetricLevels() {
        Meter meter = MetricManager.meter(Level.OFF, MetricManager.name(this.getClass(), "test1"));
        meter.mark();
        assertEquals("Count should be zero", 0, meter.getCount());
        metricService.setRootLevel(Level.OFF);
        meter.mark();
        assertEquals("Count should be zero", 0, meter.getCount());

        metricService.setRootLevel(Level.TRACE);
        meter = MetricManager.meter(Level.TRACE, MetricManager.name(this.getClass(), "test2"));
        meter.mark();
        assertEquals("Count should be one", 1, meter.getCount());

        meter = MetricManager.meter(Level.DEBUG, MetricManager.name(this.getClass(), "test3"));
        meter.mark();
        assertEquals("Count should be one", 1, meter.getCount());

        metricService.setRootLevel(Level.DEBUG);
        meter = MetricManager.meter(Level.TRACE, MetricManager.name(this.getClass(), "test4"));
        meter.mark();
        assertEquals("Count should be zero", 0, meter.getCount());

        meter = MetricManager.meter(Level.DEBUG, MetricManager.name(this.getClass(), "test5"));
        meter.mark(100);
        assertEquals("Corg.wso2.carbon.metrics.impl.MetricServiceTestount should be one hundred", 100, meter.getCount());

    }
}
