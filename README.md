# svh-to-gbif
[![Build Status](https://travis-ci.com/Naturhistoriska/svh-to-gbif.svg?branch=master)](https://travis-ci.com/Naturhistoriska/svh-to-gbif)
[![codecov](https://codecov.io/gh/Naturhistoriska/svh-to-gbif/branch/master/graph/badge.svg)](https://codecov.io/gh/Naturhistoriska/svh-to-gbif)
[![License: AGPL v3](https://img.shields.io/badge/License-AGPL%20v3-blue.svg)](http://www.gnu.org/licenses/agpl-3.0)
`[![License: AGPL v3](https://img.shields.io/badge/License-AGPL%20v3-blue.svg)](http://www.gnu.org/licenses/agpl-3.0)`

Processing data from Sweden's Virtual Herbarium to GBIF.


## Prerequisites

* java 8
* maven 3
* mysql


## Project setup

* Clone this repo
* Setup database
  * Find database schemas from data directory, and import schemas into mysql
* Setup project-initdata.yml
  * In root directory, create project-initdata.yml.
  * Config project-initdata.yml file for files and mysql database. See sample_project_initdata.yml


## Build project


In root directory, run:
```
mvn clean package
```

# Run project


After build project, run:
```
java -jar svh-gbif-process/target/svh-gbif-process-thorntail.jar -Sinitdata
```
















