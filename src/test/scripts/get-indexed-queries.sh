#!/bin/bash -e

# Get the indexed queries.

curl http://localhost:9200/ubi_queries/_search | jq
