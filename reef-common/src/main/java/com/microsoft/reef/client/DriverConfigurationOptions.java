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
package com.microsoft.reef.client;

import com.microsoft.reef.annotations.Provided;
import com.microsoft.reef.annotations.audience.Private;
import com.microsoft.reef.driver.activity.*;
import com.microsoft.reef.driver.context.ActiveContext;
import com.microsoft.reef.driver.context.ClosedContext;
import com.microsoft.reef.driver.context.ContextMessage;
import com.microsoft.reef.driver.context.FailedContext;
import com.microsoft.reef.driver.evaluator.AllocatedEvaluator;
import com.microsoft.reef.driver.evaluator.CompletedEvaluator;
import com.microsoft.reef.driver.evaluator.FailedEvaluator;
import com.microsoft.reef.runtime.common.driver.defaults.*;
import com.microsoft.tang.annotations.Name;
import com.microsoft.tang.annotations.NamedParameter;
import com.microsoft.wake.EventHandler;

import java.util.Set;

/**
 * Hosts all named parameters for Drivers.
 */
@Private
@Provided
public final class DriverConfigurationOptions {
  @NamedParameter(doc = "Driver Size", default_value = "SMALL")
  public static final class DriverSize implements Name<String> {
  }

  @NamedParameter(doc = "Driver Identifier", default_value = "Unnamed REEF Job")
  public static final class DriverIdentifier implements Name<String> {
  }

  @NamedParameter(doc = "Libraries to be made accessible on the Driver and all Evaluators.")
  public static final class GlobalLibraries implements Name<Set<String>> {
  }

  @NamedParameter(doc = "Files to be made accessible on the Driver and all Evaluators.")
  public static final class GlobalFiles implements Name<Set<String>> {
  }

  @NamedParameter(doc = "Libraries to be made accessible on the Driver only.")
  public static final class LocalLibraries implements Name<Set<String>> {
  }

  @NamedParameter(doc = "Files to be made accessible on the Driver only.")
  public static final class LocalFiles implements Name<Set<String>> {
  }

  @NamedParameter(doc = "Called when an exception occurs on a running evaluator.", default_classes = DefaultEvaluatorFailureHandler.class)
  public final static class FailedEvaluatorHandlers implements Name<Set<EventHandler<FailedEvaluator>>> {
  }

  @NamedParameter(doc = "Called when an exception occurs on a running evaluator.", default_classes = DefaultEvaluatorCompletionHandler.class)
  public final static class CompletedEvaluatorHandlers implements Name<Set<EventHandler<CompletedEvaluator>>> {
  }

  @NamedParameter(doc = "Called when an allocated evaluator is given to the client.", default_classes = DefaultEvaluatorAllocationHandler.class)
  public final static class AllocatedEvaluatorHandlers implements Name<Set<EventHandler<AllocatedEvaluator>>> {
  }

  @NamedParameter(doc = "Running activity handler.", default_classes = DefaultActivityRunningHandler.class)
  public final static class RunningActivityHandlers implements Name<Set<EventHandler<RunningActivity>>> {
  }

  @NamedParameter(doc = "Activity exception handler.", default_classes = DefaultActivityFailureHandler.class)
  public final static class FailedActivityHandlers implements Name<Set<EventHandler<FailedActivity>>> {
  }

  @NamedParameter(doc = "Activity message handler.", default_classes = DefaultActivityMessageHandler.class)
  public final static class ActivityMessageHandlers implements Name<Set<EventHandler<ActivityMessage>>> {
  }

  @NamedParameter(doc = "Completed activity handler.", default_classes = DefaultActivityCompletionHandler.class)
  public final static class CompletedActivityHandlers implements Name<Set<EventHandler<CompletedActivity>>> {
  }

  @NamedParameter(doc = "Suspended activity handler.", default_classes = DefaultActivitySuspensionHandler.class)
  public final static class SuspendedActivityHandlers implements Name<Set<EventHandler<SuspendedActivity>>> {
  }

  @NamedParameter(doc = "Job message handler.", default_classes = DefaultClientMessageHandler.class)
  public final static class ClientMessageHandlers implements Name<Set<EventHandler<byte[]>>> {
  }

  @NamedParameter(doc = "Running job handler.", default_classes = DefaultClientCloseHandler.class)
  public final static class ClientCloseHandlers implements Name<Set<EventHandler<Void>>> {
  }

  @NamedParameter(doc = "Completed job handler.", default_classes = DefaultClientCloseWithMessageHandler.class)
  public final static class ClientCloseWithMessageHandlers implements Name<Set<EventHandler<byte[]>>> {
    // TODO: Merge the two event handlers into a proper event.
  }

  @NamedParameter(doc = "Handler for EvaluatorContext", default_classes = DefaultContextActiveHandler.class)
  public static final class ActiveContextHandlers implements Name<Set<EventHandler<ActiveContext>>> {
  }

  @NamedParameter(doc = "Handler for ClosedContext", default_classes = DefaultContextClosureHandler.class)
  public static final class ClosedContextHandlers implements Name<Set<EventHandler<ClosedContext>>> {
  }

  @NamedParameter(doc = "Handler for FailedContext", default_classes = DefaultContextFailureHandler.class)
  public static final class FailedContextHandlers implements Name<Set<EventHandler<FailedContext>>> {
  }

  @NamedParameter(doc = "Context message handler.", default_classes = DefaultContextMessageHandler.class)
  public final static class ContextMessageHandlers implements Name<Set<EventHandler<ContextMessage>>> {
  }

  @NamedParameter(doc = "The EventHandler that gets preemption events.")
  public static final class PreemptionHandler implements Name<PreemptionHandler> {
  }
}
