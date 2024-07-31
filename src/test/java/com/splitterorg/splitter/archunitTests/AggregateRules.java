package com.splitterorg.splitter.archunitTests;




import static com.splitterorg.splitter.archunitTests.rules.HaveExactlyOneAggregateRoot.HAVE_EXACTLY_ONE_AGGREGATE_ROOT;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.splitterorg.splitter.SplitterApplication;
import com.splitterorg.splitter.annotationes.AggregateRoot;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;


@AnalyzeClasses(packagesOf = SplitterApplication.class)
  public class AggregateRules{


    @ArchTest
    static final ArchRule onlyAggregateRootsArePublic = classes()
      .that()
      .areNotAnnotatedWith(AggregateRoot.class)
      .and()
      .resideInAPackage("..domain..")
      .should()
      .notBePublic()
        .orShould()
        .beRecords()
      .because("the implementation of an aggregate " +
        "should be hidden");


    @ArchTest
    static final ArchRule oneAggregateRootPerAggregate = slices()
      .matching("..domain.(*)..")
      .should(HAVE_EXACTLY_ONE_AGGREGATE_ROOT);

  }


