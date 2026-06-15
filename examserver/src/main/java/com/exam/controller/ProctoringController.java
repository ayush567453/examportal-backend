package com.exam.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/proctoring")
public class ProctoringController {

    @Value("${proctoring.storage.path:./proctoring-data}")
    private String storagePath;

    @Value("${huggingface.api.key:}")
    private String hfApiKey;

    private static final String BLIP_URL =
            "https://api-inference.huggingface.co/models/Salesforce/blip-image-captioning-base";

    // ── Save raw snapshot ─────────────────────────────────────────────────────
    @PostMapping("/snapshot")
    public ResponseEntity<Map<String, String>> saveSnapshot(@RequestBody Map<String, String> body) {
        String username  = body.getOrDefault("username", "unknown");
        String quizId   = body.getOrDefault("quizId", "0");
        String imageData = body.getOrDefault("imageData", "");

        Map<String, String> response = new HashMap<>();
        try {
            byte[] imageBytes = decodeBase64Image(imageData);
            saveFile(username, quizId, "snapshot_" + System.currentTimeMillis() + ".jpg", imageBytes);
            response.put("status", "saved");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ── AI Proctoring: analyze snapshot with BLIP image captioning ────────────
    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeSnapshot(@RequestBody Map<String, String> body) {
        String username  = body.getOrDefault("username", "unknown");
        String quizId   = body.getOrDefault("quizId", "0");
        String imageData = body.getOrDefault("imageData", "");

        Map<String, Object> result = new HashMap<>();
        try {
            byte[] imageBytes = decodeBase64Image(imageData);

            // Save snapshot for record
            saveFile(username, quizId, "snapshot_" + System.currentTimeMillis() + ".jpg", imageBytes);

            // Get image caption from BLIP
            String caption = callBlipApi(imageBytes);
            System.out.println("[Proctoring] caption for " + username + ": " + caption);

            // Detect suspicious behaviour from caption
            String alertMessage = analyzeCaption(caption);
            boolean isAlert = !alertMessage.isEmpty();

            result.put("alert", isAlert);
            result.put("message", isAlert ? alertMessage : "OK");
            result.put("caption", caption);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            // Don't break the exam on analysis failure — just return no-alert
            result.put("alert", false);
            result.put("message", "");
            result.put("caption", "");
            return ResponseEntity.ok(result);
        }
    }

    // ── Screen recording upload ───────────────────────────────────────────────
    @PostMapping("/screen-recording")
    public ResponseEntity<Map<String, String>> saveScreenRecording(
            @RequestParam("file") MultipartFile file,
            @RequestParam("username") String username,
            @RequestParam("quizId") String quizId) {

        Map<String, String> response = new HashMap<>();
        try {
            String dir = buildDir(username, quizId);
            Files.createDirectories(Paths.get(dir));
            String filename = dir + File.separator + "recording_" + System.currentTimeMillis() + ".webm";
            file.transferTo(new File(filename));
            response.put("status", "saved");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private String callBlipApi(byte[] imageBytes) {
        if (hfApiKey == null || hfApiKey.isBlank()) {
            System.err.println("[Proctoring] huggingface.api.key not set — skipping BLIP analysis");
            return "";
        }
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(30))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BLIP_URL))
                    .header("Authorization", "Bearer " + hfApiKey)
                    .header("Content-Type", "application/octet-stream")
                    .timeout(Duration.ofSeconds(60))
                    .POST(HttpRequest.BodyPublishers.ofByteArray(imageBytes))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            System.out.println("[Proctoring] HTTP status: " + response.statusCode());
            System.out.println("[Proctoring] BLIP response: " + responseBody);

            // Model still loading on HuggingFace — retry after estimated time
            if (responseBody.contains("loading") || responseBody.contains("estimated_time")) {
                System.out.println("[Proctoring] Model is loading, will retry next cycle");
                return "";
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            if (root.isArray() && root.size() > 0 && root.get(0).has("generated_text")) {
                return root.get(0).get("generated_text").asText();
            }
            // HF returned an error object
            if (root.has("error")) {
                System.err.println("[Proctoring] HF error: " + root.get("error").asText());
            }
            return "";
        } catch (Exception e) {
            System.err.println("[Proctoring] BLIP API error: "
                    + e.getClass().getName() + " — " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    private String analyzeCaption(String caption) {
        if (caption == null || caption.isBlank()) return "";
        String c = caption.toLowerCase();

        boolean hasPerson = c.contains("person") || c.contains("man") || c.contains("woman")
                || c.contains("student") || c.contains("face") || c.contains("people")
                || c.contains("boy") || c.contains("girl");

        if (!hasPerson) {
            return "No face detected — please sit in front of the camera";
        }
        if (c.contains("two people") || c.contains("two men") || c.contains("two women")
                || c.contains("group") || c.contains("crowd") || c.contains("multiple")) {
            return "Multiple people detected — only you should be present";
        }
        if (c.contains("phone") || c.contains("mobile") || c.contains("book")
                || c.contains("paper") || c.contains("notes") || c.contains("notebook")) {
            return "Suspicious material detected — remove reference materials";
        }
        if (c.contains("looking away") || c.contains("looking down") || c.contains("looking sideways")) {
            return "Please keep your eyes on the screen";
        }
        return "";
    }

    private byte[] decodeBase64Image(String imageData) {
        String base64 = imageData.contains(",") ? imageData.split(",")[1] : imageData;
        return Base64.getDecoder().decode(base64);
    }

    private void saveFile(String username, String quizId, String filename, byte[] data) throws Exception {
        String dir = buildDir(username, quizId);
        Files.createDirectories(Paths.get(dir));
        try (FileOutputStream fos = new FileOutputStream(dir + File.separator + filename)) {
            fos.write(data);
        }
    }

    private String buildDir(String username, String quizId) {
        return storagePath + File.separator + sanitize(username)
                + File.separator + "quiz_" + sanitize(quizId);
    }

    private String sanitize(String input) {
        return input.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }
}
