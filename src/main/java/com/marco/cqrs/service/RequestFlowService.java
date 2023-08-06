package com.marco.cqrs.service;


import com.marco.cqrs.command.PowerFlowCommand;
import com.marco.cqrs.component.EndpointComponent;
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
public class RequestFlowService {
    private static final Logger log = LoggerFactory.getLogger(RequestFlowService.class);

    private final ComputationRepository repository;

    private final EndpointComponent endPoint;

    private final CommandGateway commandGateway;

    public RequestFlowService(ComputationRepository repository,
                              EndpointComponent endPoint,
                              CommandGateway commandGateway) {
        this.repository = repository;
        this.endPoint = endPoint;
        this.commandGateway = commandGateway;
    }

    @EventHandler
    public void onRequestFlowEvent(CloneEvent event) {
        log.info("Event: {}", event);

        repository.findById(event.id()).ifPresentOrElse(computationEntity -> {
            switch (event.operation()) {
                case REQUEST_START -> {
                    computationEntity.setOperation(event.operation());
                    repository.updateComputation(computationEntity);

                    // call API endpoint to start power flow
                    endPoint.callFlexibilityWorker(event);
                }
                case REQUEST_END -> {
                    computationEntity.setOperation(event.operation());
                    repository.updateComputation(computationEntity);

                    commandGateway.send(new PowerFlowCommand(
                            event.id(),
                            event.computationType(),
                            Operation.POWER_FLOW,
                            event.index() + 1,
                            Boolean.FALSE
                    ));
                }
                default -> throw new InvalidFlowException(String.format("Operation %s not permitted on Clone", event.operation()));
            }
        }, () -> log.error("Computation with id: {} not present", event.id()));
    }
}