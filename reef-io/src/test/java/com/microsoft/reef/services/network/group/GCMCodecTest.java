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
package com.microsoft.reef.services.network.group;

import com.microsoft.reef.io.network.group.impl.GCMCodec;
import com.microsoft.reef.io.network.proto.ReefNetworkGroupCommProtos.GroupCommMessage;
import com.microsoft.reef.io.network.proto.ReefNetworkGroupCommProtos.GroupCommMessage.Type;
import com.microsoft.reef.io.network.util.StringIdentifierFactory;
import com.microsoft.reef.services.network.util.TestUtils;
import com.microsoft.tang.Tang;
import com.microsoft.tang.exceptions.BindException;
import com.microsoft.tang.exceptions.InjectionException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author shravan
 */
public class GCMCodecTest {

  /**
   * Test method for {@link com.microsoft.reef.io.network.group.impl.GCMCodec#GCMCodec()}.
   *
   * @throws BindException
   * @throws InjectionException
   */
  @Test(timeout = 1000)
  public final void testGCMCodec() throws InjectionException, BindException {
    GCMCodec codec = Tang.Factory.getTang().newInjector().getInstance(GCMCodec.class);
    Assert.assertNotNull("tang.getInstance(GCMCodec.class)", codec);
  }

  /**
   * Test method for {@link com.microsoft.reef.io.network.group.impl.GCMCodec#decode(byte[])}.
   */
  @Test(timeout = 1000)
  public final void testDecode() {
    GroupCommMessage expected = TestUtils.bldGCM(Type.Scatter,
        new StringIdentifierFactory().getNewInstance("Activity1"),
        new StringIdentifierFactory().getNewInstance("Activity2"), "Hello".getBytes());
    byte[] msgBytes = expected.toByteArray();
    GCMCodec codec = new GCMCodec();
    GroupCommMessage decoded = codec.decode(msgBytes);
    Assert.assertEquals("GCMCodec.decode():", expected, decoded);
  }

  /**
   * Test method for {@link com.microsoft.reef.io.network.group.impl.GCMCodec#encode(com.microsoft.reef.io.network.proto.ReefNetworkGroupCommProtos.GroupCommMessage)}.
   */
  @Test(timeout = 1000)
  public final void testEncode() {
    GroupCommMessage msg = TestUtils.bldGCM(Type.Scatter,
        new StringIdentifierFactory().getNewInstance("Activity1"),
        new StringIdentifierFactory().getNewInstance("Activity2"), "Hello".getBytes());
    byte[] expected = msg.toByteArray();
    GCMCodec codec = new GCMCodec();
    byte[] encoded = codec.encode(msg);
    Assert.assertArrayEquals("GCMCodec.encode():", expected, encoded);
  }

}
