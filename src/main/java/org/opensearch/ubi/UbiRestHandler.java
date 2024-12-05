/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.admin.indices.create.CreateIndexRequest;
import org.opensearch.action.admin.indices.create.CreateIndexResponse;
import org.opensearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.opensearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.opensearch.client.node.NodeClient;
import org.opensearch.cluster.metadata.IndexMetadata;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.util.io.Streams;
import org.opensearch.core.action.ActionListener;
import org.opensearch.core.rest.RestStatus;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.BytesRestResponse;
import org.opensearch.rest.RestRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.opensearch.ubi.UbiPlugin.EVENTS_MAPPING_FILE;
import static org.opensearch.ubi.UbiPlugin.QUERIES_MAPPING_FILE;
import static org.opensearch.ubi.UbiPlugin.UBI_EVENTS_INDEX;
import static org.opensearch.ubi.UbiPlugin.UBI_QUERIES_INDEX;

public class UbiRestHandler extends BaseRestHandler {

    private static final Logger LOGGER = LogManager.getLogger(UbiRestHandler.class);

    /**
     * URL for initializing the plugin and the index mappings.
     */
    public static final String INITIALIZE_URL = "/_plugins/ubi/initialize";

    @Override
    public String getName() {
        return "User Behavior Insights";
    }

    @Override
    public List<Route> routes() {
        return List.of(new Route(RestRequest.Method.POST, INITIALIZE_URL));
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {

        if(request.path().equalsIgnoreCase(INITIALIZE_URL)) {

            if (request.method().equals(RestRequest.Method.POST)) {

                final IndicesExistsRequest indicesExistsRequest = new IndicesExistsRequest(UBI_EVENTS_INDEX, UBI_QUERIES_INDEX);

                client.admin().indices().exists(indicesExistsRequest, new ActionListener<>() {

                    @Override
                    public void onResponse(IndicesExistsResponse indicesExistsResponse) {

                        if (!indicesExistsResponse.isExists()) {

                            final Settings indexSettings = Settings.builder()
                                    .put(IndexMetadata.INDEX_NUMBER_OF_SHARDS_SETTING.getKey(), 1)
                                    .put(IndexMetadata.INDEX_AUTO_EXPAND_REPLICAS_SETTING.getKey(), "0-2")
                                    .put(IndexMetadata.SETTING_PRIORITY, Integer.MAX_VALUE)
                                    .build();

                            // Create the UBI events index.
                            final CreateIndexRequest createEventsIndexRequest = new CreateIndexRequest(UBI_EVENTS_INDEX).mapping(
                                    getResourceFile(EVENTS_MAPPING_FILE)
                            ).settings(indexSettings);

                            client.admin().indices().create(createEventsIndexRequest, new ActionListener<>() {
                                @Override
                                public void onResponse(CreateIndexResponse createIndexResponse) {
                                    LOGGER.debug("ubi_queries index created.");
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    LOGGER.error("Unable to create ubi_queries index", e);
                                }
                            });

                            // Create the UBI queries index.
                            final CreateIndexRequest createQueriesIndexRequest = new CreateIndexRequest(UBI_QUERIES_INDEX).mapping(
                                    getResourceFile(QUERIES_MAPPING_FILE)
                            ).settings(indexSettings);

                            client.admin().indices().create(createQueriesIndexRequest, new ActionListener<>() {
                                @Override
                                public void onResponse(CreateIndexResponse createIndexResponse) {
                                    LOGGER.debug("ubi_events index created.");
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    LOGGER.error("Unable to create ubi_events index", e);
                                }
                            });

                        } else {
                            LOGGER.debug("UBI indexes already exist.");
                        }

                    }

                    @Override
                    public void onFailure(Exception ex) {
                        LOGGER.error("Error determining if UBI indexes exist.", ex);
                    }

                });

                return restChannel -> restChannel.sendResponse(new BytesRestResponse(RestStatus.OK, "{\"message\": \"UBI indexes created.\"}"));

            } else {
                return restChannel -> restChannel.sendResponse(new BytesRestResponse(RestStatus.METHOD_NOT_ALLOWED, "{\"error\": \"" + request.method() + " is not allowed.\"}"));
            }

        } else {
            return restChannel -> restChannel.sendResponse(new BytesRestResponse(RestStatus.NOT_FOUND, "{\"error\": \"" + request.path() + " was not found.\"}"));
        }

    }

    private static String getResourceFile(final String fileName) {
        try (InputStream is = UbiActionFilter.class.getResourceAsStream(fileName)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Streams.copy(is, out);
            return out.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to get mapping from resource [" + fileName + "]", e);
        }
    }

}
