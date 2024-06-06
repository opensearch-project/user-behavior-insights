#!/bin/bash -e

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
