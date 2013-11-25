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
package com.microsoft.reef.runtime.common.driver;

import com.microsoft.reef.annotations.audience.Private;
import com.microsoft.reef.client.DriverConfigurationOptions;
import com.microsoft.reef.proto.ClientRuntimeProtocol;
import com.microsoft.reef.runtime.common.utils.BroadCastEventHandler;
import com.microsoft.tang.InjectionFuture;
import com.microsoft.tang.annotations.Parameter;
import com.microsoft.wake.EventHandler;
import com.microsoft.wake.time.Clock;

import javax.inject.Inject;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Private
class ClientManager implements EventHandler<ClientRuntimeProtocol.JobControlProto> {

  private final static Logger LOG = Logger.getLogger(DriverManager.class.getName());

  private final InjectionFuture<Clock> futureClock;

  private final InjectionFuture<Set<EventHandler<Void>>> clientCloseHandlers;

  private final InjectionFuture<Set<EventHandler<byte[]>>> clientCloseWithMessageHandlers;

  private final InjectionFuture<Set<EventHandler<byte[]>>> clientMessageHandlers;

  private volatile EventHandler<Void> clientCloseDispatcher;

  private volatile EventHandler<byte[]> clientCloseWithMessageDispatcher;

  private volatile EventHandler<byte[]> clientMessageDispatcher;

  @Inject
  ClientManager(final InjectionFuture<Clock> futureClock,
                @Parameter(DriverConfigurationOptions.ClientCloseHandlers.class) final InjectionFuture<Set<EventHandler<Void>>> clientCloseHandlers,
                @Parameter(DriverConfigurationOptions.ClientCloseWithMessageHandlers.class) final InjectionFuture<Set<EventHandler<byte[]>>> clientCloseWithMessageHandlers,
                @Parameter(DriverConfigurationOptions.ClientMessageHandlers.class) final InjectionFuture<Set<EventHandler<byte[]>>> clientMessageHandlers) {
    this.futureClock = futureClock;
    this.clientCloseHandlers = clientCloseHandlers;
    this.clientCloseWithMessageHandlers = clientCloseWithMessageHandlers;
    this.clientMessageHandlers = clientMessageHandlers;
  }

  /**
   * This method reacts to control messages passed by the client to the driver. It will forward
   * messages related to the ClientObserver interface to the Driver. It will also initiate a shutdown
   * if the client indicates a close message.
   *
   * @param jobControlProto contains the client initiated control message
   */
  @Override
  public void onNext(final ClientRuntimeProtocol.JobControlProto jobControlProto) {
    if (jobControlProto.hasSignal()) {
      if (jobControlProto.getSignal() == ClientRuntimeProtocol.Signal.SIG_TERMINATE) {
        try {
          if (jobControlProto.hasMessage()) {
            getClientCloseWithMessageDispatcher().onNext(jobControlProto.getMessage().toByteArray());
          } else {
            getClientCloseDispatcher().onNext(null);
          }
        } finally {
          this.futureClock.get().close();
        }
      } else {
        LOG.log(Level.FINEST, "Unsupported signal: " + jobControlProto.getSignal());
      }
    } else if (jobControlProto.hasMessage()) {
      getClientMessageDispatcher().onNext(jobControlProto.getMessage().toByteArray());
    }
  }

  public EventHandler<Void> getClientCloseDispatcher() {
    if (clientCloseDispatcher != null) {
      return clientCloseDispatcher;
    } else {
      synchronized (this) {
        if (clientCloseDispatcher == null)
          clientCloseDispatcher = new BroadCastEventHandler<>(clientCloseHandlers.get());
      }
      return clientCloseDispatcher;
    }
  }

  public EventHandler<byte[]> getClientCloseWithMessageDispatcher() {
    if (clientCloseWithMessageDispatcher != null) {
      return clientCloseWithMessageDispatcher;
    } else {
      synchronized (this) {
        if (clientCloseWithMessageDispatcher == null)
          clientCloseWithMessageDispatcher = new BroadCastEventHandler<>(clientCloseWithMessageHandlers.get());
      }
      return clientCloseWithMessageDispatcher;
    }
  }

  public EventHandler<byte[]> getClientMessageDispatcher() {
    if (clientMessageDispatcher != null) {
      return clientMessageDispatcher;
    } else {
      synchronized (this) {
        if (clientMessageDispatcher == null)
          clientMessageDispatcher = new BroadCastEventHandler<>(clientMessageHandlers.get());
      }
      return clientMessageDispatcher;
    }
  }

}
