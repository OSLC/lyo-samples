#!/usr/bin/env bash
set -eu
set -o pipefail

# otherwise, set them in the CI config
[[ -f "$(dirname "$0")/polarion.env" ]] && . "$(dirname "$0")/polarion.env" || echo ".env file not found"

POLARION_ROOT_SERVICES="${POLARION_ROOT_SERVICES:-https://polarion.itm.kth.se/polarion/oslc/rootservices}"
POLARION_PROJECT="${POLARION_PROJECT:-OslcDev}"

pushd "$(dirname "$0")/../lyo-client-samples"

mvn -B clean compile

# Polarion Sample
mvn -B exec:java -Dexec.mainClass="org.eclipse.lyo.samples.client.polarion.PolarionSample" -Dexec.args="-url ${POLARION_ROOT_SERVICES} \
 -token ${POLARION_PAT} \
 -project \"${POLARION_PROJECT}\""

popd
