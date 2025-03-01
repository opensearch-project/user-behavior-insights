
# How to load dashboards into your OpenSearch installation

This assumes that you have already have UBI data and the `ubi_queries` index created.  
See the `ubi-data-generator` tool to generate data.

Run the following shell script: `./install_dashboards.sh localhost:9200 localhost:5601`

* `ubi_dashboard.ndjson` represents the UBI Dashboards needed to analyze and understand the data.
