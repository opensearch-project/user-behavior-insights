/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.ActionRequest;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.action.search.MultiSearchRequest;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.support.ActionFilter;
import org.opensearch.action.support.ActionFilterChain;
import org.opensearch.action.support.ActionRequestMetadata;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.core.action.ActionListener;
import org.opensearch.core.action.ActionResponse;
import org.opensearch.env.Environment;
import org.opensearch.search.SearchHit;
import org.opensearch.tasks.Task;
import org.opensearch.telemetry.tracing.Span;
import org.opensearch.telemetry.tracing.SpanBuilder;
import org.opensearch.telemetry.tracing.Tracer;
import org.opensearch.transport.client.Client;
import org.opensearch.ubi.ext.UbiParameters;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.opensearch.ubi.UbiPlugin.UBI_QUERIES_INDEX;

/**
 * An implementation of {@link ActionFilter} that listens for OpenSearch
 * queries and persists the queries to the UBI store.
 */
public class UbiActionFilter implements ActionFilter {

    private static final Logger LOGGER = LogManager.getLogger(UbiActionFilter.class);

    private final Client client;
    private final Environment environment;
    private final Tracer tracer;

    /**
     * Creates a new filter.
     * @param client An OpenSearch {@link Client}.
     * @param environment The OpenSearch {@link Environment}.
     * @param tracer An Open Telemetry {@link Tracer tracer}.
     */
    public UbiActionFilter(Client client, Environment environment, Tracer tracer) {

        this.client = client;
        this.environment = environment;
        this.tracer = tracer;
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse> void apply(
        Task task,
        String action,
        Request request,
        ActionRequestMetadata<Request, Response> actionRequestMetadata,
        ActionListener<Response> listener,
        ActionFilterChain<Request, Response> chain
    ) {

        if (!(request instanceof SearchRequest || request instanceof MultiSearchRequest)) {
            chain.proceed(task, action, request, listener);
            return;
        }

        chain.proceed(task, action, request, new ActionListener<>() {

            @Override
            public void onResponse(Response response) {

                if (request instanceof MultiSearchRequest) {

                    final MultiSearchRequest multiSearchRequest = (MultiSearchRequest) request;

                    for(final SearchRequest searchRequest : multiSearchRequest.requests()) {
                        handleSearchRequest(searchRequest, response);
                    }

                }

                if(request instanceof SearchRequest) {
                    response = (Response) handleSearchRequest((SearchRequest) request, response);
                }

                listener.onResponse(response);

            }

            @Override
            public void onFailure(Exception ex) {
                listener.onFailure(ex);
            }

        });

    }

    private ActionResponse handleSearchRequest(final SearchRequest searchRequest, ActionResponse response) {

        if (response instanceof SearchResponse) {

            final UbiParameters ubiParameters = UbiParameters.getUbiParameters(searchRequest);

            if (ubiParameters != null) {

                final String queryId = ubiParameters.getQueryId();
                final String userQuery = ubiParameters.getUserQuery();
                final String userId = ubiParameters.getClientId();
                final String objectIdField = ubiParameters.getObjectIdField();
                final String application = ubiParameters.getApplication();
                final Map<String, String> queryAttributes = ubiParameters.getQueryAttributes();
                
                final String query = searchRequest.source().toString();

                final List<String> queryResponseHitIds = new LinkedList<>();

                for (final SearchHit hit : ((SearchResponse) response).getHits()) {

                    if (objectIdField == null || objectIdField.isEmpty()) {
                        // Use the result's docId since no object_id was given for the search.
                        queryResponseHitIds.add(String.valueOf(hit.docId()));
                    } else {
                        final Map<String, Object> source = hit.getSourceAsMap();
                        queryResponseHitIds.add((String) source.get(objectIdField));
                    }

                }

                final String queryResponseId = UUID.randomUUID().toString();
                final QueryResponse queryResponse = new QueryResponse(queryId, queryResponseId, queryResponseHitIds);
                final QueryRequest queryRequest = new QueryRequest(queryId, userQuery, userId, query, application, queryAttributes, queryResponse);

                final String dataPrepperUrl = environment.settings().get(UbiSettings.DATA_PREPPER_URL);
                if (dataPrepperUrl != null) {
                    sendToDataPrepper(dataPrepperUrl, queryRequest);
                } else {
                    indexQuery(queryRequest);
                }

                final SearchResponse searchResponse = (SearchResponse) response;

                response = new UbiSearchResponse(
                        searchResponse.getInternalResponse(),
                        searchResponse.getScrollId(),
                        searchResponse.getTotalShards(),
                        searchResponse.getSuccessfulShards(),
                        searchResponse.getSkippedShards(),
                        searchResponse.getTook().millis(),
                        searchResponse.getShardFailures(),
                        searchResponse.getClusters(),
                        queryId
                );

            }

        }

        return response;

    }

    private void sendToDataPrepper(final String dataPrepperUrl, final QueryRequest queryRequest) {

        LOGGER.debug("Sending query to DataPrepper at {}", dataPrepperUrl);

        // TODO: Do this in a background thread?
        try {

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

                final HttpPost httpPost = new HttpPost(dataPrepperUrl);

                httpPost.setEntity(new StringEntity(queryRequest.toString()));
                httpPost.setHeader("Content-type", "application/json");

                try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                    final int status = response.getStatusLine().getStatusCode();
                    if (status != 200) {
                        LOGGER.error("Unexpected response status from Data Prepper: " + status);
                    }
                } catch (Exception ex) {
                    LOGGER.error("Unable to send query to Data Prepper", ex);
                }

            }

        } catch (IOException ex) {
            LOGGER.error("Failed to send query to Data Prepper", ex);
        }

    }

    private void indexQuery(final QueryRequest queryRequest) {

        LOGGER.debug(
            "Indexing query ID {} with response ID {}",
            queryRequest.getQueryId(),
            queryRequest.getQueryResponse().getQueryResponseId()
        );

        // What will be indexed - adheres to the queries-mapping.json
        final Map<String, Object> source = new HashMap<>();
        source.put("timestamp", queryRequest.getTimestamp());
        source.put("query_id", queryRequest.getQueryId());
        source.put("query_response_id", queryRequest.getQueryResponse().getQueryResponseId());
        source.put("query_response_hit_ids", queryRequest.getQueryResponse().getQueryResponseHitIds());
        source.put("client_id", queryRequest.getClientId());
        source.put("application", queryRequest.getApplication());
        source.put("user_query", queryRequest.getUserQuery());
        source.put("query_attributes", queryRequest.getQueryAttributes());

        // The query can be null for some types of queries.
        if(queryRequest.getQuery() != null) {
            source.put("query", queryRequest.getQuery());
        }

        final IndexRequest indexRequest = new IndexRequest();
        indexRequest.index(UBI_QUERIES_INDEX);
        indexRequest.source(source, XContentType.JSON);

        client.index(indexRequest, new ActionListener<>() {

            @Override
            public void onResponse(IndexResponse indexResponse) {}

            @Override
            public void onFailure(Exception e) {
                LOGGER.error("Unable to index query into " + UBI_QUERIES_INDEX + ".", e);
            }

        });

    }

    private void sendOtelTrace(final Task task, final Tracer tracer, final QueryRequest queryRequest) {

        final Span span = tracer.startSpan(SpanBuilder.from(task, "ubi_search"));

        span.addAttribute("ubi.user_id", queryRequest.getQueryId());
        span.addAttribute("ubi.query", queryRequest.getQuery());
        span.addAttribute("ubi.user_query", queryRequest.getUserQuery());
        span.addAttribute("ubi.client_id", queryRequest.getClientId());
        span.addAttribute("ubi.timestamp", queryRequest.getTimestamp());

        for (final String key : queryRequest.getQueryAttributes().keySet()) {
            span.addAttribute("ubi.attribute." + key, queryRequest.getQueryAttributes().get(key));
        }

        span.addAttribute("ubi.query_response.response_id", queryRequest.getQueryResponse().getQueryResponseId());
        span.addAttribute("ubi.query_response.query_id", queryRequest.getQueryResponse().getQueryId());
        span.addAttribute("ubi.query_response.response_id", String.join(",", queryRequest.getQueryResponse().getQueryResponseHitIds()));

        span.endSpan();

    }

}
