package it.polimi.semweb.sparql.sse.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class SPARQLRequest {

    private String query;
    private Map<String, String> properties = new HashMap<>();

    public SPARQLRequest() {
    }

    public SPARQLRequest(String body) {
        try {
            String[] split = body.split("&");
            for (String s : split) {
                String[] split1 = s.split("=");
                if ("query".equals(split1[0])) {
                    query = URLDecoder.decode(split1[1], "UTF-8");
                } else add(split1[0], split1[1]);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @JsonAnySetter
    public void add(String key, String value) {
        properties.put(key, value);
    }

    public String get(String k) {
        return properties.get(k);
    }

    public String query() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean contains(String k) {
        return properties.containsKey(k);
    }
}

