/*
 * Licensed to the Netuno.org under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Netuno.org licenses this file to You under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.netuno.psamata.net;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

/**
 * Local network Hosts, with IP addresses and names.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class LocalHosts {

    public static Set<String> getAll() throws SocketException {
        return getAll(false);
    }

    public static Set<String> getAll(boolean includeIPv6) throws SocketException {
        Set<String> hosts = new HashSet<>();
        Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
        while (netInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = netInterfaces.nextElement();
            for (InterfaceAddress iAddress : netInterface.getInterfaceAddresses()) {
                String hostAddress = iAddress.getAddress().getHostAddress();
                if (!includeIPv6 && hostAddress.contains(":")) {
                    continue;
                }
                if (!hosts.contains(hostAddress)) {
                    hosts.add(hostAddress);
                }
                String hostName = iAddress.getAddress().getHostName();
                if (!hosts.contains(hostName)) {
                    hosts.add(hostName);
                }
            }
        }
        return hosts;
    }

}
