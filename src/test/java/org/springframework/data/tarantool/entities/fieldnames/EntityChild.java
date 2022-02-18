package org.springframework.data.tarantool.entities.fieldnames;

import org.springframework.data.tarantool.core.mapping.Field;

public class EntityChild extends EntityParent {
    @Field("name_from_child")
    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public EntityChild setName(String name) {
        this.name = name;
        return this;
    }
}
