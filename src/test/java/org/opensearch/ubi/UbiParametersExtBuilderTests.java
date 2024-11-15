/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi;

import org.opensearch.common.io.stream.BytesStreamOutput;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.core.common.bytes.BytesReference;
import org.opensearch.core.common.io.stream.StreamInput;
import org.opensearch.core.xcontent.XContentHelper;
import org.opensearch.core.xcontent.XContentParser;
import org.opensearch.test.OpenSearchTestCase;
import org.opensearch.ubi.ext.UbiParameters;
import org.opensearch.ubi.ext.UbiParametersExtBuilder;

import java.io.EOFException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UbiParametersExtBuilderTests extends OpenSearchTestCase {

    public void testCtor() {

        final Map<String, String> queryAttributes = new HashMap<>();

        final UbiParametersExtBuilder builder = new UbiParametersExtBuilder();
        final UbiParameters parameters = new UbiParameters("query_id", "user_query", "client_id", "app", "object_id_field", queryAttributes);
        builder.setParams(parameters);
        assertEquals(parameters, builder.getParams());

    }

    public void testParse() throws IOException {
        XContentParser xcParser = mock(XContentParser.class);
        when(xcParser.nextToken()).thenReturn(XContentParser.Token.START_OBJECT).thenReturn(XContentParser.Token.END_OBJECT);
        UbiParametersExtBuilder builder = UbiParametersExtBuilder.parse(xcParser);
        assertNotNull(builder);
        assertNotNull(builder.getParams());
    }

    public void testXContentRoundTrip() throws IOException {
        UbiParameters param1 = new UbiParameters("query_id", "user_query", "client_id", "app", "object_id_field", Collections.emptyMap());
        UbiParametersExtBuilder extBuilder = new UbiParametersExtBuilder();
        extBuilder.setParams(param1);
        XContentType xContentType = randomFrom(XContentType.values());
        BytesReference serialized = XContentHelper.toXContent(extBuilder, xContentType, true);
        XContentParser parser = createParser(xContentType.xContent(), serialized);
        UbiParametersExtBuilder deserialized = UbiParametersExtBuilder.parse(parser);
        assertEquals(extBuilder, deserialized);
        UbiParameters parameters = deserialized.getParams();
        assertEquals("query_id", parameters.getQueryId());
        assertEquals("user_query", parameters.getUserQuery());
        assertEquals("client_id", parameters.getClientId());
        assertEquals("app", parameters.getApplication());
        assertEquals("object_id_field", parameters.getObjectIdField());
    }

    public void testXContentRoundTripAllValues() throws IOException {
        UbiParameters param1 = new UbiParameters("query_id", "user_query", "client_id", "app","object_id_field", Collections.emptyMap());
        UbiParametersExtBuilder extBuilder = new UbiParametersExtBuilder();
        extBuilder.setParams(param1);
        XContentType xContentType = randomFrom(XContentType.values());
        BytesReference serialized = XContentHelper.toXContent(extBuilder, xContentType, true);
        XContentParser parser = createParser(xContentType.xContent(), serialized);
        UbiParametersExtBuilder deserialized = UbiParametersExtBuilder.parse(parser);
        assertEquals(extBuilder, deserialized);
    }

    public void testStreamRoundTrip() throws IOException {
        UbiParameters param1 = new UbiParameters("query_id", "user_query", "client_id", "app","object_id_field", Collections.emptyMap());
        UbiParametersExtBuilder extBuilder = new UbiParametersExtBuilder();
        extBuilder.setParams(param1);
        BytesStreamOutput bso = new BytesStreamOutput();
        extBuilder.writeTo(bso);
        UbiParametersExtBuilder deserialized = new UbiParametersExtBuilder(bso.bytes().streamInput());
        assertEquals(extBuilder, deserialized);
        UbiParameters parameters = deserialized.getParams();
        assertEquals("query_id", parameters.getQueryId());
        assertEquals("user_query", parameters.getUserQuery());
        assertEquals("client_id", parameters.getClientId());
        assertEquals("app", parameters.getApplication());
        assertEquals("object_id_field", parameters.getObjectIdField());
    }

    public void testStreamRoundTripAllValues() throws IOException {
        UbiParameters param1 = new UbiParameters("query_id", "user_query", "client_id", "app","object_id_field", Collections.emptyMap());
        UbiParametersExtBuilder extBuilder = new UbiParametersExtBuilder();
        extBuilder.setParams(param1);
        BytesStreamOutput bso = new BytesStreamOutput();
        extBuilder.writeTo(bso);
        UbiParametersExtBuilder deserialized = new UbiParametersExtBuilder(bso.bytes().streamInput());
        assertEquals(extBuilder, deserialized);
    }

}
