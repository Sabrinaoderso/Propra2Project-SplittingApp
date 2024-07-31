#!/usr/bin/env bash
# Leider braucht postgres, selbst wenn es vor spring boot gestartet wird, etwas zeit, um zu starten
# deshalb warten wir hier 5 sekunden, bevor wir spring boot starten
sleep 5
java -jar /app/build/libs/Splitter-0.0.1-SNAPSHOT.jar
