// settings.gradle.kts
pluginManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/jcenter") }
        maven { url = uri("https://artifact.bytedance.com/repository/Volcengine/") }
        maven { url = uri("https://s01.oss.sonatype.org/content/groups/public") }
        maven { url = uri("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_support/") }
        maven { url = uri("https://artifact.bytedance.com/repository/pangle") }
        maven { url = uri("https://www.jitpack.io") }

        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // 如果你还想让项目本身也能访问阿里云镜像，可以加下面这几行（可选）
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://www.jitpack.io") }
    }
}

rootProject.name = "微风音乐"
include(":app", ":myutil")