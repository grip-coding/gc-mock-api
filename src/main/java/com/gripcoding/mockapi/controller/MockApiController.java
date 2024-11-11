package com.gripcoding.mockapi.controller;

import com.gripcoding.mockapi.config.AppConfig;
import com.gripcoding.mockapi.service.MockApiService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MockApiController {

    private final MockApiService service;
    private final AppConfig config;
    private final Map<String, String> uriToPathMap = new HashMap<>();

    @Autowired
    public MockApiController(MockApiService service, AppConfig config) {
        this.service = service;
        this.config = config;
    }

    // Initialize mappings of URI paths to JSON response file paths at startup
    @PostConstruct
    public void initializeEndpoints() {
        config.getEndpoints().forEach(endpoint -> uriToPathMap.put(endpoint.getUriPath(), endpoint.getJsonFilePath()));
    }

    // Handle all incoming GET requests
    @GetMapping(value = "/**")
    public ResponseEntity getDummyGetApiResponse(HttpServletRequest request,
                                              @RequestParam Map<String, String> queryParams) {
        return getResponseEntity(request, queryParams);
    }

    // Handle all incoming POST requests
    @PostMapping(value = "/**")
    public ResponseEntity getDummyPostApiResponse(HttpServletRequest request,
                                                  @RequestParam Map<String, String> queryParams,
                                                  @RequestBody Object requestBody) {
        return getResponseEntity(request, queryParams);
    }

    private ResponseEntity<?> getResponseEntity(HttpServletRequest request, Map<String, String> queryParams) {
        String requestUri = request.getRequestURI();

        return service.getEndpointConfig(requestUri)
                .map(endpointConfig -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.parseMediaType("application/json"));
                    try {
                        Object responseContent = service.getDummyResponse(endpointConfig, queryParams);

                        return ResponseEntity.ok()
                                .headers(headers)
                                .body(responseContent);
                    } catch (Exception e) {
                        return ResponseEntity.internalServerError()
                                .headers(headers)
                                .body("{\"error\": \"Unable to read json file due to " + e.getMessage() + "\"}");
                    }
                })
                .orElse(ResponseEntity.status(404).body("{\"error\": \"No matching path\"}"));
    }
}
