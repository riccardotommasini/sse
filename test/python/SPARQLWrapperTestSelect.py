from SPARQLWrapper import SPARQLWrapper, JSON, POST

q = "SELECT * WHERE { ?s ?p ?o }"
sparql = SPARQLWrapper("http://localhost:8080/lite/sparql")
sparql.setQuery(q)
sparql.setMethod(POST)
sparql.setReturnFormat(JSON)
results = sparql.query().convert()

print(results)

