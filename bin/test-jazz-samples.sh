#!/usr/bin/env bash
set -eu
set -o pipefail

# otherwise, set them in the CI config
[[ -f "$(dirname "$0")/jazz.env" ]] && . "$(dirname "$0")/jazz.env" || echo ".env file not found"

pushd "$(dirname "$0")/../lyo-client-samples"

mvn clean compile

# EWM
mvn exec:java -Dexec.mainClass="org.eclipse.lyo.samples.client.EWMSample" -Dexec.args="-url https://jazz.net/sandbox02-ccm/ \
  -user ${JAZZ_NET_USERNAME} -password \"${JAZZ_NET_PASSWORD}\" \
  -project \"smarx721 Project (Change and Architecture Management)\" --basic"
mvn exec:java -Dexec.mainClass="org.eclipse.lyo.samples.client.EWMSample" -Dexec.args="-url https://nordic.clm.ibmcloud.com/ccm/ \
  -user ${JAZZ_NORDIC_USERNAME} -password \"${JAZZ_NORDIC_PASSWORD}\" \
  -project \"OSLC Open Environment (EWM)\""
mvn exec:java -Dexec.mainClass="org.eclipse.lyo.samples.client.EWMSample" -Dexec.args="-url https://oslc.itm.kth.se:9443/ccm/ \
    -user ${JAZZ_ITM_USERNAME} -password \"${JAZZ_ITM_PASSWORD}\" \
    -project \"JKE Banking (Change Management)\""

# ERM
# FIXME: add -basic
#mvn exec:java -Dexec.mainClass="org.eclipse.lyo.samples.client.ERMSample" -Dexec.args="-url https://jazz.net/sandbox02-rm/ \
#  -user ${JAZZ_NET_USERNAME} -password \"${JAZZ_NET_PASSWORD}\" \
#  -project \"smarx721 Project (Requirements Management)\" --basic"
mvn exec:java -Dexec.mainClass="org.eclipse.lyo.samples.client.ERMSample" -Dexec.args="-url https://nordic.clm.ibmcloud.com/rm/ \
  -user ${JAZZ_NORDIC_USERNAME} -password \"${JAZZ_NORDIC_PASSWORD}\" \
  -project \"OSLC Open Environment (DNG)\""
mvn exec:java -Dexec.mainClass="org.eclipse.lyo.samples.client.ERMSample" -Dexec.args="-url https://oslc.itm.kth.se:9443/rm/ \
    -user ${JAZZ_ITM_USERNAME} -password \"${JAZZ_ITM_PASSWORD}\" \
    -project \"JKE Banking (Requirements Management)\""

popd