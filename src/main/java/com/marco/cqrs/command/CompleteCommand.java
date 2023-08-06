package com.marco.cqrs.command;

import com.marco.cqrs.type.ComputationType;
import com.marco.cqrs.type.Operation;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.io.Serializable;

public record CompleteCommand(
        @TargetAggregateIdentifier
        String id,
        ComputationType computationType,
        Operation operation,
        Integer index
) implements Command, Serializable {
}