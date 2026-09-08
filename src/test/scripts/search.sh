#!/bin/bash -e

# Copyright OpenSearch Contributors
# SPDX-License-Identifier: Apache-2.0

# An example UBI search of the ecommerce index.

curl -s http://localhost:9200/ecommerce/_search -H "Content-Type: application/json" -d'
 {
  "ext": {
   "ubi": {
    }
   },
   "query": {
     "match": {
       "name": "toner"
     }
   }
 }' | jq
