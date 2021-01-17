# Java Version
I wrote and tested this using Java 11. I tried Java 14 although Beam does not appear to like such a cutting edge version.

# Test and Build
```bash
mvn clean test compile package
```

A packaged jar will be available at... 

`target/hasher-1.0-SNAPSHOT-jar-with-dependencies.jar`

Although a runner script is available at `./run.sh`

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
...
...
...
$ less hired-00000-of-00001.json
{
  "/home/chris/projects/censys/testdata/input-0048": "5ac02c104e64f82c9efbf2d9f241e1f722b7e90374654f363eceaf04421350f5",
  "/home/chris/projects/censys/testdata/input-0049": "732131d7b74e0dd60c06c0353740fa11993661d376192f0baab95458753814f5",
  "/home/chris/projects/censys/testdata/input-0044": "08877cf31cb69e5f7591727b5f5811b236738c8c156b7306500dcf1cede977f6",
  "/home/chris/projects/censys/testdata/input-0045": "79df5ec0594863ecb2e63060011b7c90b0ab1c2eb02b19cdefe7fe1316fd48a2",
  "/home/chris/projects/censys/testdata/input-0046": "3a0c665c6be138fab0047e971a19a294cf68e598981bc820e4217cdbdf8cb582",
  "/home/chris/projects/censys/testdata/input-0047": "85a1ec5c3805ea349ca58de7e8505e448517bd08355403b40d3382fe888b2d1d",
  "/home/chris/projects/censys/testdata/input-0040": "7218645c36ad6bffe9e6825b0facc8a673e8c7d3eb2f9d845a966e6b076af846",
  "/home/chris/projects/censys/testdata/input-0041": "7943c9607dfc2fa2a4379d69b4d5a383cf05751b4d5d536be6356d03e52aecdf",
  "/home/chris/projects/censys/testdata/input-0042": "30b84feeee73793f2dfa37f0d743cf935bdd480326e859a151c5be71a918a7e1",
  "/home/chris/projects/censys/testdata/input-0043": "a226a3937d2c2ffd6b9ee7ec2ae479692e360839210a64ae476fe87bb97fb3bc",
  "/home/chris/projects/censys/testdata/input-0050": "4e182cc5838546035a382566dfe734427e084940bbf1e5c8346e9c164c7c936a",
  "/home/chris/projects/censys/testdata/input-0059": "d7b4289456fd37c5dfaeaebec379adb59ad995391c04ee9d1aa6cbec63db6745",
  "/home/chris/projects/censys/testdata/input-0055": "2000412bf353e001c280eb5c4733a8a9080ad773e16f5ec64c6b254aa385cf5b",
  "/home/chris/projects/censys/testdata/input-0056": "b25a5ba33ecdea73413d48a3cb97005787db70b1b5942fb2bbbc6cc5319d0682",
  "/home/chris/projects/censys/testdata/input-0057": "5158bb77a2bc33113b1622ea200daff2a45a80ecc6dd2941154081912961946e",
  "/home/chris/projects/censys/testdata/input-0058": "d92b93baeae9579fe078b79518edc4b5cf186c66c1b502b5e10eb10c0198011d",
...
}
```
