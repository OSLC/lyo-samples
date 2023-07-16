#!/usr/bin/env bash
set -eu
set -o pipefail

# otherwise, set them in the CI config
[[ -f "$(dirname "$0")/jazz.env" ]] && . "$(dirname "$0")/jazz.env" || echo ".env file not found"

pushd "$(dirname "$0")/../lyo-client-samples"

mvn -B clean compile

# EWM
mvn -B exec:java -Dexec.mainClass="org.eclipse.lyo.samples.client.EWMSample" -Dexec.args="-url https://oslc.itm.kth.se/ccm/ \
    -user ${JAZZ_ITM_USERNAME} -password \"${JAZZ_ITM_PASSWORD}\" \
    -project \"Lyo Smoke Test Lifecycle Project (Change Management)\""

# ERM
mvn -B exec:java -Dexec.mainClass="org.eclipse.lyo.samples.client.ERMSample" -Dexec.args="-url https://oslc.itm.kth.se/rm/ \
    -user ${JAZZ_ITM_USERNAME} -password \"${JAZZ_ITM_PASSWORD}\" \
    -project \"Lyo Smoke Test Lifecycle Project (Requirements)\""

popd