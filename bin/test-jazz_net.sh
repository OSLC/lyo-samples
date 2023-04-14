#!/usr/bin/env bash
set -eu
set -o pipefail

# otherwise, set them in the CI config
[[ -f "$(dirname "$0")/jazz.env" ]] && . "$(dirname "$0")/jazz.env" || echo ".env file not found"

pushd "$(dirname "$0")/../lyo-client-samples"

mvn -B clean compile

# EWM
mvn -B exec:java -Dexec.mainClass="org.eclipse.lyo.samples.client.EWMSample" -Dexec.args="-url https://jazz.net/sandbox02-ccm/ \
  -user ${JAZZ_NET_USERNAME} -password \"${JAZZ_NET_PASSWORD}\" \
  -project \"smarx721 Project (Change and Architecture Management)\" --basic"

# ERM
# FIXME: add -basic auth support to ERM sample
#mvn -B exec:java -Dexec.mainClass="org.eclipse.lyo.samples.client.ERMSample" -Dexec.args="-url https://jazz.net/sandbox02-rm/ \
#  -user ${JAZZ_NET_USERNAME} -password \"${JAZZ_NET_PASSWORD}\" \
#  -project \"smarx721 Project (Requirements Management)\" --basic"

popd