
# Ansi color code variables
ERROR='\033[0;31m'
MAJOR='\033[0;34m'
MINOR='\033[0;37m    '
RESET='\033[0m' # No Color

: ${OPEN_SEARCH:="localhost:9200"}
: ${OPEN_SEARCH_DASHBOARDS:="localhost:5601"}

set -eo pipefail

echo "${MAJOR}Using Open Search and Open Search Dashboards at $OPEN_SEARCH and $OPEN_SEARCH_DASHBOARDS respectively.${RESET}"
echo "  (set environment variables OPEN_SEARCH and OPEN_SEARCH_DASHBOARDS otherwise)\n"

echo "${MAJOR}\nInstalling Quality Evaluation Framework Dashboards${RESET}"
curl -X POST "http://$OPEN_SEARCH_DASHBOARDS/api/saved_objects/_import?overwrite=true" -H "osd-xsrf: true" --form file=@ubi_dashboard.ndjson > /dev/null

echo "${MAJOR}The UBI Dashboards were successfully installed${RESET}"
