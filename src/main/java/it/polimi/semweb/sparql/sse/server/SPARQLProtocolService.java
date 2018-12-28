package it.polimi.semweb.sparql.sse.server;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.query.resultio.sparqljson.SPARQLResultsJSONWriter;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.WriterConfig;
import org.eclipse.rdf4j.rio.helpers.JSONLDMode;
import org.eclipse.rdf4j.rio.helpers.JSONLDSettings;
import org.eclipse.rdf4j.rio.jsonld.JSONLDWriter;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.apache.jena.sparql.vocabulary.VocabTestQuery.query;

/**
 * Created by riccardo on 10/09/2017.
 */

public abstract class SPARQLProtocolService {

    protected Namespace[] namespaces;
    protected Repository repository;
    protected RepositoryConfig prop;

    private static final Pattern SELECT_PATTERN = Pattern.compile("(.*)\\s*SELECT\\s*(.*)", Pattern.CASE_INSENSITIVE);
    private static final Pattern ASK_PATTERN = Pattern.compile("(.*)\\s*ASK\\s+(.*)", Pattern.CASE_INSENSITIVE);
    private static final Pattern CONSTRUCT_PATTERN = Pattern.compile("(.*)\\s*CONSTRUCT\\s*(.*)", Pattern.CASE_INSENSITIVE);
    private static final Pattern DESCRIBE_PATTERN = Pattern.compile("(.*)\\s*DESCRIBE\\s*(.*)", Pattern.CASE_INSENSITIVE);

    public static final String APP_SPARQL_JSON_RESULTS = "application/sparql-results+json";
    public static final String APP_JSON = "application/json";

    public static final String QUERY = "query";
    public static final String DEF_GRAPH_URI = "default-graph-uri";
    public static final String NMD_GRAPH_URI = "named-graph-uri";
    public static final int CLIENT_ERROR_BAD_REQUEST = 400;
    public static final int SUCCESS_OK = 200;

    public SPARQLProtocolService(Repository repository, PropertiesConfiguration p) throws ConfigurationException {
        this.repository = repository;
        this.namespaces = getNamespaces(repository);
        this.prop = new RepositoryConfig(p);
    }

    protected Repository getRepository() {
        return repository;
    }

    protected Namespace[] getNamespaces() {
        return namespaces;
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

        TupleResultsFormats(String s) {
            this.name = s;
        }
    }

    private enum GraphResultsFormats {
        JSONLD("json"), RDF_XML("rdf/xml"), TRIG("trig"), TURTLE("ttl"), N3("n3");

        private String name;

        GraphResultsFormats(String s) {
            this.name = s;
        }
    }

    protected String executeQuery(String return_type, Query q, Namespace... namespaces) throws MalformedQueryException, RepositoryException, UnsupportedEncodingException {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();

        if (q instanceof TupleQuery) {
            TupleQuery selectQuery = (TupleQuery) q;
            selectQuery.evaluate(getTupleFormatter(return_type, bao));
        } else if (q instanceof GraphQuery) {
                GraphQuery constructQuery = (GraphQuery) q;
            constructQuery.evaluate(getGraphFormatter(return_type, bao, namespaces));
        } else if (q instanceof BooleanQuery) {
            BooleanQuery askQuery = (BooleanQuery) q;
            boolean evaluate = askQuery.evaluate();
            return Boolean.toString(evaluate);
        } else
            throw new RuntimeException("NO SUCH QUERY TYPE [" + query + "]");

        return bao.toString("UTF-8");
    }

    protected static class RepositoryConfig extends PropertiesConfiguration {

        public RepositoryConfig(PropertiesConfiguration configuration) throws ConfigurationException {
            super(configuration.getFile());
        }

        public int getPort() {
            return getInt("server.port", 8080);
        }

        public String getGetEndpointPath() {
            return "/sparql/:query";
        }

        public String getPostEndpointPath() {
            return "/sparql";
        }

        public String getServerName() {
            return "/" + getString("server.name");
        }
    }

    protected Namespace[] getNamespaces(Repository repository) {
        RepositoryConnection connection = repository.getConnection();
        RepositoryResult<Namespace> namespaces = connection.getNamespaces();
        List<Namespace> ns = new ArrayList<>();
        while (namespaces.hasNext()) {
            Namespace next = namespaces.next();
            System.out.println(next.getPrefix() + " : " + next.getName());
            ns.add(next);
        }
        connection.close();
        return ns.toArray(new Namespace[ns.size()]);
    }
}
