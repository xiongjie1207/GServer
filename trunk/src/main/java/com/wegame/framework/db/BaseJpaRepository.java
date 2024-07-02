package com.wegame.framework.db;

import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

/**
 * @author: xiongjie.cn@gmail.com
 * @date: 2024/05/30
 */
public abstract class BaseJpaRepository<T, ID> extends SimpleJpaRepository<T, ID> {

    protected EntityManager em;

    protected BaseJpaRepository(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.em = em;
    }
}
