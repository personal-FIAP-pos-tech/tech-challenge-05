package com.viniciuspadovam.tc.cinco.packagemanagement.architecture;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Garante as regras de dependência da Clean Architecture entre as camadas.
 */
@AnalyzeClasses(
		packages = "com.viniciuspadovam.tc.cinco.packagemanagement",
		importOptions = ImportOption.DoNotIncludeTests.class)
class ArquiteturaTest {

	private static final String BASE = "com.viniciuspadovam.tc.cinco.packagemanagement";
	private static final String DOMAIN = BASE + ".domain..";
	private static final String APPLICATION = BASE + ".application..";
	private static final String INFRASTRUCTURE = BASE + ".infrastructure..";

	@ArchTest
	static final ArchRule camadasRespeitamADirecaoDasDependencias = layeredArchitecture()
			.consideringOnlyDependenciesInLayers()
			.withOptionalLayers(true)
			.layer("Domain").definedBy(DOMAIN)
			.layer("Application").definedBy(APPLICATION)
			.layer("Infrastructure").definedBy(INFRASTRUCTURE)
			.whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
			.whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure")
			.whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure");

	@ArchTest
	static final ArchRule dominioNaoDependeDeFrameworks = noClasses()
			.that().resideInAPackage(DOMAIN)
			.should().dependOnClassesThat().resideInAnyPackage("org.springframework..", "jakarta..")
			.allowEmptyShould(true);

	@ArchTest
	static final ArchRule aplicacaoNaoDependeDeFrameworks = noClasses()
			.that().resideInAPackage(APPLICATION)
			.should().dependOnClassesThat().resideInAnyPackage("org.springframework..", "jakarta..")
			.allowEmptyShould(true);
}
