package com.marco.cqrs.service;

import com.marco.cqrs.command.CloneCommand;
import com.marco.cqrs.entity.ComputationEntity;
import com.marco.cqrs.events.InitEvent;
import com.marco.cqrs.repository.ComputationRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CommonService {

    private static final Logger log = LoggerFactory.getLogger(CommonService.class);

    private final ComputationRepository repository;

    private final CommandGateway commandGateway;

    public CommonService(ComputationRepository repository,
                         CommandGateway commandGateway) {
        this.repository = repository;
        this.commandGateway = commandGateway;
    }

    @EventHandler
    public void onInitEvent(InitEvent event) {
        log.info("on event: {}", event);
        var optionalComputation = repository.findById(event.id());

        if (optionalComputation.isEmpty()) {
            repository.addComputation(ComputationEntity.of(event));

            final var send = commandGateway.send(new CloneCommand(
                    event.id(),
                    event.computationType(),
                    event.operation(),
                    event.index()
            ));
            log.info("send command: {}", send);
        }
    }
}