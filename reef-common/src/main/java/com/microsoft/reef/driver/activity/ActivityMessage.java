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
package com.microsoft.reef.driver.activity;

import com.microsoft.reef.annotations.Provided;
import com.microsoft.reef.annotations.audience.DriverSide;
import com.microsoft.reef.annotations.audience.Public;
import com.microsoft.reef.io.Message;
import com.microsoft.reef.io.naming.Identifiable;

/**
 * A message from a running activity to the driver.
 */
@DriverSide
@Public
@Provided
public interface ActivityMessage extends Message, Identifiable {
  /**
   * @return the message.
   */
  @Override
  public byte[] get();

  /**
   * @return the ID of the sending activity.
   */
  @Override
  public String getId();

  /**
   * @return the id of the context the sending activity is running in.
   */
  public String getContextId();

  /**
   * @return the ID of the ActivityMessageSource
   */
  public String getMessageSourceID();
}
