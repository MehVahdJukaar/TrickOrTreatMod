package net.mehvahdjukaar.hauntedharvest.reg;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.items.ModCarvedPumpkinItem;
import net.mehvahdjukaar.hauntedharvest.network.ClientBoundCopyCarvingCommand;
import net.mehvahdjukaar.hauntedharvest.network.NetworkHandler;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

public class ModCommands {


    public static void init() {
        RegHelper.addCommandRegistration(ModCommands::register);
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(HauntedHarvest.MOD_ID)
                        .then(CopyCarvings.register(dispatcher))
        );
    }

    public static class CopyCarvings implements Command<CommandSourceStack> {

        private static final CopyCarvings CMD = new CopyCarvings();

        public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
            return Commands.literal("copycarving")
                    .requires((p) -> p.hasPermission(0))
                    .executes(CMD);
        }

        @Override
        public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            var e = context.getSource().getEntity();
            if (e instanceof ServerPlayer p) {
                var item = p.getItemInHand(InteractionHand.MAIN_HAND);
                if (item.getItem() instanceof ModCarvedPumpkinItem i) {
                    CompoundTag t = item.getTag();
                    if (t != null && t.contains("BlockEntityTag")) {
                        t = t.getCompound("BlockEntityTag");
                        NetworkHandler.CHANNEL.sendToClientPlayer( p, new ClientBoundCopyCarvingCommand(t.get("Pixels").getAsString()));
                        context.getSource().sendSuccess(Component.literal("Copied content to clipboard"), false);
                    }
                }
            }
            context.getSource().sendFailure(Component.literal("You must hold a carved pumpkin item"));

            return 0;
        }
    }
}
