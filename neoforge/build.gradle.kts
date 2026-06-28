plugins {
    id("com.possible-triangle.neoforge")
}

neoforge {
    dependOn(project(":common"))
    accessWidener(project(":common"))
}

val moonlight_version: String by extra
val supplementaries_version: String by extra

dependencies {
    modImplementation("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}")
    accessTransformers("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}")

    // Mirrored from common (platform variant)
    modImplementation("net.mehvahdjukaar:supplementaries-neoforge:${supplementaries_version}")
    modImplementation("curse.maven:farmers-delight-398521:8083481") // 1.21.1-1.3.2
    modCompileOnly("curse.maven:jei-238222:7420587")
    modCompileOnly("curse.maven:roughly-enough-items-310111:6199140")
    modCompileOnly("curse.maven:emi-580555:6420931")
    modCompileOnly("curse.maven:quark-243121:8146177")
    modCompileOnly("maven.modrinth:immediatelyfast:1.6.1+1.21.1-neoforge")

    // NeoForge-only deps
    modImplementation("curse.maven:configured-457570:5873783")
    modCompileOnly("curse.maven:amendments-896746:5692774")
    modCompileOnly("curse.maven:serene-seasons-291874:4502525")
    modCompileOnly("curse.maven:autumnity-365045:7118591") // 1.21.1-6.0.1
    modCompileOnly("curse.maven:caverns-and-chasms-438005:8155745") // 1.21.1-3.0.0
}
