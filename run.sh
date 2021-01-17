#!/usr/bin/env bash

# Beam has it's PipelineOptions which appears to be attempting
# to be an argparser of a kind, but I did not have the time to
# learn yet another argparser and how to register help docs
#
# So...here are help docs
if [ "$1" = "--help" ]; then
  echo "Usage: ./run.sh [--input=<DATA_PATH_REGEX>] [--ouput=<OUT_FILE>]"
  echo "Note that <DATA_PATH_REGEX> must be a pattern of files to match, E.G. /temp/testdata/input*"
  echo "<DATA_PATH_REGEX> Default: testdata/*"
  echo "<OUT_FILE> Default: hashes.json"
  exit 0
fi

java -jar target/hasher-1.0-SNAPSHOT-jar-with-dependencies.jar "$@"