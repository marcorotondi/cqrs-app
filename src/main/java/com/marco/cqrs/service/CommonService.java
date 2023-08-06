package com.marco.cqrs.service;

import com.marco.cqrs.command.CloneCommand;
import com.marco.cqrs.entity.ComputationEntity;
import com.marco.cqrs.events.CompletedEvent;
import com.marco.cqrs.events.InitEvent;
import com.marco.cqrs.repository.ComputationRepository;
import com.marco.cqrs.type.Operation;
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
        repository.addComputation(ComputationEntity.of(event));

        repository.findById(event.id()).ifPresent(computationEntity -> {
            computationEntity.setOperation(event.operation());
            repository.updateComputation(computationEntity);

            final var send = commandGateway.send(new CloneCommand(
                    event.id(),
                    event.computationType(),
                    Operation.CLONE_START,
                    event.index()
            ));
            log.info("send command: {}", send);
        });
    }

    @EventHandler
    public void onCompleteEvent(CompletedEvent event) {
        log.info("on event: {}", event);

        repository.findById(event.id())
                .ifPresentOrElse(computationEntity -> {
                    computationEntity.setOperation(event.operation());
                    repository.updateComputation(computationEntity);

                    log.info("Flow completed: {}", event);
                    log.info("Computation Result: {}", computationEntity);
                }, () -> log.error("Computation with id {} already present", event.id()));
    }
}