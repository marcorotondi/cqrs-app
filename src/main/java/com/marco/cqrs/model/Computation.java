package com.marco.cqrs.model;

import com.marco.cqrs.type.Operation;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.io.Serializable;

@Aggregate
public class Computation implements Serializable {

    @AggregateIdentifier
    private String id;

    private Operation operation;

    private int index;



    

}