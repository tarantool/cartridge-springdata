package org.springframework.data.tarantool.entities.fieldnames;

import org.springframework.data.annotation.Id;
import org.springframework.data.tarantool.core.mapping.Field;

public class EntityParent {
    @Id
    private Integer id;

    @Field(name = "key_from_parent")
    private String key;

    @Field("name_from_parent")
    private String name;

    private String author;

    public Integer getId() {
        return id;
    }

    public EntityParent setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getKey() {
        return key;
    }

    public EntityParent setKey(String key) {
        this.key = key;
        return this;
    }

    public String getName() {
        return name;
    }

    public EntityParent setName(String name) {
        this.name = name;
        return this;
    }

    @Field("author_from_parent_getter")
    public String getAuthor() {
        return author;
    }

    public EntityParent setAuthor(String author) {
        this.author = author;
        return this;
    }
}
