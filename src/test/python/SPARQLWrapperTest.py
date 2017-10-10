from SPARQLWrapper import SPARQLWrapper, JSON, POST

q = "SELECT * WHERE { GRAPH ?g {?s ?p ?o} }"
sparql = SPARQLWrapper("http://localhost:4040/sparql")
sparql.setQuery(q)
sparql.setMethod(POST)
sparql.setReturnFormat(JSON)
results = sparql.query().convert()

print results

q = "CONSTRUCT {?s ?p ?o} WHERE { GRAPH ?g {?s ?p ?o} }"
sparql.setQuery(q)
sparql.setMethod(POST)
sparql.setReturnFormat(JSON)
results = sparql.query().convert()

print results

##USAGE python <stream name> <query-template> <stream-mapping-template> <output-file>