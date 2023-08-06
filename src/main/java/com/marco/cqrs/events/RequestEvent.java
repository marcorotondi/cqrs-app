package com.marco.cqrs.events;

import com.marco.cqrs.type.ComputationType;
import com.marco.cqrs.type.Operation;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.io.Serializable;

public record RequestEvent(
        @TargetAggregateIdentifier
        String id,
        ComputationType computationType,
        Operation operation,
        Integer index
) implements Event, Serializable {
}