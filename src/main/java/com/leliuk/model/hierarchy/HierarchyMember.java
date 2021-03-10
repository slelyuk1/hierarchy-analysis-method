package com.leliuk.model.hierarchy;

import com.sun.istack.internal.NotNull;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;

@Data
public class HierarchyMember implements Serializable {
    private String name;
    private String description;

    public HierarchyMember(@NotNull String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("HierarchyMember name cannot be empty!");
        }
        this.name = name;
    }

    public HierarchyMember(String name, String description) {
        this(name);
        this.description = description;
    }
}
