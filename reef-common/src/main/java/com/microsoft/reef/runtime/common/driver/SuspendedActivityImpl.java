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

import com.microsoft.reef.driver.activity.SuspendedActivity;
import com.microsoft.reef.driver.context.ActiveContext;

final class SuspendedActivityImpl implements SuspendedActivity {
  private final ActiveContext context;
  private final byte[] message;
  private final String id;

  SuspendedActivityImpl(final ActiveContext context, final byte[] message, final String id) {
    this.context = context;
    this.message = message;
    this.id = id;
  }

  @Override
  public ActiveContext getActiveContext() {
    return this.context;
  }

  @Override
  public byte[] get() {
    return this.message;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public String toString() {
    return "SuspendedActivity{ID='" + getId() + "'}";
  }
}
