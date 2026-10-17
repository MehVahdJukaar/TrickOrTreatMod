plugins {
    id("com.possible-triangle.common")
}

common {
    neoformVersion = "1.21.1-20240808.144430"
    accessWidener()
}

val moonlight_version: String by extra
val supplementaries_version: String by extra
val polytone_version: String by extra

dependencies {
    modCompileOnly("net.mehvahdjukaar:moonlight-common:${moonlight_version}")
    accessTransformers("net.mehvahdjukaar:moonlight-common:${moonlight_version}")
    modCompileOnly("net.mehvahdjukaar:polytone-common:${polytone_version}")

    modCompileOnly("net.mehvahdjukaar:supplementaries-common:${supplementaries_version}")

    modCompileOnly("curse.maven:farmers-delight-398521:8083481") // 1.21.1-1.3.2

    modCompileOnly("curse.maven:jei-238222:7420587")
    modCompileOnly("curse.maven:roughly-enough-items-310111:6199140")
    modCompileOnly("curse.maven:emi-580555:6420931")

    modCompileOnly("curse.maven:quark-243121:8146177")
    modCompileOnly("maven.modrinth:immediatelyfast:1.6.1+1.21.1-neoforge")
}
