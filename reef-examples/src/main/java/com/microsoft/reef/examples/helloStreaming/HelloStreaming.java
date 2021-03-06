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
package com.microsoft.reef.examples.helloStreaming;

import com.microsoft.reef.client.DriverConfiguration;
import com.microsoft.reef.client.DriverLauncher;
import com.microsoft.reef.client.LauncherStatus;
import com.microsoft.reef.client.REEF;
import com.microsoft.reef.runtime.local.client.LocalRuntimeConfiguration;
import com.microsoft.reef.runtime.yarn.client.YarnClientConfiguration;
import com.microsoft.reef.util.EnvironmentUtils;
import com.microsoft.tang.Configuration;
import com.microsoft.tang.exceptions.BindException;
import com.microsoft.tang.exceptions.InjectionException;
import com.microsoft.tang.formats.ConfigurationModule;
import com.microsoft.tang.formats.OptionalParameter;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * The Client for Hello REEF example.
 */
public final class HelloStreaming {

  /**
   * The name of the class hierarchy file.
   */
  // TODO: Make this a config option
  public static final String CLASS_HIERARCHY_FILENAME = "activity.bin";

  private static final Logger LOG = Logger.getLogger(HelloStreaming.class.getName());

  /**
   * Number of milliseconds to wait for the job to complete.
   */
  private static final int JOB_TIMEOUT = 1000000; // 10 sec.

  private static ConfigurationModule addAll(final ConfigurationModule conf, final OptionalParameter<String> param, final File folder) {
    ConfigurationModule result = conf;
    for (final File f : folder.listFiles()) {
      if (f.canRead() && f.exists() && f.isFile()) {
        result = result.set(param, f.getAbsolutePath());
      }
    }
    return result;
  }

  public static LauncherStatus runHelloStreaming(final Configuration runtimeConf, final int timeOut, final File clrFolder)
      throws BindException, InjectionException {

    ConfigurationModule driverConf = DriverConfiguration.CONF
        .set(DriverConfiguration.DRIVER_IDENTIFIER, "HelloStreaming")
        .set(DriverConfiguration.ON_DRIVER_STARTED, HelloDriver.StartHandler.class)
        .set(DriverConfiguration.ON_EVALUATOR_ALLOCATED, HelloDriver.EvaluatorAllocatedHandler.class);

    driverConf = EnvironmentUtils.addClasspath(driverConf, DriverConfiguration.GLOBAL_LIBRARIES);

      final File dllsForCLRConfigurationFile = new File(clrFolder, "DllsForCLR.conf");
      try (PrintWriter clientOut = new PrintWriter(dllsForCLRConfigurationFile)) {
          clientOut.write("com.microsoft.reef.activity.dll,com.microsoft.reef.ActivityInterface.dll");
      } catch (final FileNotFoundException e) {
          throw new RuntimeException("Unable to write list of DLLs needed into file.", e);
      }

    driverConf = addAll(driverConf, DriverConfiguration.GLOBAL_FILES, clrFolder);

    return DriverLauncher.getLauncher(runtimeConf).run(driverConf.build(), timeOut);
  }

  /**
   * Start Hello REEF job. Runs method runHelloReef().
   *
   * @param args command line parameters.
   * @throws com.microsoft.tang.exceptions.BindException
   *          configuration error.
   * @throws com.microsoft.tang.exceptions.InjectionException
   *          configuration error.
   */
  public static void main(final String[] args) throws BindException, InjectionException {
      final Configuration runtimeConfiguration = LocalRuntimeConfiguration.CONF
              .set(LocalRuntimeConfiguration.NUMBER_OF_THREADS, 2)
        .build();

      final File dotNetFolder = new File(args[0]).getAbsoluteFile();
    final LauncherStatus status = runHelloStreaming(runtimeConfiguration, JOB_TIMEOUT, dotNetFolder);
    LOG.log(Level.INFO, "REEF Streaming job completed: {0}", status);
  }
}
