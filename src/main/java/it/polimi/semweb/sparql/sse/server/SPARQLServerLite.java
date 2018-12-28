package it.polimi.semweb.sparql.sse.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.semweb.sparql.sse.model.SPARQLRequest;
import it.polimi.semweb.sparql.sse.model.SPARQLResponse;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.http.entity.ContentType;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.Query;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import spark.Response;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static org.apache.http.HttpHeaders.ACCEPT;
import static spark.Spark.*;

/**
 * Created by riccardo on 10/09/2017.
 */
public class SPARQLServerLite extends SPARQLProtocolService {



    public SPARQLServerLite(Repository repo, PropertiesConfiguration p) throws ConfigurationException {
        super(repo, p);
    }

    public void initialize() {

        port(prop.getPort());

        path(prop.getServerName(), () -> {
            get(prop.getGetEndpointPath(), (request, response) -> {
                RepositoryConnection connection = repository.getConnection();
                try {
                    Query q = connection.prepareQuery(QueryLanguage.SPARQL, URLDecoder.decode(request.params(":query"), "UTF-8"));
                    String results = executeQuery(request.headers(ACCEPT), q, getNamespaces());
                    response.status(SUCCESS_OK);
                    response.type(APP_SPARQL_JSON_RESULTS);
                    return results;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    response.status(CLIENT_ERROR_BAD_REQUEST);
                    response.type(APP_JSON);
                    return new SPARQLResponse(CLIENT_ERROR_BAD_REQUEST, "Wrong Encoding");
                } finally {
                    connection.close();
                }
            });

            post(prop.getPostEndpointPath(), (request, response) -> {
                String body = request.body();

                SPARQLRequest req = new SPARQLRequest(body);

                String headers = request.headers(ACCEPT);

                if (req.contains(SPARQLServerLite.DEF_GRAPH_URI) || req.contains(SPARQLServerLite.NMD_GRAPH_URI)) {
                    response.status(CLIENT_ERROR_BAD_REQUEST);
                    return new SPARQLResponse(CLIENT_ERROR_BAD_REQUEST, "Service does not allow protocol clients to specify the RDF Dataset.");
                } else {
                    RepositoryConnection connection = getRepository().getConnection();
                    Query q = connection.prepareQuery(QueryLanguage.SPARQL, req.query());
                    try {
                        String s = executeQuery(headers, q, namespaces);
                        response.status(SUCCESS_OK);
                        response.type(headers);
                        return s;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        response.status(CLIENT_ERROR_BAD_REQUEST);
                        response.type(ContentType.TEXT_PLAIN.getMimeType());
                        return new SPARQLResponse(CLIENT_ERROR_BAD_REQUEST, "Unsupported Encoding");
                    } catch (MalformedQueryException e) {
                        e.printStackTrace();
                        response.status(CLIENT_ERROR_BAD_REQUEST);
                        return "The request is not a legal sequence of characters in the language defined by the SPARQL grammar";
                    } catch (RepositoryException e) {
                        e.printStackTrace();
                        response.status(CLIENT_ERROR_BAD_REQUEST);
                        return "Query Not Executed";
                    } finally {
                        connection.close();
                    }
                }
            });
        });
    }

}
