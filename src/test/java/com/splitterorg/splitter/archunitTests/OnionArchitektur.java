package com.splitterorg.splitter.archunitTests;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;

import com.splitterorg.splitter.SplitterApplication;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class OnionArchitektur {

  private final JavaClasses klassen =
    new ClassFileImporter().withImportOption(new com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests()).importPackagesOf(SplitterApplication.class);

  private final JavaClasses klassenMitTests = new ClassFileImporter().importPackagesOf(SplitterApplication.class);

  @Test
  @DisplayName("Die SplitterApplication Anwendung hat eine Onion Architektur")
  public void rule1()
    throws Exception {
    ArchRule rule = onionArchitecture()
      .domainModels("com.splitterorg.splitter.domain..")
      .domainServices("com.splitterorg.splitter.service..")
      .applicationServices("com.splitterorg.splitter.service..")
      .adapter("web", "com.splitterorg.splitter.web..")
      .adapter("restapi","com.splitterorg.splitter.restapi..")
      .applicationServices("persistence","com.splitterorg.splitter.persistence..");

    rule.check(klassen);
  }
  @Test
  @Disabled
  @DisplayName("Das gesamte Projekt der SplitterApplication hat eine Onion Architektur")
  public void rule2()
      throws Exception {
    ArchRule rule = onionArchitecture()
            .domainModels("com.splitterorg.splitter.domain..")
            .domainServices("com.splitterorg.splitter.service..")
            .applicationServices("com.splitterorg.splitter.service..")
            .adapter("web", "com.splitterorg.splitter.web..")
            .adapter("restapi","com.splitterorg.splitter.restapi..")
            .applicationServices("persistence","com.splitterorg.splitter.persistence..");
    rule.check(klassenMitTests);
  }
}
