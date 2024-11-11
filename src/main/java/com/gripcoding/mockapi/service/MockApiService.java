package com.gripcoding.mockapi.service;

import com.gripcoding.mockapi.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

@Service
public class MockApiService {

    private final AppConfig config;

    @Autowired
    public MockApiService(AppConfig config) {
        this.config = config;
    }

    public Optional<AppConfig.Endpoint> getEndpointConfig(String uriPath) {
        return config.getEndpoints().stream()
                .filter(endpoint -> endpoint.getUriPath().equals(uriPath))
                .findFirst();
    }

    public String getDummyResponse(AppConfig.Endpoint endpointConfig, Map<String, String> queryParams) throws IOException, JSONException {
        String filePath = endpointConfig.getJsonFilePath();

        // Check if there are specific files mapped for query parameters
        if (endpointConfig.getQueryParams() != null) {
            for (Map.Entry<String, Map<String, String>> paramEntry : endpointConfig.getQueryParams().entrySet()) {
                String paramName = paramEntry.getKey();
                String paramValue = queryParams.get(paramName);

                if (paramValue != null && paramEntry.getValue().containsKey(paramValue)) {
                    // Found a matching file for the query parameter value
                    filePath = paramEntry.getValue().get(paramValue);
                }
            }
        }

        return readFileContent(filePath);
    }

    private static String readFileContent(String filePath) throws IOException, JSONException {
        if (filePath == null) {
            throw new IllegalArgumentException("filePath is null or not provided");
        }
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new FileNotFoundException("file doesn't exist. ["+ filePath +"]");
        }
        String dummyResponse = new String(Files.readAllBytes(path));
        if (dummyResponse.trim().startsWith("{")) {
            JSONObject jsonObject = new JSONObject(dummyResponse);
        } else if (dummyResponse.trim().startsWith("[")) {
            JSONArray jsonArray = new JSONArray(dummyResponse);
        }
        return dummyResponse;
    }
}
