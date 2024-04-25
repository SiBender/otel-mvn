package net.bondarik.telemetrytest.controller;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class Controller {

    private final Tracer tracer;

    @Autowired
    Controller(OpenTelemetry openTelemetry) {
        tracer = openTelemetry.getTracer(Controller.class.getName(), "0.1.0");
    }

    @GetMapping("/api/get/external")
    public String getExternalMethod() {
        Span span = tracer.spanBuilder("getExternalMethod").startSpan();

        // Make the span the current span
        try (Scope scope = span.makeCurrent()) {


            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl = "http://localhost:8081/api/get/long";
            ResponseEntity<String> response = restTemplate.getForEntity(fooResourceUrl, String.class);
            return response.getBody();

        } catch(Throwable t) {
            span.recordException(t);
            throw t;
        } finally {
            span.end();
        }
    }
}

