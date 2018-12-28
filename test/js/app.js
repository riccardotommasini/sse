var SparqlClient = require('sparql-client');

var defaultParameters = {
        'format': 'application/rdf+xml',
        'content-type': 'application/rdf+xml'
    };

const lite = new SparqlClient('http://localhost:8080/lite/sparql', defaultParameters);

construct = "CONSTRUCT {  ?s ?p ?o  } WHERE {  ?s ?p ?o  }"
select = "SELECT *  WHERE { ?s ?p ?o  }"

lite.query(construct).execute(function (err,data) {
    console.log("-----lite CONSTRUCT----");
    console.log(data);
    console.log("-----lite CONSTRUCT----");

});

lite.query(select).execute(function (err,data) {
    console.log("-----lite SELECT----");
    console.log(data);
    console.log("-----lite SELECT----");

});


