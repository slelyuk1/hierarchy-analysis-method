package com.leliuk.model.hierarchy;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class HierarchyMember implements Serializable {
    private final String name;
    private final String description;

    public HierarchyMember(String name) {
        this.name = name;
        description = null;
    }

    public HierarchyMember(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static HierarchyMember copyOf(HierarchyMember toCopy) {
        return new HierarchyMember(toCopy.getName(), toCopy.getDescription());
    }
}
