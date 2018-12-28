package it.polimi.deib.semweb.sparql.see;

import java.util.regex.Pattern;

public class PatternTest {

    private static final Pattern SELECT_PATTERN = Pattern.compile("(.*)\\s+SELECT\\s+(.*)", Pattern.CASE_INSENSITIVE);
    private static final Pattern ASK_PATTERN = Pattern.compile("(.*)\\s+ASK\\s+(.*)", Pattern.CASE_INSENSITIVE);
    private static final Pattern CONSTRUCT_PATTERN = Pattern.compile("(.*)\\s+CONSTRUCT\\s+(.*)", Pattern.CASE_INSENSITIVE);
    private static final Pattern DESCRIBE_PATTERN = Pattern.compile("(.*)\\s+DESCRIBE\\s+(.*)", Pattern.CASE_INSENSITIVE);

    public static void main(String[] args) {


        String selectq = "PREFIX ex: <http://purl.org/dc/elements/1.1/>\n" +
                "SELECT ?mail WHERE { ?person ex:mail ?mail ; ex:name ?name . }";

        String constructq = "PREFIX ex: <http://purl.org/dc/elements/1.1/>\n" +
                "CONSTRUCT {?s ?p ?o } WHERE {?s ?p ?o } ";

        String askq = "PREFIX dc: <http://purl.org/dc/elements/1.1/> \n" +
                "ASK WHERE { ?book dc:creator \"J.K. Rowling\"}";

        String descrq = "PREFIX books: <http://www.example/book/>\n" +
                "DESCRIBE books:book6";

        if (SELECT_PATTERN.matcher(selectq).matches())
            System.out.println("SELECT QUERY [" + selectq + "]");
        if (SELECT_PATTERN.matcher(constructq).matches())
            System.out.println("ERROR [" + constructq + "]");
        if (SELECT_PATTERN.matcher(askq).matches())
            System.out.println("ERROR [" + askq + "]");
        if (SELECT_PATTERN.matcher(descrq).matches())
            System.out.println("ERROR [" + descrq + "]");

        if (CONSTRUCT_PATTERN.matcher(selectq).matches())
            System.out.println("ERROR  [" + selectq + "]");
        if (CONSTRUCT_PATTERN.matcher(constructq).matches())
            System.out.println("CONSTRUCT QUERY [" + constructq + "]");
        if (CONSTRUCT_PATTERN.matcher(askq).matches())
            System.out.println("ERROR [" + askq + "]");
        if (CONSTRUCT_PATTERN.matcher(descrq).matches())
            System.out.println("ERROR [" + descrq + "]");


    }
}
