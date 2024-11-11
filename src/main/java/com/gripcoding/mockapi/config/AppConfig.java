package com.gripcoding.mockapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "mockapi")
public class AppConfig {

    private List<Endpoint> endpoints;

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }

    public static class Endpoint {
        private String uriPath;
        private String jsonFilePath;
        private Map<String, Map<String, String>> queryParams;  // New field for query parameters

        public String getUriPath() {
            return uriPath;
        }

        public void setUriPath(String uriPath) {
            this.uriPath = uriPath;
        }

        public String getJsonFilePath() {
            return jsonFilePath;
        }

        public void setJsonFilePath(String jsonFilePath) {
            this.jsonFilePath = jsonFilePath;
        }

        public Map<String, Map<String, String>> getQueryParams() {
            return queryParams;
        }

        public void setQueryParams(Map<String, Map<String, String>> queryParams) {
            this.queryParams = queryParams;
        }
    }
}
