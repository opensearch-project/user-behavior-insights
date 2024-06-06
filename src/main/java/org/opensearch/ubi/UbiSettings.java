/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.ubi;

import org.opensearch.common.settings.Setting;

import java.util.Collections;
import java.util.List;

/**
 * The UBI settings.
 */
public class UbiSettings {

    /**
     * The name of the Data Prepper http_source URL for receiving queries.
     */
    public static final String DATA_PREPPER_URL = "ubi.dataprepper.url";

    private static final Setting<String> DATA_PREPPER_URL_SETTING = Setting.simpleString(
            DATA_PREPPER_URL,
            Setting.Property.Dynamic,
            Setting.Property.NodeScope);

    /**
     * Gets a list of the UBI plugin settings.
     * @return A list of the UBI plugin settings.
     */
    public static List<Setting<?>> getSettings() {
        return Collections.singletonList(
                DATA_PREPPER_URL_SETTING
        );
    }

}
