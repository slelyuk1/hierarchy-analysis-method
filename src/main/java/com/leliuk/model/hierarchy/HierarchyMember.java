package com.leliuk.model.hierarchy;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
public class HierarchyMember implements Serializable {
    private String name;
    private String description;

    public HierarchyMember(String name) {
        this.name = name;
        description = null;
    }

    public HierarchyMember(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
