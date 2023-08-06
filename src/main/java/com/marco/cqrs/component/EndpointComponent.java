package com.marco.cqrs.component;


import com.marco.cqrs.command.CloneCommand;
import com.marco.cqrs.command.PowerFlowCommand;
import com.marco.cqrs.command.RequestCommand;
import com.marco.cqrs.events.Event;
import com.marco.cqrs.type.Operation;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EndpointComponent {

    private static final Logger log = LoggerFactory.getLogger(EndpointComponent.class);

    private final CommandGateway commandGateway;

    public EndpointComponent(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }


    public void callClone(Event event) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        commandGateway.send(new CloneCommand(
                UUID.randomUUID().toString(),
                event.computationId(),
                event.computationType(),
                Operation.CLONE_END,
                event.index()
        ));
    }

    public void callFlexibilityWorker(Event event) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        final var nextCommand = switch (event.operation()) {
            case POWER_FLOW -> new PowerFlowCommand(
                    UUID.randomUUID().toString(),
                    event.computationId(),
                    event.computationType(),
                    Operation.POWER_FLOW_END,
                    event.index(),
                    Boolean.TRUE
            );
            case REQUEST_START -> new RequestCommand(
                    UUID.randomUUID().toString(),
                    event.computationId(),
                    event.computationType(),
                    Operation.REQUEST_END,
                    event.index()
            );
            default -> new RuntimeException("Error!");
        };

        commandGateway.send(nextCommand);
    }
}