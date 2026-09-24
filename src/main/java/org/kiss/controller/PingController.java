package org.kiss.controller;

import java.time.OffsetDateTime;

import org.kiss.api.PingApi;
import org.kiss.api.model.PingResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController implements PingApi {

    @Override
    public ResponseEntity<PingResponse> ping() {
        return ResponseEntity.ok(new PingResponse().status("UP").timestamp(OffsetDateTime.now()));
    }
}
