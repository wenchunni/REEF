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
package com.microsoft.reef.examples.pool;

import com.microsoft.reef.client.DriverConfiguration;
import com.microsoft.reef.client.DriverLauncher;
import com.microsoft.reef.client.REEF;
import com.microsoft.reef.runtime.local.client.LocalRuntimeConfiguration;
import com.microsoft.reef.runtime.yarn.client.YarnClientConfiguration;
import com.microsoft.reef.util.EnvironmentUtils;
import com.microsoft.reef.util.TANGUtils;
import com.microsoft.tang.Configuration;
import com.microsoft.tang.Injector;
import com.microsoft.tang.JavaConfigurationBuilder;
import com.microsoft.tang.Tang;
import com.microsoft.tang.annotations.Name;
import com.microsoft.tang.annotations.NamedParameter;
import com.microsoft.tang.exceptions.BindException;
import com.microsoft.tang.exceptions.InjectionException;
import com.microsoft.tang.formats.CommandLine;
import com.microsoft.tang.formats.ConfigurationFile;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pool of Evaluators example - main class.
 */
public final class Launch {

  /**
   * This class should not be instantiated.
   */
  private Launch() {
    throw new RuntimeException("Do not instantiate this class!");
  }

  /**
   * Number of REEF worker threads in local mode.
   */
  private static final int NUM_LOCAL_THREADS = 4;

  /**
   * Standard Java logger
   */
  private static final Logger LOG = Logger.getLogger(Launch.class.getName());

  /**
   * Command line parameter: number of Evaluators to request.
   */
  @NamedParameter(doc = "Number of evaluators to request", short_name = "evaluators")
  public static final class NumEvaluators implements Name<Integer> {
  }

  /**
   * Command line parameter: number of Activities to run.
   */
  @NamedParameter(doc = "Number of activities to run", short_name = "activities")
  public static final class NumActivities implements Name<Integer> {
  }

  /**
   * Command line parameter: number of experiments to run.
   */
  @NamedParameter(doc = "Number of seconds to sleep in each activity", short_name = "delay")
  public static final class Delay implements Name<Integer> {
  }

  /**
   * Command line parameter = true to run locally, or false to run on YARN.
   */
  @NamedParameter(doc = "Whether or not to run on the local runtime",
      short_name = "local", default_value = "true")
  public static final class Local implements Name<Boolean> {
  }

  /**
   * Parse the command line arguments.
   *
   * @param args command line arguments, as passed to main()
   * @return Configuration object.
   * @throws BindException configuration error.
   * @throws IOException   error reading the configuration.
   */
  private static Configuration parseCommandLine(final String[] args)
      throws BindException, IOException {
    final JavaConfigurationBuilder confBuilder = Tang.Factory.getTang().newConfigurationBuilder();
    final CommandLine cl = new CommandLine(confBuilder);
    cl.registerShortNameOfClass(Local.class);
    cl.registerShortNameOfClass(NumEvaluators.class);
    cl.registerShortNameOfClass(NumActivities.class);
    cl.registerShortNameOfClass(Delay.class);
    cl.processCommandLine(args);
    return confBuilder.build();
  }

  private static Configuration cloneCommandLineConfiguration(final Configuration commandLineConf)
      throws InjectionException, BindException {
    final Injector injector = Tang.Factory.getTang().newInjector(commandLineConf);
    final JavaConfigurationBuilder cb = Tang.Factory.getTang().newConfigurationBuilder();
    cb.bindNamedParameter(NumEvaluators.class, String.valueOf(injector.getNamedInstance(NumEvaluators.class)));
    cb.bindNamedParameter(NumActivities.class, String.valueOf(injector.getNamedInstance(NumActivities.class)));
    cb.bindNamedParameter(Delay.class, String.valueOf(injector.getNamedInstance(Delay.class)));
    return cb.build();
  }

  /**
   * Parse command line arguments and create TANG configuration ready to be submitted to REEF.
   *
   * @param commandLineConf Parsed command line arguments, as passed into main().
   * @return (immutable) TANG Configuration object.
   * @throws BindException if configuration commandLineInjector fails.
   * @throws InjectionException if configuration commandLineInjector fails.
   */
  private static Configuration getClientConfiguration(final Configuration commandLineConf)
      throws BindException, InjectionException {

    // TODO: Remove the injector, have stuff injected.
    final Injector commandLineInjector = Tang.Factory.getTang().newInjector(commandLineConf);
    final boolean isLocal = commandLineInjector.getNamedInstance(Local.class);
    final Configuration runtimeConfiguration;
    if (isLocal) {
      LOG.log(Level.FINE, "Running on the local runtime");
      runtimeConfiguration = LocalRuntimeConfiguration.CONF
          .set(LocalRuntimeConfiguration.NUMBER_OF_THREADS, NUM_LOCAL_THREADS)
          .build();
    } else {
      LOG.log(Level.FINE, "Running on YARN");
      runtimeConfiguration = YarnClientConfiguration.CONF
          .set(YarnClientConfiguration.REEF_JAR_FILE, EnvironmentUtils.getClassLocationFile(REEF.class))
          .build();
    }
    return TANGUtils.merge(runtimeConfiguration, cloneCommandLineConfiguration(commandLineConf));
  }

  /**
   * Main method that launches the REEF job.
   *
   * @param args command line parameters.
   */
  public static void main(final String[] args) {

    try {

      final Configuration commandLineConf = parseCommandLine(args);
      final Configuration runtimeConfig = getClientConfiguration(commandLineConf);

      LOG.log(Level.FINEST, "Configuration:\n--\n{0}--",
          ConfigurationFile.toConfigurationString(runtimeConfig));

      final Configuration driverConfig =
          EnvironmentUtils.addClasspath(DriverConfiguration.CONF, DriverConfiguration.GLOBAL_LIBRARIES)
              .set(DriverConfiguration.DRIVER_IDENTIFIER, "pool-" + System.currentTimeMillis())
              .set(DriverConfiguration.ON_DRIVER_STARTED, JobDriver.StartHandler.class)
              .set(DriverConfiguration.ON_EVALUATOR_ALLOCATED, JobDriver.AllocatedEvaluatorHandler.class)
              .set(DriverConfiguration.ON_ACTIVITY_COMPLETED, JobDriver.CompletedActivityHandler.class)
              .build();

      final Injector injector = Tang.Factory.getTang().newInjector(commandLineConf);
      final long maxWait = injector.getNamedInstance(NumActivities.class)
          * injector.getNamedInstance(Delay.class) * 2000;

      DriverLauncher.getLauncher(runtimeConfig)
          .run(TANGUtils.merge(driverConfig, commandLineConf), maxWait);

    } catch (final BindException | InjectionException | IOException ex) {
      LOG.log(Level.SEVERE, "Job configuration error", ex);
    }
  }
}
