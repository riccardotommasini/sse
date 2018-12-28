package it.polimi.semweb.sparql.sse;

import it.polimi.semweb.sparql.sse.server.SPARQLServerLite;
import it.unibz.inf.ontop.injection.OntopSQLOWLAPIConfiguration;
import it.unibz.inf.ontop.rdf4j.repository.OntopRepository;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by riccardo on 10/09/2017.
 */
public class Console {

    private static final Logger logger = LoggerFactory.getLogger(Console.class);

    public static void main(String[] args) throws Exception {
        start(args, 0);
    }

    private static void start(String[] args, int tries) throws Exception {
        Repository repo;
        ConsoleProperties p;
        if (args.length < 1) {
            throw new RuntimeException("Wrong Number of Arguments");
        }

        try {
            if (args[0].equals("--virtual") || args[0].equals("-v")) {
                repo = getVirtualRepository(p = new ConsoleProperties(args[1]));
            } else {
                repo = getStandardRepository(p = new ConsoleProperties(args[0]));
            }

            new SPARQLServerLite(repo, p).initialize();

            logger.info("Started");


        } catch (RepositoryException e) {
            Thread.sleep(15000);
            if (tries < 3) {
                start(args, tries + 1);
            }
        }
    }

    private static Repository getStandardRepository(ConsoleProperties p) throws IOException, RepositoryException {
        return getStandardRepository(p.getFileLocation(), p.getBaseURL(), p.getNamedGraph(), p.getFileFormat());
    }

    private static Repository getVirtualRepository(ConsoleProperties p) throws RepositoryException {
        return getVirtualRepository(p.getOntopMappings(), p.getOntopOntology(), p.getOntopProperties());

    }

    private static Repository getStandardRepository(String data, String base, String ng, RDFFormat format) throws IOException, RepositoryException {
        Repository repo = new SailRepository(new MemoryStore());
        repo.initialize();
        RepositoryConnection connection = repo.getConnection();
        ValueFactory factory = repo.getValueFactory();
        if ("def".equals(ng))
            connection.add(new File(data), base, format, factory.createIRI(ng));
        else
            connection.add(new File(data), base, format);
        return repo;
    }

    private static Repository getVirtualRepository(String mappings, String ontology, String properties) throws RepositoryException {
        logger.info(mappings);
        logger.info(ontology);
        logger.info(properties);

        OntopRepository repository = OntopRepository.defaultRepository(OntopSQLOWLAPIConfiguration.defaultBuilder()
                .nativeOntopMappingFile(mappings)
                .ontologyFile(ontology)
                .propertyFile(properties)
                .build());

        repository.initialize();

        return repository;

    }

    private static class ConsoleProperties extends PropertiesConfiguration {

        private static final String file_format = "file.format";
        private static final String file_location = "file.location";
        private static final String base_url = "base.url";
        private static final String named_graph = "named.graph";

        private static final String ONTOP_PROPERTIES = "ontop.properties";
        private static final String ONTOP_ONTO = "ontop.owl";
        private static final String ONTOP_MAPPINGS = "ontop.obda";


        public ConsoleProperties(String fileName) throws ConfigurationException {
            super(fileName);
        }

        public ConsoleProperties(File file) throws ConfigurationException {
            super(file);
        }

        public ConsoleProperties(URL url) throws ConfigurationException {
            super(url);
        }


        private static RDFFormat getFileFormat(String arg) {

            if (RDFFormat.TURTLE.getFileExtensions().contains(arg))
                return RDFFormat.TURTLE;
            if (RDFFormat.JSONLD.getFileExtensions().contains(arg))
                return RDFFormat.JSONLD;
            if (RDFFormat.N3.getFileExtensions().contains(arg))
                return RDFFormat.N3;
            if (RDFFormat.NTRIPLES.getFileExtensions().contains(arg))
                return RDFFormat.NTRIPLES;
            if (RDFFormat.NQUADS.getFileExtensions().contains(arg))
                return RDFFormat.NQUADS;
            if (RDFFormat.TRIX.getFileExtensions().contains(arg))
                return RDFFormat.TRIX;
            if (RDFFormat.TRIG.getFileExtensions().contains(arg))
                return RDFFormat.TRIG;
            if (RDFFormat.RDFXML.getFileExtensions().contains(arg))
                return RDFFormat.RDFXML;
            if (RDFFormat.RDFA.getFileExtensions().contains(arg))
                return RDFFormat.RDFA;

            return RDFFormat.TURTLE;
        }

        public RDFFormat getFileFormat() {
            return getFileFormat(getString(file_format));
        }

        public String getFileLocation() {
            return getString(file_location);
        }

        public String getBaseURL() {
            return getString(base_url);
        }

        public String getNamedGraph() {
            return getString(named_graph);
        }

        public String getOntopMappings() {
            return getString(ONTOP_MAPPINGS);
        }

        public String getOntopProperties() {
            return getString(ONTOP_PROPERTIES).equals("this") ? getFileName() : getString(ONTOP_PROPERTIES);
        }

        public String getOntopOntology() {
            return getString(ONTOP_ONTO);
        }
    }
}
