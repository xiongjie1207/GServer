package com.wegame.framework.db;

import com.google.common.collect.Lists;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建SimpleSpecification来实现Specification接口，
 * 并且根据条件生成Specification对象，因为在最后查询的时候需要这个对象
 * SimpleSpecification是核心类型，
 * 用来根据条件生成Specification对象，这个SimpleSpecification直接存储了具体的查询条件。
 */
public class SimpleSpecification<T> implements Specification<T> {
    /**
     * 查询的条件列表，是一组列表
     */
    private final List<SpecificationOperator> opers;
    private final List<String> selectList;

    private SimpleSpecification(List<String> selectList, List<SpecificationOperator> opers) {
        this.opers = opers;
        if (selectList == null) {
            this.selectList = new ArrayList<>();
        } else {
            this.selectList = selectList;
        }


    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Predicate resultPre = criteriaBuilder.conjunction();
        for (String select : this.selectList) {
            criteriaQuery.multiselect(root.get(select));
        }
        for (SpecificationOperator so : opers) {
            switch (so.getJoin()) {
                case And -> {
                    Predicate pre = generatePredicate(root, criteriaBuilder, so);
                    resultPre = criteriaBuilder.and(resultPre, pre);
                }
                case Or -> {
                    Predicate pre = generatePredicate(root, criteriaBuilder, so);
                    resultPre = criteriaBuilder.or(resultPre, pre);
                }
                case GroupBy -> criteriaQuery.groupBy(root.get(so.getKey()));
            }
        }
        return resultPre;
    }

    private Predicate generatePredicate(Root<T> root, CriteriaBuilder criteriaBuilder, SpecificationOperator so) {
        return switch (so.getOper()) {
            case Equal -> criteriaBuilder.equal(root.get(so.getKey()), so.getValue());
            case LessThan -> criteriaBuilder.lt(root.get(so.getKey()).as(Number.class), (Number) so.getValue());
            case GreaterThan -> criteriaBuilder.gt(root.get(so.getKey()).as(Number.class), (Number) so.getValue());
            case LessEqualThan -> criteriaBuilder.le(root.get(so.getKey()).as(Number.class), (Number) so.getValue());
            case GreaterEqualThan -> criteriaBuilder.ge(root.get(so.getKey()).as(Number.class), (Number) so.getValue());
            case Like -> criteriaBuilder.like(root.get(so.getKey()).as(String.class), "%" + so.getValue() + "%");
            case LikeEnd -> criteriaBuilder.like(root.get(so.getKey()).as(String.class), so.getValue() + "%");
            case LikeStart -> criteriaBuilder.like(root.get(so.getKey()).as(String.class), "%" + so.getValue());
            case Null -> criteriaBuilder.isNull(root.get(so.getKey()));
            case NotNull -> criteriaBuilder.isNotNull(root.get(so.getKey()));
            case NotEqual -> criteriaBuilder.notEqual(root.get(so.getKey()), so.getValue());
            case In -> criteriaBuilder.in(root.get(so.getKey())).value(so.getValue());
        };
    }

    public static SimpleSpecificationBuilder newBuilder() {
        return new SimpleSpecificationBuilder();
    }

    public static class SimpleSpecificationBuilder {

        /**
         * 条件列表
         */
        private List<SpecificationOperator> opers;
        private List<String> selectList;

        /**
         * 构造，初始化无条件
         */
        private SimpleSpecificationBuilder() {
            opers = Lists.newArrayList();
        }

        public SimpleSpecificationBuilder select(List<String> selectList) {
            this.selectList = selectList;
            return this;
        }

        /**
         * 往list中填加条件
         *
         * @param key
         * @param oper
         * @param value
         * @param join
         * @return
         */
        public <T> SimpleSpecificationBuilder add(String key, Operator oper, T value, Join join) {
            SpecificationOperator so = new SpecificationOperator(key, value, oper, join);
            opers.add(so);
            return this;
        }

        /**
         * 填加一个and条件
         *
         * @param key
         * @param oper
         * @param value
         * @return
         */
        public <T> SimpleSpecificationBuilder and(String key, Operator oper, T value) {
            return this.add(key, oper, value, Join.And);
        }

        /**
         * 根据field分组查询
         *
         * @param field
         * @return
         */
        public SimpleSpecificationBuilder groupBy(String field) {
            return this.add(field, null, null, Join.GroupBy);
        }


        /**
         * 填加一个or条件
         *
         * @param key
         * @param oper
         * @param value
         * @return
         */
        public <T> SimpleSpecificationBuilder or(String key, Operator oper, T value) {
            return this.add(key, oper, value, Join.Or);
        }

        /**
         * 触发SimpleSpecification并返回Specification
         */
        public <T> SimpleSpecification<T> build() {
            return new SimpleSpecification<>(selectList, opers);
        }

    }
}