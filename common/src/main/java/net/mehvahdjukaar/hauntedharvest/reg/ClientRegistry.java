package net.mehvahdjukaar.hauntedharvest.reg;

import net.mehvahdjukaar.hauntedharvest.client.SplatteredEggRenderer;
import net.mehvahdjukaar.moonlight.api.platform.ClientPlatformHelper;
import net.minecraft.client.particle.HeartParticle;

public class ClientRegistry {

    public static void init() {
        ClientPlatformHelper.addEntityRenderersRegistration(ClientRegistry::registerEntityRenderers);
        ClientPlatformHelper.addParticleRegistration(ClientRegistry::registerParticles);
    }

    private static void registerParticles(ClientPlatformHelper.ParticleEvent event) {
        event.register(ModRegistry.SPOOKED_PARTICLE.get(), HeartParticle.AngryVillagerProvider::new);
    }

    private static void registerEntityRenderers(ClientPlatformHelper.EntityRendererEvent event) {
        event.register(ModRegistry.SPLATTERED_EGG_ENTITY.get(), SplatteredEggRenderer::new);
    }
}