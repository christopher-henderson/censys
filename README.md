# Build
```bash
mvn clean test compile package
```

# --help
```bash
$ ./run.sh --help
Usage: ./run.sh [--input=<DATA_PATH_REGEX>] [--ouput=<OUT_FILE>]
Note that <DATA_PATH_REGEX> must be a pattern of files to match, E.G. /temp/testdata/input*
<DATA_PATH_REGEX> Default: testdata/*
<OUT_FILE> Default: hashes.json

Note that Beam will write the output to <OUT_FILE>-00000-of-00001
```

# Run
```bash
$ ./run.sh --input=testdata/* --output=hired.json
```