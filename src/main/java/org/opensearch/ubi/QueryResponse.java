/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi;

import java.util.List;

/**
 * A query response.
 */
public class QueryResponse {

    private final String queryId;
    private final String queryResponseId;
    private final List<String> queryResponseHitIds;

    /**
     * Creates a query response.
     * @param queryId The ID of the query.
     * @param queryResponseId The ID of the query response.
     * @param queryResponseHitIds A list of IDs for the hits in the query.
     */
    public QueryResponse(final String queryId, final String queryResponseId, final List<String> queryResponseHitIds) {
        this.queryId = queryId;
        this.queryResponseId = queryResponseId;
        this.queryResponseHitIds = queryResponseHitIds;
    }

    /**
     * Gets the query ID.
     * @return The query ID.
     */
    public String getQueryId() {
        return queryId;
    }

    /**
     * Gets the query response ID.
     * @return The query response ID.
     */
    public String getQueryResponseId() {
        return queryResponseId;
    }

    /**
     * Gets the list of query response hit IDs.
     * @return A list of query response hit IDs.
     */
    public List<String> getQueryResponseHitIds() {
        return queryResponseHitIds;
    }

}
