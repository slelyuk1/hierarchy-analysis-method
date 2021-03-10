package com.leliuk.model.hierarchy;

import lombok.Data;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
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
