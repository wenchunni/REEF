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
package com.microsoft.reef.tests.exceptions;

/**
 * Thrown when a test fails on the activity side.
 */
public class SimulatedActivityFailure extends RuntimeException {

  public SimulatedActivityFailure() {
    super();
  }

  public SimulatedActivityFailure(final String message) {
    super(message);
  }

  public SimulatedActivityFailure(final String message, final Throwable cause) {
    super(message, cause);
  }

  public SimulatedActivityFailure(final Throwable cause) {
    super(cause);
  }
}
