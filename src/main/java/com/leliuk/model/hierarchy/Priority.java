package com.leliuk.model.hierarchy;

import lombok.Value;

import java.io.Serializable;

@Value
public class Priority<T extends Number> implements Serializable {
    HierarchyMember hierarchyMember;
    T[] priorities;
}
