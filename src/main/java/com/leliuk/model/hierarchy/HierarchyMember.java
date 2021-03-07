package com.leliuk.model.hierarchy;

import lombok.Getter;

@Getter
public class HierarchyMember {
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
