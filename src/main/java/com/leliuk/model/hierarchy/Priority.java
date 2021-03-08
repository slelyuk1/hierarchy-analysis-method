package com.leliuk.model.hierarchy;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class Priority implements Serializable {

    HierarchyMember hierarchyMember;
    List<Number> values;

    public Priority(HierarchyMember hierarchyMember, int valuesQuantity) {
        this.hierarchyMember = hierarchyMember;
        this.values = zeroGenerator().limit(valuesQuantity).collect(Collectors.toList());
    }

    public void addValues(int quantity) {
        values.addAll(zeroGenerator().limit(quantity).collect(Collectors.toList()));
    }

    private static Stream<Number> zeroGenerator() {
        return Stream.generate(() -> 0);
    }

}
