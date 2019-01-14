package com.eudemon.core.model.base;

import javax.persistence.MappedSuperclass;
import java.util.List;

@MappedSuperclass
public class Group<T> {
    List<T> elements;

    public List<T> getElements() {
        return elements;
    }

    public void setElements(List<T> elements) {
        this.elements = elements;
    }
}
