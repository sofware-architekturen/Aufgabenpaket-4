package de.hskl.itanalyst.buchservice.controller.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebpageController {
    @GetMapping("/")
    /**
     * Leitet jede Anfrage von der Hauptseite auf die API-Dokumentation um.
     *
     * / -> /swagger-ui.html
     */
    public ResponseEntity indexPage() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "swagger-ui.html");
        return new ResponseEntity(headers, HttpStatus.FOUND);
    }
}
