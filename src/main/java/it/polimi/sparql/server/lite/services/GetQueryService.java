package it.polimi.sparql.server.lite.services;

import org.eclipse.rdf4j.query.Query;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Options;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

public class GetQueryService extends SPARQLProtocolService {
    @Options
    public void optionsRequestHandler() {
        Set<Method> allowedMethodes = new HashSet<Method>();
        allowedMethodes.add(Method.GET);
        getResponse().setAccessControlAllowMethods(allowedMethodes);
        getResponse().setAccessControlAllowOrigin(getClientAddress());
    }

    @Get
    public void SPARQLget() {
        RepositoryConnection connection = getRepository().getConnection();

        try {
            Query q = connection.prepareQuery(QueryLanguage.SPARQL, getSPARQLQuery());
            executeQuery(q, getNamespaces());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Query  succesfully evaluated");
        } finally {

            connection.close();
        }
    }
}

