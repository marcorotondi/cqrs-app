package com.marco.cqrs.command;

import com.marco.cqrs.type.ComputationType;
import com.marco.cqrs.type.Operation;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.io.Serializable;

public record PowerFlowCommand(
        @TargetAggregateIdentifier
        String id,
        String computationId,
        ComputationType computationType,
        Operation operation,
        Integer index,
        boolean haveViolation
) implements Command, Serializable {
}