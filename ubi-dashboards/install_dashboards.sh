
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

echo "${MAJOR}\nInstalling User Behavior Insights Dashboards${RESET}"
curl -X POST "http://$opensearch_dashboard/api/saved_objects/_import?overwrite=true" -H "osd-xsrf: true" --form file=@ubi_dashboard.ndjson > /dev/null

echo "${MAJOR}The UBI Dashboards were successfully installed${RESET}"
