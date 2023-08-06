package com.marco.cqrs.service;


import com.marco.cqrs.command.CompleteCommand;
import com.marco.cqrs.command.PowerFlowCommand;
import com.marco.cqrs.command.RequestCommand;
import com.marco.cqrs.component.EndpointComponent;
import com.marco.cqrs.events.PowerFlowEvent;
import com.marco.cqrs.exception.InvalidFlowException;
import com.marco.cqrs.repository.ComputationRepository;
import com.marco.cqrs.type.Operation;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PowerFlowService {
    private static final Logger log = LoggerFactory.getLogger(PowerFlowService.class);

    private final ComputationRepository repository;

    private final EndpointComponent endPoint;

    private final CommandGateway commandGateway;

    public PowerFlowService(ComputationRepository repository,
                            EndpointComponent endPoint,
                            CommandGateway commandGateway) {
        this.repository = repository;
        this.endPoint = endPoint;
        this.commandGateway = commandGateway;
    }

    @EventHandler
    public void onPowerFlowEvent(PowerFlowEvent event) {
        log.info("Event: {}", event);

        repository.findById(event.computationId()).ifPresentOrElse(computationEntity -> {
            switch (event.operation()) {
                case POWER_FLOW_START -> {
                    computationEntity.setOperation(event.operation());
                    repository.updateComputation(computationEntity);

                    // call API endpoint to start power flow
                    endPoint.callFlexibilityWorker(event);
                }
                case POWER_FLOW -> {
                    if (event.index() < 96) {
                        computationEntity.setOperation(event.operation());
                        computationEntity.setIndex(event.index());
                        repository.updateComputation(computationEntity);

                        // call API endpoint to continue power flow
                        endPoint.callFlexibilityWorker(event);
                    } else {
                        commandGateway.send(new PowerFlowCommand(
                                event.id(),
                                event.computationId(),
                                event.computationType(),
                                Operation.POWER_FLOW_COMPLETED,
                                event.index(),
                                Boolean.FALSE
                        ));
                    }
                }
                case POWER_FLOW_END -> {
                    computationEntity.setOperation(event.operation());
                    repository.updateComputation(computationEntity);

                    if (event.violationPresent()) {
                        commandGateway.send(new RequestCommand(
                                event.id(),
                                event.computationId(),
                                event.computationType(),
                                Operation.REQUEST_START,
                                event.index()
                        ));
                    } else {
                        commandGateway.send(new PowerFlowCommand(
                                event.id(),
                                event.computationId(),
                                event.computationType(),
                                Operation.POWER_FLOW,
                                event.index() + 1,
                                Boolean.FALSE
                        ));
                    }
                }
                case POWER_FLOW_COMPLETED -> {
                    computationEntity.setOperation(event.operation());
                    repository.updateComputation(computationEntity);

                    commandGateway.send(new CompleteCommand(
                            event.id(),
                            event.computationId(),
                            event.computationType(),
                            Operation.COMPLETED,
                            event.index()
                    ));
                }
                default -> throw new InvalidFlowException(String.format("Operation %s not permitted on Clone", event.operation()));
            }
        }, () -> log.error("Computation with id: {} not present", event.id()));
    }
}