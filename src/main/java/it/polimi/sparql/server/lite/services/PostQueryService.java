package it.polimi.sparql.server.lite.services;

import it.polimi.sparql.server.lite.SPARQLServerLite;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.Query;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Options;
import org.restlet.resource.Post;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PostQueryService extends SPARQLProtocolService {

    @Options
    public void optionsRequestHandler() {
        Set<Method> allowedMethodes = new HashSet<Method>();
        allowedMethodes.add(Method.POST);
        getResponse().setAccessControlAllowMethods(allowedMethodes);
        getResponse().setAccessControlAllowOrigin(getClientAddress());
    }

    @Post
    public void SPARQLpostEncoded(Representation rep) {
        getResponse().setAccessControlAllowOrigin(getClientAddress());
        Request request = getRequest();
        Form form = new Form(rep);

        //String[] default_graphs = form.getValuesArray(SPARQLServerLite.DEF_GRAPH_URI);
        //String[] named_graphs = form.getValuesArray(SPARQLServerLite.NMD_GRAPH_URI);


        Map<String, String> valuesMap = form.getValuesMap();
        if (valuesMap.containsKey(SPARQLServerLite.DEF_GRAPH_URI) || valuesMap.containsKey(SPARQLServerLite.NMD_GRAPH_URI)) {
            //NOTE only the default dateset
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Service does not allow protocol clients to specify the RDF Dataset.");
        } else {

            RepositoryConnection connection = getRepository().getConnection();
            Query q = connection.prepareQuery(QueryLanguage.SPARQL, form.getFirstValue(SPARQLServerLite.QUERY));

            try {
                executeQuery(q, getNamespaces());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Unsupported Encoding");
            } catch (MalformedQueryException e) {
                e.printStackTrace();
                getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "The request is not a legal sequence of characters in the language defined by the SPARQL grammar; or,");
            } catch (RepositoryException e) {
                e.printStackTrace();
                getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, "Query Not Executed");
            } finally {

                connection.close();
            }
        }
    }
}
