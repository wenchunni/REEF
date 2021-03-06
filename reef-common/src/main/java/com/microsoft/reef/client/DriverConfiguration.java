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
import com.microsoft.reef.annotations.audience.ClientSide;
import com.microsoft.reef.annotations.audience.Public;
import com.microsoft.reef.driver.activity.*;
import com.microsoft.reef.driver.context.ActiveContext;
import com.microsoft.reef.driver.context.ClosedContext;
import com.microsoft.reef.driver.context.ContextMessage;
import com.microsoft.reef.driver.context.FailedContext;
import com.microsoft.reef.driver.evaluator.AllocatedEvaluator;
import com.microsoft.reef.driver.evaluator.CompletedEvaluator;
import com.microsoft.reef.driver.evaluator.FailedEvaluator;
import com.microsoft.reef.runtime.common.driver.DriverRuntimeConfiguration;
import com.microsoft.tang.formats.*;
import com.microsoft.wake.EventHandler;
import com.microsoft.wake.time.Clock;
import com.microsoft.wake.time.event.StartTime;
import com.microsoft.wake.time.event.StopTime;

/**
 * A ConfigurationModule for Drivers.
 */
@ClientSide
@Public
@Provided
public final class DriverConfiguration extends ConfigurationModuleBuilder {

  /**
   * Identifies the driver and therefore the JOB. Expect to see this e.g. on YARN's dashboard.
   */
  public static final RequiredParameter<String> DRIVER_IDENTIFIER = new RequiredParameter<>();

  /**
   * The size of the container allocated for the Driver. One of SMALL, MEDIUM, LARGE and XLARGE. Defaults to SMALL.
   */
  public static final OptionalParameter<String> DRIVER_SIZE = new OptionalParameter<>();

  /**
   * Files to be made available on the Driver and all Evaluators.
   */
  public static final OptionalParameter<String> GLOBAL_FILES = new OptionalParameter<>();

  /**
   * Libraries to be made available on the Driver and all Evaluators.
   */
  public static final OptionalParameter<String> GLOBAL_LIBRARIES = new OptionalParameter<>();

  /**
   * Files to be made available on the Driver only.
   */
  public static final OptionalParameter<String> LOCAL_FILES = new OptionalParameter<>();

  /**
   * Libraries to be made available on the Driver only.
   */
  public static final OptionalParameter<String> LOCAL_LIBRARIES = new OptionalParameter<>();

  /**
   * The event handler invoked right after the driver boots up.
   */
  public static final RequiredImpl<EventHandler<StartTime>> ON_DRIVER_STARTED = new RequiredImpl<>();

  /**
   * The event handler invoked right before the driver shuts down. Defaults to ignore.
   */
  public static final OptionalImpl<EventHandler<StopTime>> ON_DRIVER_STOP = new OptionalImpl<>();

  // ***** EVALUATOR HANDLER BINDINGS:

  /**
   * Event handler for allocated evaluators. Defaults to returning the evaluator if not bound.
   */
  public static final OptionalImpl<EventHandler<AllocatedEvaluator>> ON_EVALUATOR_ALLOCATED = new OptionalImpl<>();

  /**
   * Event handler for completed evaluators. Defaults to logging if not bound.
   */
  public static final OptionalImpl<EventHandler<CompletedEvaluator>> ON_EVALUATOR_COMPLETED = new OptionalImpl<>();

  /**
   * Event handler for failed evaluators. Defaults to job failure if not bound.
   */
  public static final OptionalImpl<EventHandler<FailedEvaluator>> ON_EVALUATOR_FAILED = new OptionalImpl<>();

  // ***** ACTIVITY HANDLER BINDINGS:

  /**
   * Event handler for activity messages. Defaults to logging if not bound.
   */
  public static final OptionalImpl<EventHandler<ActivityMessage>> ON_ACTIVITY_MESSAGE = new OptionalImpl<>();

  /**
   * Event handler for completed activities. Defaults to closing the context the activity ran on if not bound.
   */
  public static final OptionalImpl<EventHandler<CompletedActivity>> ON_ACTIVITY_COMPLETED = new OptionalImpl<>();

  /**
   * Event handler for failed activities. Defaults to job failure if not bound.
   */
  public static final OptionalImpl<EventHandler<FailedActivity>> ON_ACTIVITY_FAILED = new OptionalImpl<>();

  /**
   * Event handler for running activities. Defaults to logging if not bound.
   */
  public static final OptionalImpl<EventHandler<RunningActivity>> ON_ACTIVITY_RUNNING = new OptionalImpl<>();

  /**
   * Event handler for suspended activities. Defaults to job failure if not bound. Rationale: many jobs don't support
   * activity suspension. Hence, this parameter should be optional. The only sane default is to crash the job, then.
   */
  public static final OptionalImpl<EventHandler<SuspendedActivity>> ON_ACTIVITY_SUSPENDED = new OptionalImpl<>();

  // ***** CLIENT HANDLER BINDINGS:

  /**
   * Event handler for client messages. Defaults to logging if not bound.
   */
  public static final OptionalImpl<EventHandler<byte[]>> ON_CLIENT_MESSAGE = new OptionalImpl<>();

  /**
   * Event handler for close messages sent by the client. Defaults to job failure if not bound.
   */
  public static final OptionalImpl<EventHandler<Void>> ON_CLIENT_CLOSED = new OptionalImpl<>();

  /**
   * Event handler for close messages sent by the client. Defaults to job failure if not bound.
   */
  public static final OptionalImpl<EventHandler<byte[]>> ON_CLIENT_CLOSED_MESSAGE = new OptionalImpl<>();

  // ***** CONTEXT HANDLER BINDINGS:

  /**
   * Event handler for active context. Defaults to closing the context if not bound.
   */
  public static final OptionalImpl<EventHandler<ActiveContext>> ON_CONTEXT_ACTIVE = new OptionalImpl<>();

  /**
   * Event handler for closed context. Defaults to logging if not bound.
   */
  public static final OptionalImpl<EventHandler<ClosedContext>> ON_CONTEXT_CLOSED = new OptionalImpl<>();

  /**
   * Event handler for closed context. Defaults to job failure if not bound.
   */
  public static final OptionalImpl<EventHandler<FailedContext>> ON_CONTEXT_FAILED = new OptionalImpl<>();

  /**
   * Event handler for context messages. Defaults to logging if not bound.
   */
  public static final OptionalImpl<EventHandler<ContextMessage>> ON_CONTEXT_MESSAGE = new OptionalImpl<>();

  /**
   * ConfigurationModule to fill out to get a legal Driver Configuration.
   */
  public static final ConfigurationModule CONF = new DriverConfiguration().merge(DriverRuntimeConfiguration.CONF)

      .bindNamedParameter(DriverConfigurationOptions.DriverIdentifier.class, DRIVER_IDENTIFIER)
      .bindNamedParameter(DriverConfigurationOptions.DriverSize.class, DRIVER_SIZE)
      .bindSetEntry(DriverConfigurationOptions.GlobalFiles.class, GLOBAL_FILES)
      .bindSetEntry(DriverConfigurationOptions.GlobalLibraries.class, GLOBAL_LIBRARIES)
      .bindSetEntry(DriverConfigurationOptions.LocalFiles.class, LOCAL_FILES)
      .bindSetEntry(DriverConfigurationOptions.LocalLibraries.class, LOCAL_LIBRARIES)

          // Driver start/stop handlers
      .bindSetEntry(Clock.StartHandler.class, ON_DRIVER_STARTED)
      .bindSetEntry(Clock.StopHandler.class, ON_DRIVER_STOP)

          // Evaluator handlers
      .bindSetEntry(DriverConfigurationOptions.AllocatedEvaluatorHandlers.class, ON_EVALUATOR_ALLOCATED)
      .bindSetEntry(DriverConfigurationOptions.CompletedEvaluatorHandlers.class, ON_EVALUATOR_COMPLETED)
      .bindSetEntry(DriverConfigurationOptions.FailedEvaluatorHandlers.class, ON_EVALUATOR_FAILED)

          // Activity handlers
      .bindSetEntry(DriverConfigurationOptions.RunningActivityHandlers.class, ON_ACTIVITY_RUNNING)
      .bindSetEntry(DriverConfigurationOptions.FailedActivityHandlers.class, ON_ACTIVITY_FAILED)
      .bindSetEntry(DriverConfigurationOptions.ActivityMessageHandlers.class, ON_ACTIVITY_MESSAGE)
      .bindSetEntry(DriverConfigurationOptions.CompletedActivityHandlers.class, ON_ACTIVITY_COMPLETED)
      .bindSetEntry(DriverConfigurationOptions.SuspendedActivityHandlers.class, ON_ACTIVITY_SUSPENDED)

          // Context handlers
      .bindSetEntry(DriverConfigurationOptions.ActiveContextHandlers.class, ON_CONTEXT_ACTIVE)
      .bindSetEntry(DriverConfigurationOptions.ClosedContextHandlers.class, ON_CONTEXT_CLOSED)
      .bindSetEntry(DriverConfigurationOptions.ContextMessageHandlers.class, ON_CONTEXT_MESSAGE)
      .bindSetEntry(DriverConfigurationOptions.FailedContextHandlers.class, ON_CONTEXT_FAILED)

          // Client handlers
      .bindSetEntry(DriverConfigurationOptions.ClientMessageHandlers.class, ON_CLIENT_MESSAGE)
      .bindSetEntry(DriverConfigurationOptions.ClientCloseHandlers.class, ON_CLIENT_CLOSED)
      .bindSetEntry(DriverConfigurationOptions.ClientCloseWithMessageHandlers.class, ON_CLIENT_CLOSED_MESSAGE)

      .build();
}
