package org.springframework.data.tarantool.repository.support;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.tarantool.core.TarantoolOperations;
import org.springframework.data.tarantool.repository.TarantoolRepository;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Basic Tarantool repository implementation
 *
 * @param <T>  entity type
 * @param <ID> entity identifier (primary key) type
 * @author Alexey Kuzin
 */
public class SimpleTarantoolRepository<T, ID> implements TarantoolRepository<T, ID> {

    private final TarantoolOperations tarantoolOperations;
    private final TarantoolEntityInformation<T, ID> entityInformation;

    public SimpleTarantoolRepository(TarantoolEntityInformation<T, ID> entityInformation,
                                     TarantoolOperations tarantoolOperations) {
        this.entityInformation = entityInformation;
        this.tarantoolOperations = tarantoolOperations;
    }

    @Override
    public List<T> findAll() {
        return tarantoolOperations.findAll(entityInformation.getJavaType());
    }

    @Override
    public Iterable<T> findAll(Sort sort) {
        // sort is not supported in the driver yet. TODO change this when it is added to the driver
        return findAll();
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        throw new UnsupportedOperationException("Count and paging are not supported in the driver yet");
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        throw new UnsupportedOperationException("Search by multiple ids is not supported in the driver yet");
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(tarantoolOperations.findById(id, entityInformation.getJavaType()));
    }

    @Override
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S extends T> S save(S entity) {
        Assert.notNull(entity, "The entity must not be null");

        return tarantoolOperations.save(entity, (Class<S>) entityInformation.getJavaType());
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        Assert.notNull(entities, "The given Iterable of entities must not be null");

        List<S> result = new ArrayList<>();
        for (S entity : entities) {
            result.add(save(entity));
        }

        return result;
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("Count and paging are not supported in the driver yet");
    }

    @Override
    public void deleteById(ID id) {
        Assert.notNull(id, "The given id must not be null");

        tarantoolOperations.removeById(id, entityInformation.getJavaType());
    }

    @Override
    public void delete(T entity) {
        Assert.notNull(entity, "The given entity must not be null");

        tarantoolOperations.remove(entity, entityInformation.getJavaType());
    }

    @Override
    public void deleteAll(Iterable<? extends T> iterable) {
        throw new UnsupportedOperationException("Delete by multiple ids is not supported in the driver yet");
    }

    @Override
    public void deleteAll() {
        tarantoolOperations.truncate(entityInformation.getSpaceName());
    }
}
