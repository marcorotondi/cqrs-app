package com.marco.cqrs.controller;


import com.marco.cqrs.command.InitCommand;
import com.marco.cqrs.type.ComputationType;
import com.marco.cqrs.type.Operation;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FlowController {

    private final CommandGateway commandGateway;

    public FlowController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping("/initFlow")
    public Mono<ResponseEntity<String>> startFlow() {
        commandGateway.send(new InitCommand(
                UUID.randomUUID().toString(),
                "DFR-20230806",
                ComputationType.FLEXIBILITY,
                Operation.INIT,
                0
        ));

        return Mono.just(ResponseEntity.ok("START"));
    }
}