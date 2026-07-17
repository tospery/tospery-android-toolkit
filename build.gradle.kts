import com.android.build.api.variant.LibraryAndroidComponentsExtension
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

plugins {
    // 在根工程统一加载插件，子模块应用时不再重复加载 Kotlin 插件。
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

/**
 * 根据当前 Gradle 模块路径生成稳定的模块元数据。
 */
abstract class GenerateModuleMetadataTask : DefaultTask() {
    @get:Input
    abstract val modulePath: Property<String>

    @get:Input
    abstract val moduleMetadataPackage: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val outputFile = outputDirectory.get()
            .file("${moduleMetadataPackage.get().replace('.', '/')}/ModuleMetadata.kt")
            .asFile

        outputFile.parentFile.mkdirs()
        outputFile.writeText(
            """
            |package ${moduleMetadataPackage.get()}
            |
            |/**
            | * 由 Gradle 根据当前模块路径生成，禁止手动修改。
            | */
            |internal object ModuleMetadata {
            |    const val path: String = "${modulePath.get()}"
            |}
            |
            """.trimMargin(),
        )
    }
}

/**
 * 为所有 Toolkit 子模块注册模块元数据生成任务。
 */
subprojects {
    val moduleProjectPath = path
    val moduleIdentifier = moduleProjectPath
        .trim(':')
        .replace(Regex("[^A-Za-z0-9_]"), "_")
        .ifBlank { "root" }
    val moduleMetadataPackageName = "com.tospery.buildmetadata.module_$moduleIdentifier"
    val generatedModuleMetadataDirectory = layout.buildDirectory.dir(
        "generated/source/module-metadata/kotlin",
    )

    val generateModuleMetadata = tasks.register<GenerateModuleMetadataTask>(
        "generateModuleMetadata",
    ) {
        modulePath.set(moduleProjectPath)
        moduleMetadataPackage.set(moduleMetadataPackageName)
        outputDirectory.set(generatedModuleMetadataDirectory)
    }

    // 将生成目录接入纯 JVM 模块的主源码集。
    plugins.withId("java") {
        extensions.configure<JavaPluginExtension> {
            sourceSets.named("main") {
                java.srcDir(generatedModuleMetadataDirectory)
            }
        }
    }

    // 将生成目录接入 Android Library 的 Kotlin 主源码集。
    plugins.withId("com.android.library") {
        extensions.configure<LibraryAndroidComponentsExtension> {
            onVariants(selector().all()) { variant ->
                variant.sources.kotlin?.addGeneratedSourceDirectory(
                    generateModuleMetadata,
                    GenerateModuleMetadataTask::outputDirectory,
                )
            }
        }
    }

    // 确保所有 Kotlin 编译任务开始前已经生成模块元数据。
    tasks.configureEach {
        if (name.startsWith("compile") && name.endsWith("Kotlin")) {
            dependsOn(generateModuleMetadata)
        }
    }
}
