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

package org.opensearch.action.support.nodes;

import org.opensearch.action.ActionResponse;
import org.opensearch.action.FailedNodeException;
import org.opensearch.cluster.ClusterName;
import org.opensearch.core.common.io.stream.StreamInput;
import org.opensearch.core.common.io.stream.StreamOutput;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Transport response for nodes requests
 *
 * @opensearch.internal
 */
public abstract class BaseNodesResponse<TNodeResponse extends BaseNodeResponse> extends ActionResponse {

    private ClusterName clusterName;
    private List<FailedNodeException> failures;
    private List<TNodeResponse> nodes;
    private Map<String, TNodeResponse> nodesMap;

    protected BaseNodesResponse(StreamInput in) throws IOException {
        super(in);
        clusterName = new ClusterName(in);
        nodes = readNodesFrom(in);
        failures = in.readList(FailedNodeException::new);
    }

    protected BaseNodesResponse(ClusterName clusterName, List<TNodeResponse> nodes, List<FailedNodeException> failures) {
        this.clusterName = Objects.requireNonNull(clusterName);
        this.failures = Objects.requireNonNull(failures);
        this.nodes = Objects.requireNonNull(nodes);
    }

    /**
     * Get the {@link ClusterName} associated with all of the nodes.
     *
     * @return Never {@code null}.
     */
    public ClusterName getClusterName() {
        return clusterName;
    }

    /**
     * Get the failed node exceptions.
     *
     * @return Never {@code null}. Can be empty.
     */
    public List<FailedNodeException> failures() {
        return failures;
    }

    /**
     * Determine if there are any node failures in {@link #failures}.
     *
     * @return {@code true} if {@link #failures} contains at least 1 {@link FailedNodeException}.
     */
    public boolean hasFailures() {
        return failures.isEmpty() == false;
    }

    /**
     * Get the <em>successful</em> node responses.
     *
     * @return Never {@code null}. Can be empty.
     * @see #hasFailures()
     */
    public List<TNodeResponse> getNodes() {
        return nodes;
    }

    /**
     * Lazily build and get a map of Node ID to node response.
     *
     * @return Never {@code null}. Can be empty.
     * @see #getNodes()
     */
    public Map<String, TNodeResponse> getNodesMap() {
        if (nodesMap == null) {
            nodesMap = new HashMap<>();
            for (TNodeResponse nodeResponse : nodes) {
                nodesMap.put(nodeResponse.getNode().getId(), nodeResponse);
            }
        }
        return nodesMap;
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        clusterName.writeTo(out);
        writeNodesTo(out, nodes);
        out.writeList(failures);
    }

    /**
     * Read the {@link #nodes} from the stream.
     *
     * @return Never {@code null}.
     */
    protected abstract List<TNodeResponse> readNodesFrom(StreamInput in) throws IOException;

    /**
     * Write the {@link #nodes} to the stream.
     */
    protected abstract void writeNodesTo(StreamOutput out, List<TNodeResponse> nodes) throws IOException;

}
