#!/usr/bin/env bash

java -jar sse-1.0.jar $1 $2

${JAVA} ${ONTOP_JAVA_ARGS} -cp "${ONTOP_HOME}/lib/*:${ONTOP_HOME}/jdbc/*" -Dlogback.configurationFile="${ONTOP_HOME}/log/logback.xml" \
 it. -v @$