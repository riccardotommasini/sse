from SPARQLWrapper import SPARQLWrapper, JSONLD, POST

sparql = SPARQLWrapper("http://localhost:8080/lite/sparql")

q = "CONSTRUCT {?s ?p ?o} WHERE { ?s ?p ?o }"
sparql.setQuery(q)
sparql.setMethod(POST)
sparql.setReturnFormat(JSONLD,)
results = sparql.query().convert()


#save
results.serialize('ciao')

#print
print(results.serialize(format='n3'))