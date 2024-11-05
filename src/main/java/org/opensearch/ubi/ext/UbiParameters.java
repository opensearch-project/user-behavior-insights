/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi.ext;

import org.opensearch.action.search.SearchRequest;
import org.opensearch.core.ParseField;
import org.opensearch.core.common.io.stream.StreamInput;
import org.opensearch.core.common.io.stream.StreamOutput;
import org.opensearch.core.common.io.stream.Writeable;
import org.opensearch.core.xcontent.ObjectParser;
import org.opensearch.core.xcontent.ToXContentObject;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.core.xcontent.XContentParser;
import org.opensearch.search.SearchExtBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * The UBI parameters available in the ext.
 */
public class UbiParameters implements Writeable, ToXContentObject {

    private static final ObjectParser<UbiParameters, Void> PARSER;
    private static final ParseField QUERY_ID = new ParseField("query_id");
    private static final ParseField USER_QUERY = new ParseField("user_query");
    private static final ParseField CLIENT_ID = new ParseField("client_id");
    private static final ParseField APPLICATION = new ParseField("application");
    private static final ParseField OBJECT_ID_FIELD = new ParseField("object_id_field");
    private static final ParseField QUERY_ATTRIBUTES = new ParseField("query_attributes");

    static {
        PARSER = new ObjectParser<>(UbiParametersExtBuilder.UBI_PARAMETER_NAME, UbiParameters::new);
        PARSER.declareString(UbiParameters::setQueryId, QUERY_ID);
        PARSER.declareString(UbiParameters::setUserQuery, USER_QUERY);
        PARSER.declareString(UbiParameters::setClientId, CLIENT_ID);
        PARSER.declareString(UbiParameters::setApplication, APPLICATION);
        PARSER.declareString(UbiParameters::setObjectIdField, OBJECT_ID_FIELD);
        PARSER.declareObject(UbiParameters::setQueryAttributes, (p, c) -> p.mapStrings(), QUERY_ATTRIBUTES);
    }

    /**
     * Get the {@link UbiParameters} from a {@link SearchRequest}.
     * @param request A {@link SearchRequest},
     * @return The UBI {@link UbiParameters parameters}.
     */
    public static UbiParameters getUbiParameters(final SearchRequest request) {

        UbiParametersExtBuilder builder = null;

        if (request.source() != null && request.source().ext() != null && !request.source().ext().isEmpty()) {
            final Optional<SearchExtBuilder> b = request.source()
                    .ext()
                    .stream()
                    .filter(bldr -> UbiParametersExtBuilder.UBI_PARAMETER_NAME.equals(bldr.getWriteableName()))
                    .findFirst();
            if (b.isPresent()) {
                builder = (UbiParametersExtBuilder) b.get();
            }
        }

        if (builder != null) {
            return builder.getParams();
        } else {
            return null;
        }

    }

    private String queryId;
    private String userQuery;
    private String clientId;
    private String application;
    private String objectIdField;
    private Map<String, String> queryAttributes;

    /**
     * Creates a new instance.
     */
    public UbiParameters() {}

    /**
     * Creates a new instance.
     * @param input The {@link StreamInput} to read parameters from.
     * @throws IOException Thrown if the parameters cannot be read.
     */
    @SuppressWarnings("unchecked")
    public UbiParameters(StreamInput input) throws IOException {
        this.queryId = input.readString();
        this.userQuery = input.readOptionalString();
        this.clientId = input.readOptionalString();
        this.application = input.readOptionalString();
        this.objectIdField = input.readOptionalString();
        this.queryAttributes = (Map<String, String>) input.readGenericValue();
    }

    /**
     * Creates a new instance.
     * @param queryId The query ID.
     * @param userQuery The user-entered search query.
     * @param clientId The client ID.
     * @param application The name of the application making the query.
     * @param objectIdField The object ID field.
     * @param queryAttributes Optional attributes for UBI.
     */
    public UbiParameters(String queryId, String userQuery, String clientId, String application, String objectIdField, Map<String, String> queryAttributes) {
        this.queryId = queryId;
        this.userQuery = userQuery;
        this.clientId = clientId;
        this.application = application;
        this.objectIdField = objectIdField;
        this.queryAttributes = queryAttributes;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder xContentBuilder, Params params) throws IOException {
        return xContentBuilder
                .field(QUERY_ID.getPreferredName(), this.queryId)
                .field(USER_QUERY.getPreferredName(), this.userQuery)
                .field(CLIENT_ID.getPreferredName(), this.clientId)
                .field(APPLICATION.getPreferredName(), this.application)
                .field(OBJECT_ID_FIELD.getPreferredName(), this.objectIdField)
                .field(QUERY_ATTRIBUTES.getPreferredName(), this.queryAttributes);
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeString(getQueryId());
        out.writeOptionalString(userQuery);
        out.writeOptionalString(clientId);
        out.writeOptionalString(application);
        out.writeOptionalString(objectIdField);
        out.writeGenericValue(queryAttributes);
    }

    /**
     * Create the {@link UbiParameters} from a {@link XContentParser}.
     * @param parser An {@link XContentParser}.
     * @return The {@link UbiParameters}.
     * @throws IOException Thrown if the parameters cannot be read.
     */
    public static UbiParameters parse(XContentParser parser) throws IOException {
        return PARSER.parse(parser, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final UbiParameters other = (UbiParameters) o;
        return Objects.equals(this.queryId, other.getQueryId())
                && Objects.equals(this.userQuery, other.getUserQuery())
                && Objects.equals(this.clientId, other.getClientId())
                && Objects.equals(this.application, other.getApplication())
                && Objects.equals(this.objectIdField, other.getObjectIdField())
                && Objects.equals(this.queryAttributes, other.getQueryAttributes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), this.queryId);
    }

    /**
     * Get the query ID.
     * @return The query ID, or a random UUID if the query ID is <code>null</code>.
     */
    public String getQueryId() {
        if(queryId == null) {
            queryId = UUID.randomUUID().toString();
        }
        return queryId;
    }

    /**
     * Set the query ID.
     * @param queryId The query ID.
     */
    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    /**
     * Get the client ID.
     * @return The client ID.
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Set the client ID.
     * @param clientId The client ID.
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Gets the application.
     * @return The application.
     */
    public String getApplication() {
        return application;
    }

    /**
     * Set the application.
     * @param application The application.
     */
    public void setApplication(String application) {
        this.application = application;
    }

    /**
     * Get the object ID field.
     * @return The object ID field.
     */
    public String getObjectIdField() {
        return objectIdField;
    }

    /**
     * Set the object ID field.
     * @param objectIdField The object ID field.
     */
    public void setObjectIdField(String objectIdField) {
        this.objectIdField = objectIdField;
    }

    /**
     * Get the user query.
     * @return The user query.
     */
    public String getUserQuery() {
        return userQuery;
    }

    /**
     * Set the user query.
     * @param userQuery The user query.
     */
    public void setUserQuery(String userQuery) {
        this.userQuery = userQuery;
    }

    /**
     * Get the attributes.
     * @return A map of attributes.
     */
    public Map<String, String> getQueryAttributes() {
        if(queryAttributes == null) {
            queryAttributes = new HashMap<>();
        }
        return queryAttributes;
    }

    /**
     * Sets the attributes.
     * @param queryAttributes A map of attributes.
     */
    public void setQueryAttributes(Map<String, String> queryAttributes) {
        this.queryAttributes = queryAttributes;
    }

}
