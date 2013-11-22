/**
 * Copyright (C) 2013 Microsoft Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.microsoft.reef.driver.context;

import com.microsoft.reef.annotations.Provided;
import com.microsoft.reef.annotations.audience.DriverSide;
import com.microsoft.reef.annotations.audience.Public;
import com.microsoft.reef.evaluator.context.ContextMessageHandler;
import com.microsoft.reef.evaluator.context.ContextMessageSource;
import com.microsoft.reef.evaluator.context.events.ContextStart;
import com.microsoft.reef.evaluator.context.events.ContextStop;
import com.microsoft.reef.runtime.common.driver.contexts.defaults.*;
import com.microsoft.tang.annotations.Name;
import com.microsoft.tang.annotations.NamedParameter;
import com.microsoft.tang.formats.ConfigurationModuleBuilder;
import com.microsoft.wake.EventHandler;

import java.util.Set;

/**
 * Configuration parameters for ContextConfiguration module.
 */
@Public
@DriverSide
@Provided
public class ContextConfigurationOptions extends ConfigurationModuleBuilder {

  @NamedParameter(doc = "The identifier for the context.")
  public class ContextIdentifier implements Name<String> {
  }

  @NamedParameter(doc = "The set of event handlers for the ContextStart event.",
      default_classes = DefaultContextStartHandler.class)
  public class StartHandlers implements Name<Set<EventHandler<ContextStart>>> {
  }

  @NamedParameter(doc = "The set of event handlers for the ContextStop event.",
      default_classes = DefaultContextStopHandler.class)
  public class StopHandlers implements Name<Set<EventHandler<ContextStop>>> {
  }

  @NamedParameter(doc = "The set of ContextMessageSource implementations called during heartbeats.",
      default_classes = DefaultContextMessageSource.class)
  public class ContextMessageSources implements Name<Set<ContextMessageSource>> {
  }

  @NamedParameter(doc = "The set of Context message handlers.",
      default_classes = DefaultContextMessageHandler.class)
  public class ContextMessageHandlers implements Name<Set<ContextMessageHandler>> {
  }
}
