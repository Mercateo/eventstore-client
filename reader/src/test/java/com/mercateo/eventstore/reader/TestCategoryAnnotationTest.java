package com.mercateo.eventstore.reader;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import com.mercateo.common.ArchitectureTest;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchUnitRunner;
import com.tngtech.archunit.lang.ArchRule;

@Category(ArchitectureTest.class)
@RunWith(ArchUnitRunner.class)
@AnalyzeClasses(packages = "com.mercateo.eventstore")
public class TestCategoryAnnotationTest {

    @ArchTest
    public static final ArchRule testCategoryAnnotations = classes()
        .that()
        .resideInAPackage("com.mercateo.eventstore")
        .and()
        .haveNameMatching(".*Test$")
        .should()
        .beAnnotatedWith(Category.class);
}
