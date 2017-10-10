var SparqlClient = require('sparql-client');


var defaultParameters = {
        'format': 'application/rdf+xml',
        'content-type': 'application/rdf+xml'
    };

const lite = new SparqlClient('http://localhost:4040/sparql');
const fuseki = new SparqlClient('http://localhost:3030/ds/query',  defaultParameters );

construct = "CONSTRUCT {  ?s ?p ?o  } FROM <http://example.org/sgraph> WHERE {  ?s ?p ?o  }"
select = "SELECT * FROM <http://example.org/sgraph>  WHERE { ?s ?p ?o  }"

fuseki.query(construct).execute(function (err,data) {
    console.log("-----fuseki CONSTRUCT----");
    console.log(data);
    console.log("-----fuseki CONSTRUCT----");

});


