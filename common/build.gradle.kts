plugins {
    id("com.possible-triangle.common")
}

common {
    //pinned so the build doesn't need to hit maven.neoforged.net to list versions
    neoformVersion = "26.1.2-1"
    accessWidener()
}

val moonlight_version: String by extra

dependencies {
    // Use the common variant in common (loader-agnostic) for compile-only access
    modCompileOnly("net.mehvahdjukaar:moonlight-common:${moonlight_version}")
    accessTransformers("net.mehvahdjukaar:moonlight-common:${moonlight_version}")

    //no 26.1.2 builds of these yet. uncomment as they port
    //modCompileOnly("net.mehvahdjukaar:supplementaries-common:${supplementaries_version}")
    modCompileOnly("maven.modrinth:farmers-delight-refabricated:26.1-3.6.26")
    modCompileOnly("mezz.jei:jei-26.1.2-common-api:29.16.0.48")
    //modCompileOnly("curse.maven:roughly-enough-items-310111:6199140")
    //modCompileOnly("curse.maven:emi-580555:6420931")
    //modCompileOnly("curse.maven:quark-243121:8146177")
    //modCompileOnly("maven.modrinth:immediatelyfast:1.6.1+1.21.1-neoforge")
}
