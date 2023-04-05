#!/usr/bin/env bash
set -eu
set -o pipefail

# otherwise, set them in the CI config
[[ -f "$(dirname "$0")/jazz.env" ]] && . "$(dirname "$0")/jazz.env" || echo ".env file not found"

pushd "$(dirname "$0")/../lyo-client-samples"

mvn -B clean compile

# EWM
mvn -B exec:java -Dexec.mainClass="org.eclipse.lyo.samples.client.EWMSample" -Dexec.args="-url https://nordic.clm.ibmcloud.com/ccm/ \
  -user ${JAZZ_NORDIC_USERNAME} -password \"${JAZZ_NORDIC_PASSWORD}\" \
  -project \"OSLC Open Environment (EWM)\""

# ERM
mvn -B exec:java -Dexec.mainClass="org.eclipse.lyo.samples.client.ERMSample" -Dexec.args="-url https://nordic.clm.ibmcloud.com/rm/ \
  -user ${JAZZ_NORDIC_USERNAME} -password \"${JAZZ_NORDIC_PASSWORD}\" \
  -project \"OSLC Open Environment (DNG)\""

popd