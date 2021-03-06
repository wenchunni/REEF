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
package com.microsoft.reef.tests.driver;

import com.microsoft.wake.EventHandler;
import com.microsoft.wake.time.Clock;
import com.microsoft.wake.time.event.Alarm;
import com.microsoft.wake.time.event.StartTime;

import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A StartHandler that does nothing.
 */
final class DriverTestStartHandler implements EventHandler<StartTime> {
  private static final Logger LOG = Logger.getLogger(DriverTestStartHandler.class.getName());

  private final Clock clock;

  @Inject
  DriverTestStartHandler(final Clock clock) {
    this.clock = clock;
  }

  @Override
  public void onNext(final StartTime startTime) {
    LOG.log(Level.INFO, "StartTime: {0}", startTime);
    // TODO: This has to go when #389 is fixed
    clock.scheduleAlarm(100, new EventHandler<Alarm>() {
      @Override
      public void onNext(final Alarm alarm) {
        LOG.log(Level.INFO, "Alarm fired: {0}", alarm);
      }
    });
  }
}
