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
    modImplementation("curse.maven:farmers-delight-398521:5051242")
    modCompileOnly("curse.maven:farmers-delight-398521:5772720")
    modCompileOnly("curse.maven:jei-238222:5846878")
    modCompileOnly("curse.maven:roughly-enough-items-310111:5731643")
    modCompileOnly("curse.maven:emi-580555:5872513")
    modCompileOnly("org.violetmoon.quark:Quark-4.0-beta-431.3254")
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
