package org.springframework.data.tarantool.repository;


import org.springframework.data.tarantool.entities.ObjectWithoutTuple;

/**
 * @author Oleg Kuznetsov
 */
public interface RepositoryWithoutTupleAnnotationOnEntity extends TarantoolRepository<ObjectWithoutTuple, String> {
}
