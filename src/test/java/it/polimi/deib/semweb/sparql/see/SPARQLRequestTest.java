package it.polimi.deib.semweb.sparql.see;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.semweb.sparql.sse.model.SPARQLRequest;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

public class SPARQLRequestTest {


    @Test
    public void test1() throws IOException {

        String query = "{\n" +
                "    \"query\":\"SELECT * WHERE {?s ?p ?o}\",\n" +
                "    \"named-graph-uri\":\"val2\",\n" +
                "    \"default-graph-uri\":\"val1\"\n" +
                "}\n";

        SPARQLRequest bean = new ObjectMapper()
                .readerFor(SPARQLRequest.class)
                .readValue(query);

        assertEquals("SELECT * WHERE {?s ?p ?o}", bean.query());
        assertEquals("val2", bean.get("named-graph-uri"));

    }


}
