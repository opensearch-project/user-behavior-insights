/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi;

import org.opensearch.action.support.ActionFilter;
import org.opensearch.client.Client;
import org.opensearch.cluster.metadata.IndexNameExpressionResolver;
import org.opensearch.cluster.service.ClusterService;
import org.opensearch.common.settings.Setting;
import org.opensearch.core.common.io.stream.NamedWriteableRegistry;
import org.opensearch.core.xcontent.NamedXContentRegistry;
import org.opensearch.env.Environment;
import org.opensearch.env.NodeEnvironment;
import org.opensearch.plugins.*;
import org.opensearch.repositories.RepositoriesService;
import org.opensearch.script.ScriptService;
import org.opensearch.telemetry.metrics.MetricsRegistry;
import org.opensearch.telemetry.tracing.Tracer;
import org.opensearch.threadpool.ThreadPool;
import org.opensearch.ubi.ext.UbiParametersExtBuilder;
import org.opensearch.watcher.ResourceWatcherService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;

/**
 * OpenSearch User Behavior Insights
 */
public class UbiPlugin extends Plugin implements ActionPlugin, SearchPlugin, TelemetryAwarePlugin {

    private ActionFilter ubiActionFilter;

    /**
     * Creates a new instance of {@link UbiPlugin}.
     */
    public UbiPlugin() {}

    @Override
    public List<Setting<?>> getSettings() {
        return UbiSettings.getSettings();
    }

    @Override
    public List<ActionFilter> getActionFilters() {
        return singletonList(ubiActionFilter);
    }

    @Override
    public Collection<Object> createComponents(
        Client client,
        ClusterService clusterService,
        ThreadPool threadPool,
        ResourceWatcherService resourceWatcherService,
        ScriptService scriptService,
        NamedXContentRegistry xContentRegistry,
        Environment environment,
        NodeEnvironment nodeEnvironment,
        NamedWriteableRegistry namedWriteableRegistry,
        IndexNameExpressionResolver indexNameExpressionResolver,
        Supplier<RepositoriesService> repositoriesServiceSupplier,
        Tracer tracer,
        MetricsRegistry metricsRegistry
    ) {

        this.ubiActionFilter = new UbiActionFilter(client, environment, tracer);
        return Collections.emptyList();

    }

    @Override
    public List<SearchExtSpec<?>> getSearchExts() {

        final List<SearchExtSpec<?>> searchExts = new ArrayList<>();

        searchExts.add(
            new SearchExtSpec<>(UbiParametersExtBuilder.UBI_PARAMETER_NAME, UbiParametersExtBuilder::new, UbiParametersExtBuilder::parse)
        );

        return searchExts;

    }

}
