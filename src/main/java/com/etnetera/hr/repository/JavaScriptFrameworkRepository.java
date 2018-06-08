package com.etnetera.hr.repository;

import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.data.QJavaScriptFramework;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Spring data repository interface used for accessing the data in database.
 *
 * @author Etnetera
 */

public interface JavaScriptFrameworkRepository extends CrudRepository<JavaScriptFramework, Long>,
        QuerydslPredicateExecutor<JavaScriptFramework>,
        QuerydslBinderCustomizer<QJavaScriptFramework> {

    @Override
    default public void customize(QuerydslBindings bindings, QJavaScriptFramework qJavaScriptFramework) {

        // ignoring cases at version and name
        bindings
                .bind(String.class)
                .first((SingleValueBinding<StringPath, String>) StringExpression::likeIgnoreCase);

        // binding for filtering by parameter deprecated=[true|false]
        bindings
                .bind(qJavaScriptFramework.deprecated)
                .first((path, value) -> {
                    if (value) {
                        return qJavaScriptFramework.deprecationDate.lt(Instant.now().getEpochSecond());
                    }
                    return qJavaScriptFramework.deprecationDate.goe(Instant.now().getEpochSecond());
                });

        // filter by hypeLevel
        bindings
                .bind(qJavaScriptFramework.hypeLevel)
                .all((path, values) -> {
                    final ArrayList<? extends Byte> levels = new ArrayList<>(values);
                    switch (levels.size()){
                        case 1:
                            return java.util.Optional.ofNullable(qJavaScriptFramework.hypeLevel.eq(levels.get(0)));
                        case 2:
                            Collections.sort(levels);
                            return java.util.Optional.of(qJavaScriptFramework.hypeLevel.between(levels.get(0), levels.get(1)));
                    }
                    throw new IllegalArgumentException("1 or 2 hypeLevel params expected for:" + path + " found:" + values);
                });
    }
}
