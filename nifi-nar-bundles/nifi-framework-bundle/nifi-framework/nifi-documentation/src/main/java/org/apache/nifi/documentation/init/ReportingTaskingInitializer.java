/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.documentation.init;

import org.apache.nifi.annotation.lifecycle.OnShutdown;
import org.apache.nifi.components.ConfigurableComponent;
import org.apache.nifi.documentation.ConfigurableComponentInitializer;
import org.apache.nifi.documentation.mock.MockConfigurationContext;
import org.apache.nifi.documentation.mock.MockProcessorLogger;
import org.apache.nifi.documentation.mock.MockReportingInitializationContext;
import org.apache.nifi.documentation.util.ReflectionUtils;
import org.apache.nifi.nar.NarCloseable;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.reporting.ReportingTask;

/**
 * Initializes a ReportingTask using a MockReportingInitializationContext;
 *
 *
 */
public class ReportingTaskingInitializer implements ConfigurableComponentInitializer {

    @Override
    public void initialize(ConfigurableComponent component) throws InitializationException {
        ReportingTask reportingTask = (ReportingTask) component;
        try (NarCloseable narCloseable = NarCloseable.withNarLoader()) {
            reportingTask.initialize(new MockReportingInitializationContext());
        }
    }

    @Override
    public void teardown(ConfigurableComponent component) {
        ReportingTask reportingTask = (ReportingTask) component;
        try (NarCloseable narCloseable = NarCloseable.withNarLoader()) {

            final MockConfigurationContext context = new MockConfigurationContext();
            ReflectionUtils.quietlyInvokeMethodsWithAnnotations(OnShutdown.class, org.apache.nifi.processor.annotation.OnShutdown.class, reportingTask, new MockProcessorLogger(), context);
        }
    }
}
