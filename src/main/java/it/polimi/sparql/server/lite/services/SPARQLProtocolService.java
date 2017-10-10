package it.polimi.sparql.server.lite.services;

import it.polimi.sparql.server.lite.SPARQLServerLite;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.query.resultio.sparqljson.SPARQLResultsJSONWriter;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.WriterConfig;
import org.eclipse.rdf4j.rio.helpers.JSONLDMode;
import org.eclipse.rdf4j.rio.helpers.JSONLDSettings;
import org.eclipse.rdf4j.rio.jsonld.JSONLDWriter;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.resource.ServerResource;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import static org.apache.jena.sparql.vocabulary.VocabTestQuery.query;

/**
 * Created by riccardo on 10/09/2017.
 */
public abstract class SPARQLProtocolService extends ServerResource {

    private static final Pattern SELECT_PATTERN = Pattern.compile("(.*)\\s*SELECT\\s*(.*)", Pattern.CASE_INSENSITIVE);
    private static final Pattern ASK_PATTERN = Pattern.compile("(.*)\\s*ASK\\s+(.*)", Pattern.CASE_INSENSITIVE);
    private static final Pattern CONSTRUCT_PATTERN = Pattern.compile("(.*)\\s*CONSTRUCT\\s*(.*)", Pattern.CASE_INSENSITIVE);
    private static final Pattern DESCRIBE_PATTERN = Pattern.compile("(.*)\\s*DESCRIBE\\s*(.*)", Pattern.CASE_INSENSITIVE);

    protected String getClientAddress() {
        return getRequest().getClientInfo().getAddress();
    }

    protected String getSPARQLQuery() {
        try {
            return URLDecoder.decode((String) getRequest().getAttributes().get(SPARQLServerLite.QUERY), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected Repository getRepository() {
        return (Repository) getContext().getAttributes().get(SPARQLServerLite.REPOSITORY);
    }

    protected Namespace[] getNamespaces() {
        return (Namespace[]) getContext().getAttributes().get(SPARQLServerLite.NAMESPACES);
    }

    protected String getBaseUri() {
        return (String) getContext().getAttributes().get(SPARQLServerLite.BASEURI);
    }

    public TupleQueryResultHandler getTupleFormatter(String accept, ByteArrayOutputStream bao) {


        return new SPARQLResultsJSONWriter(bao);

    }

    public RDFHandler getGraphFormatter(String accept, ByteArrayOutputStream bao, Namespace... namespaces) {

        WriterConfig config = new WriterConfig();
        config.set(JSONLDSettings.JSONLD_MODE, JSONLDMode.COMPACT);
        JSONLDWriter handler = new JSONLDWriter(bao);
        handler.setWriterConfig(config);

        if (namespaces != null)
            for (Namespace ns : namespaces) {
                handler.handleNamespace(ns.getPrefix(), ns.getName());
            }

        return handler;

    }

    private enum TupleResultsFormats {
        JSON("json"), TSV("tsv"), CSV("csv"), RDF_XML("rdf/xml");

        private String name;

        private TupleResultsFormats(String s) {
            this.name = s;
        }
    }

    private enum GraphResultsFormats {
        JSONLD("json"), RDF_XML("rdf/xml"), TRIG("trig"), TURTLE("ttl"), N3("n3");

        private String name;

        private GraphResultsFormats(String s) {
            this.name = s;
        }
    }

    protected void executeQuery(Query q, Namespace... namespaces) throws UnsupportedEncodingException, MalformedQueryException, RepositoryException {
        System.out.println();

        MediaType mediaType = getRequest().getEntity().getMediaType();
        String accept = getRequest().getHeaders().getFirst("Accept").getValue();

        ByteArrayOutputStream bao = new ByteArrayOutputStream();


        if (q instanceof TupleQuery) {
            TupleQuery selectQuery = (TupleQuery) q;
            selectQuery.evaluate(getTupleFormatter(accept, bao));
            mediaType = MediaType.APPLICATION_SPARQL_RESULTS_JSON;

        } else if (q instanceof GraphQuery) {
            GraphQuery constructQuery = (GraphQuery) q;
            constructQuery.evaluate(getGraphFormatter(accept, bao, namespaces));
            mediaType = MediaType.APPLICATION_SPARQL_RESULTS_JSON;
        } else if (q instanceof BooleanQuery) {
            BooleanQuery askQuery = (BooleanQuery) q;
        } else
            throw new RuntimeException("NO SUCH QUERY TYPE [" + query + "]");

        getResponse().setStatus(Status.SUCCESS_OK, "Query  succesfully evaluated");
        getResponse().setEntity(bao.toString("UTF-8"), mediaType);

    }

}
