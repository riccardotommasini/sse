# Simple SPARQL Endpoint

A simple endpoint to expose RDF4J SPARQL repository without to much hustle

# Usage

---

SSE allows you to deploy a RDF4J SPARQL repository using either native RDF data or a relational database and [Ontop](https://github.com/ontop/ontop).

Build with maven

    maven package

## Native Mode

    java -jar target/sse-1.0.jar file.properties

An es of properties file for the native mode is

    server.name=lite
    server.port=8080
    
    base.url=http://example.org/base
    file.location=src/main/resources/data.lite.ttl
    file.format=ttl
    named.graph=default

## Virtual Mode

    java -jar target/sse-1.0.jar --virtual file.properties

The virtual mode requires to point the properties file required by Ontop.

    server.name=lite
    server.port=8080
    
    base.url=http://example.org/base
    
    ontop.properties=src/main/resources/postgres.properties
    ontop.owl=src/main/resources/citybench.owl
    ontop.obda=src/main/resources/citybench-traffic.obda

# Docker

    docker build -t sse .
    docker run -p —name sse-running 8080:8080 sse

# Tests

---

I tested the service with **sparql-client** and **SPARQLWrapper.**

To run the javascript tests NPM is requried

    npm install sparql-client
    node app.js

To run the python test using python 3

    pip install SPARQLWrapper, rdflib-jsonld
    python SPARQLWrapperTestSelect.py 
    python SPARQLWrapperTestConstruct.py