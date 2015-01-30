# Monitoring Service [![Circle CI](https://circleci.com/gh/Rise-Vision/monitoring-service.svg?style=svg)](https://circleci.com/gh/Rise-Vision/monitoring-service)

## Introduction

Monitoring Service is a [Google Cloud Endpoints](https://cloud.google.com/appengine/docs/java/endpoints/) application service which contains APIs for retrieving monitoring data
related to our backend systems. It basically query a BigQuery Dataset which contains logs from these systems.

Project Name works in conjunction with [Rise Vision](http://www.risevision.com), the [digital signage management application](http://rva.risevision.com/) that runs on [Google Cloud](https://cloud.google.com).

At this time Chrome is the only browser that this project and Rise Vision supports.

## Built With
- Java (1.7)
- [GAE](https://cloud.google.com/appengine/docs)
- [Google Cloud Endpoints](https://cloud.google.com/appengine/docs/java/endpoints/)
- Maven
- [Mockito](https://github.com/mockito/mockito)


## Development

### Local Development Environment Setup and Installation
* Maven 3 is required so you need to do some things to make sure your apt-get doesn't install an older version of maven.

* clone the repo using Git to your local:
```bash
git clone https://github.com/Rise-Vision/monitoring-service.git
```

* cd into the repo directory
```bash
cd monitoring-service
```

* Run this command to build locally
``` bash
mvn clean install
```

### Run Local

* Run the application with below command, and ensure it's running by visiting your local server's api explorer's address (by default [localhost:8080/_ah/api/explorer](https://localhost:8080/_ah/api/explorer).)

``` bash
mvn appengine:devserver
```

* Get the client library with the below command. It will generate a client library jar file under the `target/endpoints-client-libs/<api-name>/target` directory of your project, as well as install the artifact into your local maven repository.
   
``` bash
mvn appengine:endpoints_get_client_lib
```

* Deploy your application to Google App Engine with

``` bash
mvn appengine:update
```

### Dependencies
* Junit for testing 
* Mockito for mocking and testing
* Google App Engine SDK

### Testing
* Run this command to test locally

``` bash
mvn test
```

## Submitting Issues
If you encounter problems or find defects we really want to hear about them. If you could take the time to add them as issues to this Repository it would be most appreciated. When reporting issues please use the following format where applicable:

**Reproduction Steps**

1. did this
2. then that
3. followed by this (screenshots / video captures always help)

**Expected Results**

What you expected to happen.

**Actual Results**

What actually happened. (screenshots / video captures always help)

## Contributing
All contributions are greatly appreciated and welcome! If you would first like to sound out your contribution ideas please post your thoughts to our [community](http://community.risevision.com), otherwise submit a pull request and we will do our best to incorporate it

## Resources
If you have any questions or problems please don't hesitate to join our lively and responsive community at http://community.risevision.com.

If you are looking for user documentation on Rise Vision please see http://www.risevision.com/help/users/

If you would like more information on developing applications for Rise Vision please visit http://www.risevision.com/help/developers/.

**Facilitator**

[Rodrigo Serviuc Pavezi](https://github.com/rodrigopavezi "Rodrigo Serviuc Pavezi")