# OpenSearch User Behavior Insights

This repository contains the OpenSearch plugin for the User Behavior Insights (UBI) capability. This plugin
facilitates persisting client-side events (e.g. item clicks, scroll depth) and OpenSearch queries for the purpose of analyzing the data
to improve search relevance and user experience.

UBI and this plugin project was originally proposed in the [OpenSearch UBI RFC](https://github.com/opensearch-project/OpenSearch/issues/12084).

## UBI Schemas

Please note that this repository is the implementation of the UBI plugin for OpenSearch.

> [!IMPORTANT] 
> This release targets to the 1.0.0 version of the UBI Specification.

For details on the JSON Schema used by UBI to send and receive queries and events please see the [UBI](https://github.com/o19s/ubi) repository and the links below.
* [Query Request Schema](https://o19s.github.io/ubi/docs/html/query.request.schema.html)
* [Query Response Schema](https://o19s.github.io/ubi/docs/html/query.response.schema.html)
* [Event Schema](https://o19s.github.io/ubi/docs/html/event.schema.html)

## Getting Help

* For questions or help getting started, please find us in the [OpenSearch Slack](https://opensearch.org/slack.html) in the `#plugins` channel.
* For bugs or feature requests, please create [a new issue](https://github.com/o19s/opensearch-ubi/issues/new/choose).

## Useful Commands

The `scripts/` directory contains example UBI requests for common use cases.

## User Quick Start

### Installing the Plugin

To get started, download the plugin zip file from the [releases](https://github.com/o19s/opensearch-ubi/releases). Next, install the plugin into OpenSearch with the following command:

```
bin/opensearch-plugin install file:/opensearch-ubi-1.0.0-os2.14.0.zip
```

You will be prompted while installing the plugin beacuse the plugin defines additional security permissions. These permissions allow the plugin to serialize query requests to JSON for storing and to allow the plugin to send query requests to Data Prepper. You can skip the prompt by adding the `--batch` argument to the above command.

To create the UBI indexes called `ubi_queries` and `ubi_events`, send a query to an OpenSearch index with the `ubi` query block added:

```
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
 }'
```

These indexes can also be created manually by using the [mapping files](https://github.com/o19s/opensearch-ubi/tree/2.14.0/src/main/resources):

```
curl -s -X PUT "http://localhost:9200/ubi-events" -H "Content-Type: application/json" --data-binary @/path/to/events-mapping.json
curl -s -X PUT "http://localhost:9200/ubi-queries" -H "Content-Type: application/json" --data-binary @/path/to/events-queries.json
```

### Capturing Queries

Queries sent to OpenSearch containing a `ubi` section in the `ext` block will be captured by the plugin and stored in the `ubi_queries` index. For example:

```
curl -s http://localhost:9200/your-index/_search -H "Content-Type: application/json" -d'
 {
  "ext": {
   "ubi": {
     "query_id": "12300d16cb-b6f1-4012-93ebcc49cac90426"
    }
   },
   "query": {
     "match": {
       "name": "toner"
     }
   }
 }'
```

As shown in the query above, the presence of the `ubi` block in the `ext` section causes the UBI plugin to capture the query and store it in the `ubi_queries` index.
The `ubi` block can contain the following parameters. All parameters are optional.

* `query_id` - A unique identifier for the query. If not provided, the plugin will generate a random UUID for the query and return the UUID in the query response. The `query_id` can be in any format but we use UUIDs in the examples.
* `user_query` - The user-entered query. This is meant to be the actual text the user provided to initiate the search.
* `client_id` - A unique identifier for the originator of the query. The client may be a user, an application, or any other originator of the query. The `client_id` can be in any format but we use UUIDs in the examples.
* `object_id_field` - The name of a field in the index that contains a unique identifier for each result. If not provided, the `_id` field is used.
* `query_attributes` - A map of arbitrary key/value pairs that will be indexed along with the query. This can be used to capture additional information about the query, such as an experiment ID or other details.

Following is an example query that provides the parameters:

```
curl -s http://localhost:9200/your-index/_search -H "Content-Type: application/json" -d'
 {
  "ext": {
   "ubi": {
     "query_id": "12300d16cb-b6f1-4012-93ebcc49cac90426",
     "user_query": "Toner",
     "client_id": "c4af7ca2-d6d2-4326-a41f-e616ebdd3d7b",
     "object_id_field": "product_name",
     "query_attributes": {
       "experiment_id": "12345"
     }
    }
   },
   "query": {
     "match": {
       "name": "toner"
     }
   }
 }'
```

Similar to a query request, query responses will also include a `ubi` section in the `ext` block.

```
{
  "took": 3,
  "timed_out": false,
  "_shards": {
    "total": 1,
    "successful": 1,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": {
      "value": 1,
      "relation": "eq"
    },
    "max_score": 2.2413535,
    "hits": [
      {
        "_index": "ecommerce",
        "_id": "968447",
        "_score": 2.2413535,
        "_source": {
          "id": "968447",
          "name": "Cyan Toner Cartridge"
        }
      }
    ]
  },
  "ext": {
    "ubi": {
      "query_id": "12300d16cb-b6f1-4012-93ebcc49cac90426"
    }
  }
}
```

The only field present in the query response will be the `query_id`. The value of the `query_id` is the same as the `query_id` provided in the query, or, if not provided, a random UUID.

### Indexing Queries

Queries can be either indexed in the local OpenSearch in the `ubi_queries` index, or queries can be sent to a Data Prepper `http_source` endpoint.

By default, queries are written to the local OpenSearch `ubi_queries` index as they are received. To see how the query is indexed, you can search the `ubi_queries` index:

```
curl -s http://localhost:9200/ubi_queries/_search -H "Content-Type: application/json" | jq
{
  "took": 3,
  "timed_out": false,
  "_shards": {
    "total": 1,
    "successful": 1,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": {
      "value": 1,
      "relation": "eq"
    },
    "max_score": 1,
    "hits": [
      {
        "_index": "ubi_queries",
        "_id": "6CrooY8BzxoaOvIPKtWj",
        "_score": 1,
        "_source": {
          "query_response_id": "e4bdd289-0875-421f-bc34-aa71eb8e1cb3",
          "user_query": "toner",
          "query_id": "12300d16cb-b6f1-4012-93ebcc49cac90426",
          "query_response_object_ids": [
            "9"
          ],
          "client_id": null,
          "query": "{\"query\":{\"match\":{\"name\":{\"query\":\"toner\",\"operator\":\"OR\",\"prefix_length\":0,\"max_expansions\":50,\"fuzzy_transpositions\":true,\"lenient\":false,\"zero_terms_query\":\"NONE\",\"auto_generate_synonyms_phrase_query\":true,\"boost\":1.0}}},\"ext\":{\"query_id\":\"12300d16cb-b6f1-4012-93ebcc49cac90426\",\"user_query\":\"toner\",\"client_id\":null,\"object_id_field\":null,\"query_attributes\":{\"system\":\"my_system\",\"experiment\":\"exp_1\"}}}",
          "timestamp": 1716408298072
        }
      }
    ]
  }
```

Each indexed query will have the following fields:

* `query_response_id` - A unique identifier for the query response.
* `user_query`- Corresponds to the `user_query` in the query request.
* `query_id` - Corresponds to the `query_id` in the query request, or a random UUID if a `query_id` was not provided in the query request.
* `query_response_object_ids` - A list of the values of the `object_id_field` field in the document.
* `client_id` - Corresponds to the `client_id` in the query request.
* `query` - The raw query that was provided to OpenSearch.
* `timestamp` - The Unix timestamp when the query was indexed.

#### Sending Queries to Data Prepper

To send queries to Data Prepper, configure the following properties in OpenSearch:

| Property            | Description                           | Example Value                 |
|---------------------|---------------------------------------|-------------------------------|
| ubi.dataprepper.url | Data Prepper's `http_source` endpoint | `http://localhost:2021/log/ingest` |

With these properties set, queries will no longer be indexed into the local OpenSearch. The `ubi_queries` index can be deleted. Queries will be sent to Data Prepper as they are received by OpenSearch.

### Capturing Events

The UBI plugin does *not* provide a way to capture client-side events. Sending client-side events requires action on the client to send the events to OpenSearch for indexing.

The following is an example of a client-side event. [Additional examples are available.](https://github.com/o19s/ubi/tree/main/samples)

```
curl -s -X POST http://localhost:9200/ubi_events/_doc/ -H "Content-Type: application/json" -d'
 {
  "action_name": "page_exit",
  "user_id": "1821196507152684",
  "query_id": "00112233-4455-6677-8899-aabbccddeeff",
  "session_id": "c3d22be7-6bdc-4250-91e1-fc8a92a9b1f9",
  "page_id": "/docs/latest/",
  "timestamp": "2024-05-16T12:34:56.789Z",
  "message_type": "INFO",
  "message": "On page /docs/latest/ for 3.35 seconds",
  "event_attributes": {
    "position":{},
    "object": {
      "idleTimeoutMs": 5000,
      "currentIdleTimeMs": 250,
      "checkIdleStateRateMs": 250,
      "isUserCurrentlyOnPage": true,
      "isUserCurrentlyIdle": false,
      "currentPageName": "http://localhost:4000/docs/latest/",
      "timeElapsedCallbacks": [],
      "userLeftCallbacks": [],
      "userReturnCallbacks": [],
      "visibilityChangeEventName": "visibilitychange",
      "hiddenPropName": "hidden"
    }
  }
 }'
```

## Analyzing Queries and Client-Side Events

With the queries and client-side events we can use OpenSearch's SQL capability to analyze the data.

### Queries with zero results

We can identify queries with zero results by querying either the `ubi_queries` or `ubi_events` indexes as shown below. Both queries should return the same value.

```sql
select
   count(0)
from ubi_queries
where query_response_object_ids is null
```

```sql
select
	count(0)
from ubi_events
where action_name='on_search' and event_attributes.data.data_detail.query_data.query_response_object_ids is null
order by timestamp
```

### Most Common Client-Side Events

Find the most common client-side events:

```sql
select
	action_name, count(0) Total  
from ubi_events
group by action_name
order by Total desc
```

action_name|Total
|---|---|
on_search|3199
brand_filter|3112
button_click|3150
type_filter|3149
product_hover|3132
product_sort|3115
login|2458
logout|1499
new_user_entry|208

### Client-Side Events Associated ith Queries

All client-side events that are associated with a query should have the same `query_id`.

```sql
select
	action_name, count(0) Total  
from ubi_events
where query_id is not null
group by action_name
order by Total desc
```
action_name|Total
|---|---|
on_search|1329
brand_filter|669
button_click|648
product_hover|639
product_sort|625
type_filter|613
logout|408

## Development

If you find bugs or want to request a feature, please create [a new issue](https://github.com/o19s/opensearch-ubi/issues/new/choose). For questions or to discuss how UBI works, please find us in the [OpenSearch Slack](https://opensearch.org/slack.html) in the `#plugins` channel.

The plugin provides an implementation of an `ActionFilter` plugin that can capture and index queries, a `SearchExtBuilder` that provides the UBI parameters, and the object classes used to index the queries. Testing is done by YAML rest tests and unit tests.

### Building and Testing

The plugin can be built using Gradle:

```
./gradlew build
```

To test and debug, build the OpenSearch docker image that contains the built plugin and then start the containers:

```
docker compose build && docker compose up
```

Or to start a three-node OpenSearch cluster:

```
docker compose build
docker compose -f docker-compose-cluster.yaml up
```

## Security
See [CONTRIBUTING](CONTRIBUTING.md#security-issue-notifications) for more information.

## License
This code is licensed under the Apache 2.0 License. See [LICENSE.txt](LICENSE.txt).

## Copyright
Copyright OpenSearch Contributors. See [NOTICE.txt](NOTICE.txt) for details.
