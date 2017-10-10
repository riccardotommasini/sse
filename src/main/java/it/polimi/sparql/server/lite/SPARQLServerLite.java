package it.polimi.sparql.server.lite;

import it.polimi.sparql.server.lite.services.GetQueryService;
import it.polimi.sparql.server.lite.services.PostQueryService;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.restlet.routing.Template;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by riccardo on 10/09/2017.
 */
public class SPARQLServerLite {

    public static final String REPOSITORY = "repository";
    public static final String QUERY = "query";
    public static final String DEF_GRAPH_URI = "default-graph-uri";
    public static final String NMD_GRAPH_URI = "named-graph-uri";
    public static final String BASEURI = "base_uri";
    public static final String NAMESPACES = "namespaces";
    private final Repository repository;
    private final RepositoryConfig prop;

    public SPARQLServerLite(Repository repo, PropertiesConfiguration p) throws ConfigurationException {
        this.prop = new RepositoryConfig(p);
        this.repository = repo;
    }

    public void initialize() throws Exception {

        Component server = new Component();

        //TODO expose port in configuration
        server.getServers().add(Protocol.HTTP, prop.getPort());
        server.getClients().add(Protocol.FILE);

        server.getDefaultHost().attach("", new SPARQLEndpoint(repository));
        server.start();

        System.out.println("it.polimi.rsp.sparql.server.lite.SPARQLServerLite Started");
    }


    private class SPARQLEndpoint extends Application {

        private final Repository repository;

        private SPARQLEndpoint(Repository repository) {
            this.repository = repository;
        }

        public Restlet createInboundRoot() {

            getContext().getAttributes().put(SPARQLServerLite.REPOSITORY, repository);
            getContext().getAttributes().put(SPARQLServerLite.NAMESPACES, getNamespaces());

            Router router = new Router(getContext());
            router.setDefaultMatchingMode(Template.MODE_EQUALS);
            router.attach(prop.getGetEndpointPath(), GetQueryService.class);
            router.attach(prop.getPostEndpointPath(), PostQueryService.class);

            System.out.println(prop.getGetEndpointPath());
            System.out.println(prop.getPostEndpointPath());
            return router;
        }

        private Namespace[] getNamespaces() {
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

    private static class RepositoryConfig extends PropertiesConfiguration {

        public RepositoryConfig(PropertiesConfiguration configuration) throws ConfigurationException {
            super(configuration.getFile());
        }


        public int getPort() {
            return getInt("server.port", 8080);
        }

        public String getGetEndpointPath() {
            return "/" + getServerName() + "/sparql/{query}";
        }

        public String getPostEndpointPath() {
            return "/" + getServerName() + "/sparql";
        }

        public String getServerName() {
            return getString("server.name");
        }
    }
}
