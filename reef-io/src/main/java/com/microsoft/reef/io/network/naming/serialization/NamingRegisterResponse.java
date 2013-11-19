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
package com.microsoft.reef.io.network.naming.serialization;

/**
 * naming registration response
 */
public class NamingRegisterResponse extends NamingMessage {
  private final NamingRegisterRequest request;
  
  /**
   * Constructs a naming register response
   * 
   * @param request the naming register request
   */
  public NamingRegisterResponse(NamingRegisterRequest request) {
    this.request = request;
  }
  
  /**
   * Gets a naming register request
   * 
   * @return a naming register request
   */
  public NamingRegisterRequest getRequest() {
    return request;
  }
}