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
package com.microsoft.reef.tests.evaluatorreuse;


import com.microsoft.reef.activity.Activity;

import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * A basic activity that just returns the memento given.
 */
final class EchoActivity implements Activity {
  private static final Logger LOG = Logger.getLogger(EchoActivity.class.getName());

  @Inject
  EchoActivity() {
  }

  @Override
  public byte[] call(final byte[] memento) throws Exception {
    LOG.info("Memento received: " + new String(memento));
    return memento;
  }
}
