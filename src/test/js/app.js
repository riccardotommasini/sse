var SparqlClient = require('sparql-client-2');
var jsonld = require('jsonld');
var dateTime = require('node-datetime');

// var lite = new SparqlClient('http://localhost:8080/AarhusTrafficData158505/sparql');

// select = "PREFIX : <http://www.rsp-lab.org/triplewave/citybench/>"
// + " PREFIX ct: <http://www.insight-centre.org/citytraffic#>"
// + " PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>"
// + " SELECT ?o ?time"
// + " WHERE { ?o a ssn:Observation ; :eventTime ?time }"
// + " ORDER BY DESC(?time) limit 10"

// lite.query(select).execute(function (err,data) {
//     console.log("-----lite SELECT----");
//     console.log(data.results.bindings);
//     console.log("-----lite SELECT----");
// });


var defaultParameters = {
        format: 'json'
    };


var stream = "AarhusTrafficData158505"
var lite2 = new SparqlClient('http://localhost:8080/'+stream+'/sparql', defaultParameters );

var obs = "<http://www.rsp-lab.org/triplewave/citybench/AarhusTrafficData158505/20746168-MT59140dedec82fcb35b22b8571509210b>"

costruct = "PREFIX ct: <http://www.insight-centre.org/citytraffic#>"
+ " PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>"
+ " CONSTRUCT { "
+  obs + " a ssn:Observation ;"    
+ " ct:hasValue ?value ; "
+ " ssn:observedBy ?sensor ;  "
+ " ssn:observedProperty ?property . } "
+ " WHERE { "
+  obs +" ct:hasValue ?value ; "
+ " ssn:observedBy ?sensor ;"
+ " ssn:observedProperty ?property . } "

var context = {
  "ssn": "http://purl.oclc.org/NET/ssnx/ssn#",
  "ses": "http://www.insight-centre.org/dataset/SampleEventService#",
  "ct": "http://www.insight-centre.org/citytraffic#",
  "prov":"http://www.w3.org/ns/prov#",
  "sld":"http://www.streamreasoning.org/sld#",
  "xsd":"http://www.w3.org/2001/XMLSchema#",
  "generatedAt" : {
         "@id": "http://www.w3.org/ns/prov#generatedAtTime",
          "@type": "http://www.w3.org/2001/XMLSchema#dateTime"
    }
};

console.log(costruct)

var dt = dateTime.create();
var ts = dt.format('Y-m-d H:M:S');

var processingTime = { "@value": ts,
    "@type": "xsd:dateTime" }  

var eventTime = { "@value": ts,
    "@type": "xsd:dateTime" }

lite2.query(costruct).execute(function (err,data) {
    console.log("-----lite costruct----");
    console.log(data);
    console.log("-----CONTEXT----");

    j = {
            "@id":"http://localhost:8080/AarhusTrafficData158505/28063750-AS1d3b3b82f1b772c040cb2883c4a3fc20",
            "http://www.w3.org/ns/prov#generatedAtTime":processingTime,
            "sld:eventTime":eventTime,
            "@graph" : data
     }

    jsonld.compact(j, context, function(err, compacted) {
    
        console.log(compacted['prov:generatedAtTime'])
       
    console.log(JSON.stringify(compacted, null, 2));
    });

    console.log("-----lite costruct----");
});


jsonld.compact({ "@id":"http://a.o/a", "name":"Riccardo"} , {"name": { "@id":"http://schema.org/name", "@type":"@id"}}, {"graph":true}, function(err, compacted) {
           
    console.log(JSON.stringify(compacted, null, 2));
});


 
// compact a document according to a particular context 
// see: http://json-ld.org/spec/latest/json-ld/#compacted-document-form 
