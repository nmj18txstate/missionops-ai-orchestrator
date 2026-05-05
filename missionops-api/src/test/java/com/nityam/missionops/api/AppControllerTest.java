package com.nityam.missionops.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AppControllerTest {
  private final AppController c = new AppController();

  @Test
  void unknownObjectScenario() {
    Map<String, Object> out = c.run("UNKNOWN_OBJECT_DETECTED");
    assertThat(out.get("missionId")).isEqualTo("mis-demo-001");
  }

  @Test
  void askReturnsTrace() {
    Map<String, Object> out = c.ask(Map.of("question", "test"));
    assertThat(out.get("traceId")).isEqualTo("trace-demo-001");
  }

  @Test
  void syntheticIsrEventReturnsExpectedPayload() {
    Map<String, Object> out = c.triageEvent(Map.of("eventType", "UNKNOWN_OBJECT_DETECTED", "domain", "SPACE"));
    assertThat(out.get("missionId")).isEqualTo("MIS-2026-0501-001");
    assertThat(out.get("tcpedStage")).isEqualTo("TASKING");
    assertThat(out.get("traceId")).isEqualTo("trace-isr-demo-001");
  }

  @Test
  void isrTraceIsRecorded() {
    c.triageEvent(Map.of("eventType", "UNKNOWN_OBJECT_DETECTED", "domain", "SPACE"));
    List<Map<String, Object>> traces = c.trace("trace-isr-demo-001");
    assertThat(traces).isNotEmpty();
    assertThat(traces.get(0).get("agentName")).isEqualTo("SyntheticIsrTriage");
  }
}
