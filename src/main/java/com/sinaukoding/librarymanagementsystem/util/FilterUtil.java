package com.sinaukoding.librarymanagementsystem.util;

import com.sinaukoding.librarymanagementsystem.builder.CustomBuilder;
import com.sinaukoding.librarymanagementsystem.builder.CustomSpecification;
import com.sinaukoding.librarymanagementsystem.builder.MultipleCriteria;
import com.sinaukoding.librarymanagementsystem.builder.SearchCriteria;
import io.micrometer.common.util.StringUtils;

import java.util.Set;

public class FilterUtil {

    public static void builderConditionNotBlankEqual(String field, String value, CustomBuilder<?> builder) {
        if (StringUtils.isNotBlank(value)) {
            builder.with(SearchCriteria.of(field, CustomSpecification.OPERATION_EQUAL, value));
        }
    }

    public static void builderConditionNotNullEqual(String field, Object value, CustomBuilder<?> builder) {
        if (value != null) {
            builder.with(SearchCriteria.of(field, CustomSpecification.OPERATION_EQUAL, value));
        }
    }

    public static void builderConditionNotBlankLike(String field, String value, CustomBuilder<?> builder) {
        if (StringUtils.isNotBlank(value)) {
            builder.with(SearchCriteria.of(field, CustomSpecification.OPERATION_LIKE, value));
        }
    }

    public static void builderConditionNotBlankEqualJoin(String field, String value, CustomBuilder<?> builder) {
        if (StringUtils.isNotBlank(value)) {
            builder.with(SearchCriteria.of(field, CustomSpecification.OPERATION_JOIN_EQUAL, value));
        }
    }

    public static void builderConditionNotBlankInJoin(String field, Set<String> listValue, CustomBuilder<?> builder) {
        if (listValue != null && !listValue.isEmpty()) {
            builder.with(SearchCriteria.of(field, CustomSpecification.OPERATION_JOIN_IN, listValue));
        }
    }

    /**
     * Search value in multiple fields with OR condition
     * Example: search in judulBuku OR penulis OR penerbit
     */
    public static void builderConditionSearchInOr(String field1, String field2, String field3, String value, CustomBuilder<?> builder) {
        if (StringUtils.isNotBlank(value)) {
            MultipleCriteria multipleCriteria = MultipleCriteria.builder()
                    .criterias(SearchCriteria.OPERATOR_OR,
                            SearchCriteria.of(field1, CustomSpecification.OPERATION_LIKE, value),
                            SearchCriteria.of(field2, CustomSpecification.OPERATION_LIKE, value),
                            SearchCriteria.of(field3, CustomSpecification.OPERATION_LIKE, value))
                    .operatorCriteria(SearchCriteria.OPERATOR_OR)
                    .build();

            builder.with(multipleCriteria);
        }
    }

    /**
     * Search value in 4 fields with OR condition
     * Example: search in judulBuku OR penulis OR penerbit OR isbn
     */
    public static void builderConditionSearchInOr(String field1, String field2, String field3, String field4, String value, CustomBuilder<?> builder) {
        if (StringUtils.isNotBlank(value)) {
            MultipleCriteria multipleCriteria = MultipleCriteria.builder()
                    .criterias(SearchCriteria.OPERATOR_OR,
                            SearchCriteria.of(field1, CustomSpecification.OPERATION_LIKE, value),
                            SearchCriteria.of(field2, CustomSpecification.OPERATION_LIKE, value),
                            SearchCriteria.of(field3, CustomSpecification.OPERATION_LIKE, value),
                            SearchCriteria.of(field4, CustomSpecification.OPERATION_LIKE, value))
                    .operatorCriteria(SearchCriteria.OPERATOR_OR)
                    .build();

            builder.with(multipleCriteria);
        }
    }

}
