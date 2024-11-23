
# Ansi color code variables
ERROR='\033[0;31m'
MAJOR='\033[0;34m'
MINOR='\033[0;37m    '
RESET='\033[0m' # No Color

opensearch=$1
opensearch_dashboard=$2
set -eo pipefail

if [ -z "$opensearch" ]; then
    echo "Error: please pass in both the opensearch url and the opensearch dashboard url"
    exit 1
fi
if [ -z "$opensearch_dashboard" ]; then
    echo "Error: please pass in both the opensearch url and the opensearch dashboard url"
    exit 1
fi

echo "${MAJOR}Using Open Search and Open Search Dashboards at $opensearch and $opensearch_dashboard respectively.${RESET}"

echo "${MAJOR}Deleting index sqe_metrics_sample_data${RESET}"
curl -s -X DELETE $opensearch/sqe_metrics_sample_data/

echo "${MAJOR}Populating index sqe_metrics_sample_data with sample metric data${RESET}"
curl -s -H 'Content-Type: application/x-ndjson' -XPOST "$opensearch/sqe_metrics_sample_data/_bulk?pretty=false&filter_path=-items" --data-binary @sample_search_quality_evaluation_data.ndjson 

echo "${MAJOR}\nInstalling Quality Evaluation Framework Dashboards${RESET}"
curl -X POST "$opensearch_dashboard/api/saved_objects/_import?overwrite=true" -H "osd-xsrf: true" --form file=@search_quality_evaluation_dashboard.ndjson > /dev/null

echo "${MAJOR}The Search Quality Evaluation Framework Dashboards were successfully installed${RESET}"
