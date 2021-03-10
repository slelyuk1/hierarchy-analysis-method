package com.leliuk.model.other;

import com.leliuk.model.hierarchy.HierarchyMember;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class Priority implements Serializable {

    HierarchyMember hierarchyMember;
    List<Double> values;

    public Priority(HierarchyMember hierarchyMember, int valuesQuantity) {
        this.hierarchyMember = hierarchyMember;
        this.values = zeroGenerator().limit(valuesQuantity).collect(Collectors.toList());
    }

    private static Stream<Double> zeroGenerator() {
        return Stream.generate(() -> 0.0);
    }

    public void addValues(int quantity) {
        values.addAll(zeroGenerator().limit(quantity).collect(Collectors.toList()));
    }

}
