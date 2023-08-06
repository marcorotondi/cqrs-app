package com.marco.cqrs.entity;


import com.marco.cqrs.type.ComputationType;
import com.marco.cqrs.type.Operation;

import java.io.Serializable;
import java.util.Objects;

public class ComputationEntity implements Serializable {

    private String id;

    private ComputationType computationType;

    private Operation operation;

    private int index;

    public ComputationEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ComputationType getComputationType() {
        return computationType;
    }

    public void setComputationType(ComputationType computationType) {
        this.computationType = computationType;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComputationEntity that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "ComputationEntity{" +
                "id='" + id + '\'' +
                ", computationType=" + computationType +
                ", operation=" + operation +
                ", index=" + index +
                '}';
    }
}