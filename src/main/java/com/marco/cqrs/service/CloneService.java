package com.marco.cqrs.service;

import com.marco.cqrs.command.PowerFlowCommand;
import com.marco.cqrs.component.EndpointComponent;
import com.marco.cqrs.entity.ComputationEntity;
import com.marco.cqrs.events.CloneEvent;
import com.marco.cqrs.exception.InvalidFlowException;
import com.marco.cqrs.repository.ComputationRepository;
import com.marco.cqrs.type.Operation;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CloneService {

    private static final Logger log = LoggerFactory.getLogger(CloneService.class);

    private final ComputationRepository repository;

    private final EndpointComponent endPoint;

    private final CommandGateway commandGateway;

    public CloneService(ComputationRepository repository,
                        EndpointComponent endPoint,
                        CommandGateway commandGateway) {
        this.repository = repository;
        this.endPoint = endPoint;
        this.commandGateway = commandGateway;
    }

    @EventHandler
    public void onCloneEvent(CloneEvent event) {
        log.info("Event: {}", event);

        repository.findById(event.computationId()).ifPresentOrElse(computationEntity -> {
            switch (event.operation()) {
                case CLONE_START -> {
                    repository.updateComputation(ComputationEntity.of(event));

                    // call API endpoint to start clone
                    endPoint.callClone(event);
                }
                case CLONE_END -> {
                    repository.updateComputation(ComputationEntity.of(event));

                    commandGateway.send(new PowerFlowCommand(
                            event.id(),
                            event.computationId(),
                            event.computationType(),
                            Operation.POWER_FLOW_START,
                            event.index(),
                            Boolean.FALSE
                    ));
                }
                default -> throw new InvalidFlowException(String.format("Operation %s not permitted on Clone", event.operation()));
            }
        }, () -> log.error("Computation with id: {} not present", event.id()));
    }
}