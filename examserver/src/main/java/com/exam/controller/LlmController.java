package com.exam.controller;

import com.exam.model.QuotationRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
@RequestMapping("/llm")
@CrossOrigin("*")
public class LlmController {

    @PostMapping("/")
    public ResponseEntity<String> askModel(@RequestBody QuotationRequest request) {
        String answer = callLlmApi(request.getQuestion());
        return ResponseEntity.ok(answer);
    }

    private String callLlmApi(String question) {

        // ✅ FINAL CORRECT URL (Router API)
        String apiUrl = "https://router.huggingface.co/hf-inference/models/google/flan-t5-base";

        System.out.println("API URL: " + apiUrl);

        if (apiUrl == null || apiUrl.isEmpty()) {
            return "Error: API URL is null";
        }

        // ⚠️ Put your Hugging Face token here
        String apiKey = "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";

        try {
            HttpClient client = HttpClient.newHttpClient();

            // ✅ Better prompt
            String requestBody = "{ \"inputs\": \"Answer this question clearly: " + question + "\" }";

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response =
                    client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            String body = response.body();
            System.out.println("API Response: " + body);

            // ✅ SAFE HANDLING (important fix)
            if (!body.startsWith("{") && !body.startsWith("[")) {
                return body; // handles "Not Found" or plain text
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(body);

            // ✅ Handle valid response
            if (root.isArray() && root.size() > 0) {
                JsonNode node = root.get(0);

                if (node.has("generated_text")) {
                    return node.get("generated_text").asText();
                }

                if (node.has("summary_text")) {
                    return node.get("summary_text").asText();
                }
            }

            return body;

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}