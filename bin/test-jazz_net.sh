#!/usr/bin/env bash
set -eu
set -o pipefail

# otherwise, set them in the CI config
[[ -f "$(dirname "$0")/jazz.env" ]] && . "$(dirname "$0")/jazz.env" || echo ".env file not found"

JAZZ_NET_PROJECT_ID="${JAZZ_NET_PROJECT_ID:-sandbox01}"
JAZZ_NET_PROJECT_NAME='smarx721 Project'

pushd "$(dirname "$0")/../lyo-client-samples"

mvn -B clean compile

# EWM
mvn -B exec:java -Dexec.mainClass="org.eclipse.lyo.samples.client.jazz.EWMSample" -Dexec.args="-url https://jazz.net/${JAZZ_NET_PROJECT_ID}-ccm/ \
  -user ${JAZZ_NET_USERNAME} -password \"${JAZZ_NET_PASSWORD}\" \
  -project \"${JAZZ_NET_PROJECT_NAME} (Change and Architecture Management)\" --basic"

# ERM
mvn -B exec:java -Dexec.mainClass="org.eclipse.lyo.samples.client.jazz.ERMSample" -Dexec.args="-url https://jazz.net/${JAZZ_NET_PROJECT_ID}-rm/ \
 -user ${JAZZ_NET_USERNAME} -password \"${JAZZ_NET_PASSWORD}\" \
 -project \"${JAZZ_NET_PROJECT_NAME} (Requirements Management)\" --basic"

# ETM
mvn -B exec:java -Dexec.mainClass="org.eclipse.lyo.samples.client.jazz.ETMSample" -Dexec.args="-url https://jazz.net/${JAZZ_NET_PROJECT_ID}-qm/ \
 -user ${JAZZ_NET_USERNAME} -password \"${JAZZ_NET_PASSWORD}\" \
 -project \"${JAZZ_NET_PROJECT_NAME} (Quality Management)\" --basic"

popd