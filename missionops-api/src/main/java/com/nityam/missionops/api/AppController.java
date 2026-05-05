package com.nityam.missionops.api;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AppController {
  private final List<Map<String, Object>> audit = new ArrayList<>();

  @PostMapping("/ask")
  Map<String, Object> ask(@RequestBody Map<String, String> req) {
    String q = req.getOrDefault("question", "");
    Map<String, Object> r = new LinkedHashMap<>();
    r.put("answer", "Mock answer for: " + q);
    r.put("citations", List.of(Map.of("documentName", "ai-governance-policy.md", "chunkId", "chunk-001")));
    r.put("confidence", 0.86);
    r.put("traceId", "trace-demo-001");
    audit.add(Map.of("traceId", "trace-demo-001", "agentName", "RagService", "createdAt", Instant.now().toString()));
    return r;
  }

  @PostMapping("/simulation/events")
  Map<String, Object> triageEvent(@RequestBody Map<String, Object> request) {
    Map<String, Object> response = new LinkedHashMap<>();
    response.put("missionId", "MIS-2026-0501-001");
    response.put("tcpedStage", "TASKING");
    response.put("recommendedActions", List.of(
        "Queue secondary satellite collection",
        "Request ground radar confirmation",
        "Run anomaly classification model",
        "Generate initial analyst briefing"));
    response.put("riskLevel", "MEDIUM");
    response.put("agentsActivated", List.of("SatelliteSensorAgent", "TaskingAgent", "RiskAgent", "RagBriefingAgent"));
    response.put("initialBriefing", "An unknown orbital object was detected with medium confidence. Recommended next collection action is secondary satellite tasking and ground radar confirmation. Relevant policy references: Space Domain Awareness SOP, ISR Collection Priority Guide.");
    response.put("traceId", "trace-isr-demo-001");

    audit.add(Map.of(
        "traceId", "trace-isr-demo-001",
        "agentName", "SyntheticIsrTriage",
        "missionId", "MIS-2026-0501-001",
        "inputSummary", request.getOrDefault("eventType", "UNKNOWN") + " / " + request.getOrDefault("domain", "UNKNOWN"),
        "createdAt", Instant.now().toString()));
    return response;
  }

  @PostMapping("/simulation/scenarios/{name}/run")
  Map<String, Object> run(@PathVariable String name) {
    if (!"UNKNOWN_OBJECT_DETECTED".equals(name)) {
      return Map.of("scenario", name, "status", "NOT_IMPLEMENTED");
    }
    Map<String, Object> r = new LinkedHashMap<>();
    r.put("missionId", "mis-demo-001");
    r.put("currentStage", "DISSEMINATION");
    r.put("riskLevel", "MEDIUM");
    r.put("briefing", "An unknown orbital object was detected with medium confidence. Recommended next actions are secondary satellite collection, ground confirmation, and continued monitoring. Relevant policies were retrieved from Space Domain Awareness SOP and AI Governance Policy.");
    r.put("agentsUsed", List.of("TaskingAgent", "CollectionAgent", "ProcessingAgent", "ExploitationAgent", "RiskAgent", "DisseminationAgent"));
    r.put("citations", List.of(Map.of("documentName", "tcped-workflow-guide.md", "chunkId", "chunk-001")));
    r.put("traceId", "trace-demo-001");
    audit.add(Map.of("traceId", "trace-demo-001", "agentName", "SimulationOrchestrator", "missionId", "mis-demo-001", "createdAt", Instant.now().toString()));
    return r;
  }

  @GetMapping("/audit/traces/{traceId}")
  List<Map<String, Object>> trace(@PathVariable String traceId) {
    return audit.stream().filter(a -> traceId.equals(a.get("traceId"))).toList();
  }

  @GetMapping("/health/ai")
  Map<String, String> ai() {
    return Map.of("status", "UP", "provider", "mock");
  }

  @GetMapping("/simulation/scenarios")
  List<String> scenarios() {
    return List.of("UNKNOWN_OBJECT_DETECTED", "DEGRADED_SENSOR_NETWORK", "MULTI_DOMAIN_EVENT_CORRELATION", "URGENT_COLLECTION_REQUEST");
  }

  @PostMapping("/documents/upload")
  ResponseEntity<Map<String, String>> upload() {
    return ResponseEntity.ok(Map.of("documentId", UUID.randomUUID().toString(), "status", "INGESTED"));
  }
}
