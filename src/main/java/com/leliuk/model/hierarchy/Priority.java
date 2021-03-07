package com.leliuk.model.hierarchy;

import lombok.Value;

@Value
public class Priority<T extends Number> {
    HierarchyMember hierarchyMember;
    T[] priorities;
}
