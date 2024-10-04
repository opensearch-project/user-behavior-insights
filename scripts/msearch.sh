#!/bin/bash -e

curl -s -X GET "http://localhost:9200/_msearch" -H 'Content-Type: application/json' -d'
{ "index": "ecommerce"}
{ "query": { "match_all": {} }, "ext": { "ubi": { "query_id": "11111" } } }
{ "index": "ecommerce"}
{ "query": { "match_all": {} }, "ext": { "ubi": { "query_id": "22222" } } }
'