package com.marco.cqrs.model;


import com.marco.cqrs.command.*;
import com.marco.cqrs.events.*;
import com.marco.cqrs.exception.InvalidFlowException;
import com.marco.cqrs.type.ComputationType;
import com.marco.cqrs.type.Operation;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.common.Assert;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.io.Serializable;

@Aggregate
public class Computation implements Serializable {

    @AggregateIdentifier
    private String id;

    private String computationId;

    private ComputationType computationType;

    private Operation operation;

    private int index;

    private boolean violation;

    protected Computation() {
    }

    @CommandHandler
    public Computation(InitCommand command) {
        validateCommandInput(command);

        AggregateLifecycle.apply(new InitEvent(
                command.id(),
                command.computationId(),
                command.computationType(),
                command.operation(),
                0));
    }

    @CommandHandler
    public Computation(CloneCommand command) {
        validateCommandInput(command);

        var event = switch (command.operation()) {
            case CLONE_START -> new CloneEvent(
                    command.id(),
                    command.computationId(),
                    command.computationType(),
                    Operation.CLONE_START,
                    0);
            case CLONE_END -> new CloneEvent(
                    command.id(),
                    command.computationId(),
                    command.computationType(),
                    Operation.CLONE_END,
                    0);
            default -> throw new InvalidFlowException("Invalid command value for operation on clone");
        };

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public Computation(PowerFlowCommand command) {
        validateCommandInput(command);

        var event = switch (command.operation()) {
            case POWER_FLOW_START -> new PowerFlowEvent(
                    command.id(),
                    command.computationId(),
                    command.computationType(),
                    Operation.POWER_FLOW_START,
                    command.index(),
                    command.haveViolation());
            case POWER_FLOW -> new PowerFlowEvent(
                    command.id(),
                    command.computationId(),
                    command.computationType(),
                    Operation.POWER_FLOW,
                    command.index(),
                    command.haveViolation());
            case POWER_FLOW_END -> new PowerFlowEvent(
                    command.id(),
                    command.computationId(),
                    command.computationType(),
                    Operation.POWER_FLOW_END,
                    command.index(),
                    command.haveViolation());
            case POWER_FLOW_COMPLETED -> new PowerFlowEvent(
                    command.id(),
                    command.computationId(),
                    command.computationType(),
                    Operation.POWER_FLOW_COMPLETED,
                    command.index(),
                    command.haveViolation());
            default -> throw new InvalidFlowException("Invalid command value for operation on clone");
        };

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public Computation(RequestCommand command) {
        validateCommandInput(command);

        var event = switch (command.operation()) {
            case REQUEST_START -> new RequestEvent(
                    command.id(),
                    command.computationId(),
                    ComputationType.FLEXIBILITY,
                    Operation.REQUEST_START,
                    command.index());
            case REQUEST_END -> new RequestEvent(
                    command.id(),
                    command.computationId(),
                    command.computationType(),
                    Operation.REQUEST_END,
                    command.index());
            default -> throw new InvalidFlowException("Invalid command value for operation on clone");
        };

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public Computation(ErrorCommand command) {
        validateCommandInput(command);

        AggregateLifecycle.apply(new CloneEvent(
                command.id(),
                command.computationId(),
                command.computationType(),
                command.operation(),
                command.index())
        );
    }

    @CommandHandler
    public Computation(CompleteCommand command) {
        validateCommandInput(command);

        AggregateLifecycle.apply(new CompletedEvent(
                command.id(),
                command.computationId(),
                command.computationType(),
                command.operation(),
                command.index())
        );
    }

    @EventSourcingHandler
    public void on(InitEvent event) {
        updateAggregate(event);
    }

    @EventSourcingHandler
    public void on(CloneEvent event) {
        updateAggregate(event);
    }

    @EventSourcingHandler
    public void on(PowerFlowEvent event) {
        updateAggregate(event);
        this.violation = event.violationPresent();
    }

    @EventSourcingHandler
    public void on(RequestEvent event) {
        updateAggregate(event);
    }

    @EventSourcingHandler
    public void on(ErrorEvent event) {
        updateAggregate(event);
    }

    @EventSourcingHandler
    public void on(CompletedEvent event) {
        updateAggregate(event);
    }

    private void validateCommandInput(Command command) {
        Assert.nonNull(command.id(), () -> "id not be null");
        Assert.nonNull(command.computationId(), () -> "Computation id not be null");
        Assert.nonNull(command.computationType(), () -> "Computation Type not be null");
        Assert.nonNull(command.operation(), () -> "Computation Operation not be null");
        Assert.isTrue(command.index() >= 0, () -> "Computation index not be negative");
    }

    private void updateAggregate(Event event) {
        this.id = event.id();
        this.computationId = event.computationId();
        this.computationType = event.computationType();
        this.operation = event.operation();
        this.index = event.index();
    }
}