package com.eudemon.core.model.base;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class RelationShip<E,F ,G>  extends  AbstractAuditingEntity{
    E owner;
    F target;
    G relation;

    public E getOwner() {
        return owner;
    }

    public void setOwner(E owner) {
        this.owner = owner;
    }

    public F getTarget() {
        return target;
    }

    public void setTarget(F target) {
        this.target = target;
    }

    public G getRelation() {
        return relation;
    }

    public void setRelation(G relation) {
        this.relation = relation;
    }
}
