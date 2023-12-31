/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

package org.opensearch.discovery;

import org.opensearch.core.common.transport.TransportAddress;

import java.util.List;

/**
 * A pluggable provider of the list of seed hosts to use for discovery.
 *
 * @opensearch.internal
 */
public interface SeedHostsProvider {

    /**
     * Returns a list of seed hosts to use for discovery. Called repeatedly while discovery is active (i.e. while there is no cluster-manager)
     * so that this list may be dynamic.
     */
    List<TransportAddress> getSeedAddresses(HostsResolver hostsResolver);

    /**
     * Helper object that allows to resolve a list of hosts to a list of transport addresses.
     * Each host is resolved into a transport address
     */
    interface HostsResolver {
        List<TransportAddress> resolveHosts(List<String> hosts);
    }
}
