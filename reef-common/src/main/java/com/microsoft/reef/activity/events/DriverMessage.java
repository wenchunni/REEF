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
package com.microsoft.reef.activity.events;

import com.microsoft.reef.annotations.Provided;
import com.microsoft.reef.annotations.audience.ActivitySide;
import com.microsoft.reef.annotations.audience.Public;
import com.microsoft.reef.util.Optional;

/**
 * Represents a message sent by the driver.
 */
@ActivitySide
@Public
@Provided
public interface DriverMessage {

  /**
   * @return the message sent by the driver.
   */
  public Optional<byte[]> get();
}
