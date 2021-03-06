/*
 * Copyright 2014-2015 WSO2 Inc. (http://wso2.org)
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

import org.wso2.carbon.metrics.manager.Level;
import org.wso2.carbon.metrics.manager.Metric;

/**
 * An abstract class to keep generic behavior for metric instances. This class implements a metric hierarchy
 */
public abstract class AbstractMetric implements Metric {

    /**
     * A flag to indicate whether the metric is enabled
     */
    private volatile boolean enabled;

    /**
     * The level used when creating the metric
     */
    private final Level level;

    private final String name;

    public AbstractMetric(Level level, String name) {
        this.level = level;
        this.name = name;
    }

    public Level getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    protected final boolean isEnabled() {
        return enabled;
    }

    final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
