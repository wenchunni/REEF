/*
 * Copyright 2013 Microsoft.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.microsoft.reef.io.network.group.impl.operators.faulty;

import javax.inject.Inject;

import com.microsoft.reef.evaluator.context.events.ContextStop;
import com.microsoft.reef.io.network.impl.NetworkService;
import com.microsoft.wake.EventHandler;

/**
 * 
 */
public class NSClose implements EventHandler<ContextStop> {
  private final NetworkService<?> ns;
  
  @Inject
  public NSClose(NetworkService<?> ns) {
    this.ns = ns;
  }

  @Override
  public void onNext(ContextStop arg0) {
    try {
      System.out.println("Closing Network Service");
      ns.close();
    } catch (Exception e) {
      System.out.println("Exception while closing");
      e.printStackTrace();
    }
  }

}