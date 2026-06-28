plugins {
    id("com.possible-triangle.fabric")
}

fabric {
    dependOn(project(":common"))
    accessWidener(project(":common"))
}

val moonlight_version: String by extra
val supplementaries_version: String by extra
val cloth_version: String by extra
val emi_version: String by extra

dependencies {
    modImplementation("net.mehvahdjukaar:moonlight-fabric:${moonlight_version}")

    // Mirrored from common (platform variant)
    modImplementation("net.mehvahdjukaar:supplementaries-fabric:${supplementaries_version}")
    modCompileOnly("curse.maven:farmers-delight-refabricated-993166:8088691")
    modCompileOnly("curse.maven:jei-238222:7420583")
    modCompileOnly("curse.maven:roughly-enough-items-310111:6199139")
    modCompileOnly("curse.maven:roughly-enough-items-310111:6199140") // NeoForge build, provides me.shedaniel.rei.forge for common REICompat
    modCompileOnly("curse.maven:emi-580555:6420930")
    modCompileOnly("curse.maven:quark-243121:8146177")
    modCompileOnly("maven.modrinth:immediatelyfast:1.6.1+1.21.1-neoforge")

    // Fabric-only deps
    modImplementation("me.shedaniel.cloth:cloth-config-fabric:${cloth_version}") {
        exclude(group = "net.fabricmc.fabric-api")
    }
    modImplementation("curse.maven:yacl-667299:4574163")
    modImplementation("curse.maven:fabric-seasons-413523:5789846")
    modCompileOnly("curse.maven:modmenu-308702:3920481") {
        exclude(module = "fabric-api")
    }
    modCompileOnly("curse.maven:amendments-896746:5791204")
    modCompileOnly("dev.emi:emi:${emi_version}+1.19.4:api")
}
