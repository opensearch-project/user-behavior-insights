/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * A query received by OpenSearch.
 */
public class QueryRequest {

    private final String timestamp;
    private final String queryId;
    private final String clientId;
    private final Map<String, String> userQuery;
    private final String query;
    private final String application;
    private final Map<String, String> queryAttributes;
    private final QueryResponse queryResponse;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());

    /**
     * Creates a query request.
     * @param queryId The ID of the query.
     * @param userQuery The user-entered query.
     * @param clientId The ID of the client that initiated the query.
     * @param query The raw query.
     * @param application The application that initiated the query.
     * @param queryAttributes An optional map of additional attributes for the query.
     * @param queryResponse The {@link QueryResponse} for this query request.
     */
    public QueryRequest(final String queryId, final Map<String, String> userQuery, final String clientId, final String query,
                        final String application, final Map<String, String> queryAttributes,
                        final QueryResponse queryResponse) {

        this.timestamp = sdf.format(new Date());
        this.queryId = queryId;
        this.clientId = clientId;
        this.userQuery = userQuery;
        this.query = query;
        this.application = application;
        this.queryAttributes = queryAttributes;
        this.queryResponse = queryResponse;

    }

    @Override
    public String toString() {

        final ObjectMapper objectMapper = new ObjectMapper();

        final String json = AccessController.doPrivileged((PrivilegedAction<String>) () -> {
            try {
                return objectMapper.writeValueAsString(this);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        });

        return "[" + json + "]";

    }

    /**
     * Gets the query attributes.
     * @return The query attributes.
     */
    public Map<String, String> getQueryAttributes() {
        return queryAttributes;
    }

    /**
     * Gets the timestamp.
     * @return The timestamp.
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the application.
     * @return The application.
     */
    public String getApplication() {
        return application;
    }

    /**
     * Gets the query ID.
     * @return The query ID.
     */
    public String getQueryId() {
        return queryId;
    }

    /**
     * Gets the user query map.
     * @return The user query map.
     */
    public Map<String, String> getUserQuery() {
        return userQuery;
    }

    /**
     * Gets the user query text (convenience accessor).
     * @return The "text" value of user_query or empty string if missing.
     */
    public String getUserQueryText() {
        if (userQuery == null) {
            return "";
        }
        return userQuery.getOrDefault("text", "");
    }

    /**
     * Gets the client ID.
     * @return The client ID.
     */
    public String getClientId() {
        if(clientId == null) {
            return "";
        }
        return clientId;
    }

    /**
     * Gets the raw query.
     * @return The raw query.
     */
    public String getQuery() {
        if(query == null) {
            return "";
        }
        return query;
    }

    /**
     * Gets the query response for this query request.
     * @return The {@link QueryResponse} for this query request.
     */
    public QueryResponse getQueryResponse() {
        return queryResponse;
    }

}
