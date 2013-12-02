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
package com.microsoft.reef.examples.suspend;

import com.microsoft.reef.client.RunningJob;
import com.microsoft.wake.EventHandler;
import com.microsoft.wake.remote.RemoteManager;
import com.microsoft.wake.remote.RemoteMessage;
import com.microsoft.wake.remote.impl.ObjectSerializableCodec;

import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * (Wake) listener to get suspend/resume commands from Control process.
 */
public class SuspendClientControl implements AutoCloseable {

  private static final Logger LOG = Logger.getLogger(Control.class.getName());
  private static final ObjectSerializableCodec<String> CODEC = new ObjectSerializableCodec<>();

  private transient RunningJob runningJob;
  private final transient AutoCloseable transport;

  @Inject
  public SuspendClientControl(final RemoteManager rm) {
    this.transport = rm.registerHandler(byte[].class, new ControlMessageHandler());
  }

  /**
   * Forward remote message to the job driver.
   */
  private class ControlMessageHandler implements EventHandler<RemoteMessage<byte[]>> {
    @Override
    public void onNext(final RemoteMessage<byte[]> msg) {
      synchronized (SuspendClientControl.this) {
        LOG.log(Level.INFO, "Control message: {0} destination: {1}",
                new Object[] { CODEC.decode(msg.getMessage()), runningJob });
        if (runningJob != null) {
          runningJob.send(msg.getMessage());
        }
      }
    }
  }

  public synchronized void setRunningJob(final RunningJob job) {
    this.runningJob = job;
  }

  @Override
  public void close() throws Exception {
    this.transport.close();
  }
}
