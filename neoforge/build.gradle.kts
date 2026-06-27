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
    modImplementation("curse.maven:farmers-delight-398521:5051242")
    modCompileOnly("curse.maven:farmers-delight-398521:5772720")
    modCompileOnly("curse.maven:jei-238222:5846878")
    modCompileOnly("curse.maven:roughly-enough-items-310111:5731643")
    modCompileOnly("curse.maven:emi-580555:5872513")
    modCompileOnly("org.violetmoon.quark:Quark-4.0-beta-431.3254")
    modCompileOnly("maven.modrinth:immediatelyfast:1.6.1+1.21.1-neoforge")

    // NeoForge-only deps
    modImplementation("curse.maven:configured-457570:5873783")
    modCompileOnly("curse.maven:amendments-896746:5692774")
    modCompileOnly("curse.maven:serene-seasons-291874:4502525")
    modCompileOnly("com.teamabnormals:autumnity:1.19.2-4.0.0")
}
