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
package com.microsoft.reef.services.network;

import com.microsoft.reef.io.naming.NameAssignment;
import com.microsoft.reef.io.network.naming.*;
import com.microsoft.reef.io.network.util.StringIdentifierFactory;
import com.microsoft.wake.Identifier;
import com.microsoft.wake.IdentifierFactory;
import com.microsoft.wake.remote.NetUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Naming server and client test
 */
public class NamingTest {
  @Rule public TestName name = new TestName();

  final long TTL = 30000;
  int port;
  final IdentifierFactory factory = new StringIdentifierFactory();

  /**
   * NameServer and NameLookupClient test
   * @throws Exception
   */
  @Test
  public void testNamingLookup() throws Exception {
    System.out.println(name.getMethodName());
    
    // names 
    Map<Identifier, InetSocketAddress> idToAddrMap = new HashMap<Identifier, InetSocketAddress>();
    idToAddrMap.put(factory.getNewInstance("activity1"), new InetSocketAddress(NetUtils.getLocalAddress(), 7001));
    idToAddrMap.put(factory.getNewInstance("activity2"), new InetSocketAddress(NetUtils.getLocalAddress(), 7002));

    // run a server
    NameServer server = new NameServer(0, factory);
    port = server.getPort();
    for (Identifier id : idToAddrMap.keySet()) {
      server.register(id, idToAddrMap.get(id));
    }

    // run a client
    NameLookupClient client = new NameLookupClient(NetUtils.getLocalAddress(), port, 10000, factory, new NameCache(TTL));
    
    Identifier id1 = factory.getNewInstance("activity1");
    Identifier id2 = factory.getNewInstance("activity2");

    Map<Identifier, InetSocketAddress> respMap = new HashMap<Identifier, InetSocketAddress>();
    InetSocketAddress addr1 = client.lookup(id1);
    respMap.put(id1, addr1);
    InetSocketAddress addr2 = client.lookup(id2);
    respMap.put(id2, addr2);
    
    for (Identifier id : respMap.keySet()) {
      System.out.println("Mapping: " + id + " -> " + respMap.get(id));
    }
   
    Assert.assertTrue(isEqual(idToAddrMap, respMap));
    
    client.close();
    server.close();
  }
  
  /**
   * Test concurrent lookups (threads share a client)
   * @throws Exception
   */
  @Test
  public void testConcurrentNamingLookup() throws Exception {
    System.out.println(name.getMethodName());

    // test it 3 times to make failure likely
    for (int i=0; i<3; i++) {
      System.out.println("test "+i);

      // names 
      Map<Identifier, InetSocketAddress> idToAddrMap = new HashMap<Identifier, InetSocketAddress>();
      idToAddrMap.put(factory.getNewInstance("activity1"), new InetSocketAddress(NetUtils.getLocalAddress(), 7001));
      idToAddrMap.put(factory.getNewInstance("activity2"), new InetSocketAddress(NetUtils.getLocalAddress(), 7002));
      idToAddrMap.put(factory.getNewInstance("activity3"), new InetSocketAddress(NetUtils.getLocalAddress(), 7003));

      // run a server
      NameServer server = new NameServer(0, factory);
      port = server.getPort();
      for (Identifier id : idToAddrMap.keySet()) {
        server.register(id, idToAddrMap.get(id));
      }

      // run a client
      final NameLookupClient client = new NameLookupClient(NetUtils.getLocalAddress(), port, 10000, factory, new NameCache(TTL));

      final Identifier id1 = factory.getNewInstance("activity1");
      final Identifier id2 = factory.getNewInstance("activity2");
      final Identifier id3 = factory.getNewInstance("activity3");

      ExecutorService e = Executors.newCachedThreadPool();

      final ConcurrentMap<Identifier, InetSocketAddress> respMap = new ConcurrentHashMap<Identifier, InetSocketAddress>();

      Future<?> f1 = e.submit(new Runnable() {
        @Override
        public void run() {
          InetSocketAddress addr = null;
          try {
            addr = client.lookup(id1);
          } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
          }
          respMap.put(id1, addr);       
        }
      });
      Future<?> f2 = e.submit(new Runnable() {
        @Override
        public void run() {
          InetSocketAddress addr = null;
          try {
            addr = client.lookup(id2);
          } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
          }
          respMap.put(id2, addr);       
        }
      });
      Future<?> f3 = e.submit(new Runnable() {
        @Override
        public void run() {
          InetSocketAddress addr = null;
          try {
            addr = client.lookup(id3);
          } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
          }
          respMap.put(id3, addr);       
        }
      });


      f1.get();
      f2.get();
      f3.get();


      for (Identifier id : respMap.keySet()) {
        System.out.println("Mapping: " + id + " -> " + respMap.get(id));
      }

      Assert.assertTrue(isEqual(idToAddrMap, respMap));

      client.close();
      server.close();
    }
  }
  
  /**
   * NameServer and NameRegistryClient test
   * @throws Exception
   */
  @Test
  public void testNamingRegistry() throws Exception {
    System.out.println(name.getMethodName());

    NameServer server = new NameServer(0, factory);
    port = server.getPort();

    // names to start with
    Map<Identifier, InetSocketAddress> idToAddrMap = new HashMap<Identifier, InetSocketAddress>();
    idToAddrMap.put(factory.getNewInstance("activity1"), new InetSocketAddress(NetUtils.getLocalAddress(), 7001));
    idToAddrMap.put(factory.getNewInstance("activity2"), new InetSocketAddress(NetUtils.getLocalAddress(), 7002));

    // registration
    // invoke registration from the client side
    NameRegistryClient client = new NameRegistryClient(NetUtils.getLocalAddress(), port, factory);
    for (Identifier id : idToAddrMap.keySet()) {
      client.register(id, idToAddrMap.get(id));
    }
    
    // wait
    Set<Identifier> ids = idToAddrMap.keySet();
    busyWait(server, ids.size(), ids);
        
    // check the server side 
    Map<Identifier, InetSocketAddress> serverMap = new HashMap<Identifier, InetSocketAddress>();
    Iterable<NameAssignment> nas = server.lookup(ids);
    for (NameAssignment na : nas) {
      System.out.println("Mapping: " + na.getIdentifier() + " -> " + na.getAddress());
      serverMap.put(na.getIdentifier(), na.getAddress());
    }
    
    Assert.assertTrue(isEqual(idToAddrMap, serverMap));

    // un-registration
    for (Identifier id : idToAddrMap.keySet()) {
      client.unregister(id);
    }
    
    // wait
    busyWait(server, 0, ids);
    
    serverMap = new HashMap<Identifier, InetSocketAddress>();
    nas = server.lookup(ids);
    for (NameAssignment na : nas)
      serverMap.put(na.getIdentifier(), na.getAddress());
    
    Assert.assertEquals(0, serverMap.size());
    
    client.close();
    server.close();

  }
  
  /**
   * NameServer and NameClient test
   * @throws Exception
   */
  @Test
  public void testNameClient() throws Exception {
    System.out.println(name.getMethodName());

    NameServer server = new NameServer(0, factory);
    port = server.getPort();
    
    Map<Identifier, InetSocketAddress> idToAddrMap = new HashMap<Identifier, InetSocketAddress>();
    idToAddrMap.put(factory.getNewInstance("activity1"), new InetSocketAddress(NetUtils.getLocalAddress(), 7001));
    idToAddrMap.put(factory.getNewInstance("activity2"), new InetSocketAddress(NetUtils.getLocalAddress(), 7002));

    // registration
    // invoke registration from the client side
    NameClient client = new NameClient(NetUtils.getLocalAddress(), port, factory, new NameCache(TTL));
    for (Identifier id : idToAddrMap.keySet()) {
      client.register(id, idToAddrMap.get(id));
    }
    
    // wait
    Set<Identifier> ids = idToAddrMap.keySet();
    busyWait(server, ids.size(), ids);
    
    // lookup
    Identifier id1 = factory.getNewInstance("activity1");
    Identifier id2 = factory.getNewInstance("activity2");

    Map<Identifier, InetSocketAddress> respMap = new HashMap<Identifier, InetSocketAddress>();
    InetSocketAddress addr1 = client.lookup(id1);
    respMap.put(id1, addr1);
    InetSocketAddress addr2 = client.lookup(id2);
    respMap.put(id2, addr2);
    
    for (Identifier id : respMap.keySet()) {
      System.out.println("Mapping: " + id + " -> " + respMap.get(id));
    }
    
    Assert.assertTrue(isEqual(idToAddrMap, respMap));
    
    // un-registration
    for (Identifier id : idToAddrMap.keySet()) {
      client.unregister(id);
    }

    // wait
    busyWait(server, 0, ids);
   
    Map<Identifier, InetSocketAddress> serverMap = new HashMap<Identifier, InetSocketAddress>();
    addr1 = server.lookup(id1);
    if (addr1 != null) serverMap.put(id1, addr1);
    addr2 = server.lookup(id1);
    if (addr2 != null) serverMap.put(id2, addr2);
    
    Assert.assertEquals(0, serverMap.size());
    
    client.close();
    server.close();
  }
  
  private boolean isEqual(Map<Identifier, InetSocketAddress> map1, Map<Identifier, InetSocketAddress> map2) {
    boolean equal = true;
    if (map1.size() != map2.size())
      equal = false;
    
    for(Identifier id : map1.keySet()) {
      InetSocketAddress addr1 = map1.get(id);
      InetSocketAddress addr2 = map2.get(id);
      if (!addr1.equals(addr2)) {
        equal = false;
        break;
      }
    }
    return equal;
  }

  private void busyWait(NameServer server, int expected, Set<Identifier> ids) {
    int count = 0;
    while(true) {
      Iterable<NameAssignment> nas = server.lookup(ids);
      for (@SuppressWarnings("unused") NameAssignment na : nas) {
        ++count;
      }
      if (count == expected)
        break;
      count = 0;
    }
  }

}
