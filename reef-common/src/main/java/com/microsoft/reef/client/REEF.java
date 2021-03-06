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

import com.microsoft.reef.annotations.audience.ClientSide;
import com.microsoft.reef.annotations.audience.Public;
import com.microsoft.reef.runtime.common.client.ClientManager;
import com.microsoft.tang.Configuration;
import com.microsoft.tang.annotations.DefaultImplementation;

/**
 * The main entry point into the REEF runtime.
 * <p/>
 * Every REEF runtime provides an implementation of this interface. That
 * instance is used to submitActivity the Driver class for execution to REEF. As with
 * all submissions in REEF, this is done in the form of a TANG Configuration
 * object.
 */
@Public
@ClientSide
@DefaultImplementation(ClientManager.class)
public interface REEF extends AutoCloseable {

  /**
   * Close the runtime instance. This forcefully kills all running jobs.
   */
  @Override
  public void close();

  /**
   * Submits the Driver set up in the given Configuration for execution.
   * <p/>
   * The Configuration needs to bind the Driver interface to an actual
   * implementation of that interface for the job at hand.
   *
   * @param driverConf The driver configuration: including everything it needs to execute.  @see DriverConfiguration
   */
  public void submit(final Configuration driverConf);
}
