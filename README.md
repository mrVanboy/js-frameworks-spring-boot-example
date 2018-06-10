# JavaScript Frameworks REST API in Spring Boot
Simple example of the application with docker.

## Getting started
1. Install [Docker](https://docs.docker.com/install/)
2. Clone repo
3. Run `docker build -t js-frameworks .`
4. After successfuull build also run: `docker run --rm -ti -p 8080:8080 js-frameworks`
5. Open [localhost:8080](http://localhost:8080) in your browser

## Documentation
Documentation of REST is available in `openapi.yml` that can be viewed
via [Swager Editor](https://editor.swagger.io) or also after running
contaier at root path.

## Description
It is a simple application built on Spring and REST, which is used to
register JavaScript frameworks. As Java programmers we know that their
world is very confusing and unstable. That is why it is good to record
their most important properties in one place.

Except the name framework entity has properties "version",
"deprecationDate" and "hypeLevel". They indicate the version of the
framework, the date when the / framework was identified as obsolete and
the current level of irrational fanatic admiration :-).

Via REST is possible to created new frameworks, updates existing and also
delete them.

All features are covered by the tests.
