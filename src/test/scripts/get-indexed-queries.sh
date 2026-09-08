#!/bin/bash -e

# Copyright OpenSearch Contributors
# SPDX-License-Identifier: Apache-2.0

# Get the indexed queries.

curl http://localhost:9200/ubi_queries/_search | jq
