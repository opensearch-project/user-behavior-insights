#!/bin/bash -e

# Delete and recreate the ubi indexes using the schema defined in the plugin
 
curl -s -X DELETE http://localhost:9200/ubi_queries,ubi_events | jq

curl -s -X POST http://localhost:9200/_plugins/ubi/initialize | jq
