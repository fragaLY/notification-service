import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	application

	id("org.springframework.boot") version "2.2.4.RELEASE"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"

	id("com.palantir.docker") version "0.24.0"

	kotlin("jvm") version "1.3.61"
	kotlin("plugin.spring") version "1.3.61"
}

group = "by.integrated"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
	jcenter()
	maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
	implementation("org.springframework.boot.experimental:spring-boot-starter-data-r2dbc")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("io.r2dbc:r2dbc-h2")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.boot.experimental:spring-boot-bom-r2dbc:0.1.0.M3")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

application {
	mainClassName = "by.integrated.notification.NotificationApplicationKt"
	applicationName = "notification-service"
}

docker {
	name = "${application.applicationName}:${version}"
	files(tasks["bootJar"].outputs)
	setDockerfile(file("docker/Dockerfile"))
	labels(mapOf("app-name" to application.applicationName, "service-version" to version.toString()))
	noCache(true)
}