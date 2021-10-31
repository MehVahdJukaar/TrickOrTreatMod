package net.mehvahdjukaar.hauntedharvest.loot;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;

public class AddItemModifier extends LootModifier {
    private final Item addedItem;

    protected AddItemModifier(LootItemCondition[] conditionsIn, Item addedItemIn) {
        super(conditionsIn);
        this.addedItem = addedItemIn;
    }

    @Nonnull
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        generatedLoot.add(new ItemStack(this.addedItem));
        return generatedLoot;
    }


    public static class Serializer extends GlobalLootModifierSerializer<AddItemModifier> {
        public Serializer() {
        }

        public AddItemModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] ailootcondition) {
            Item addedItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(GsonHelper.getAsString(object, "item")));
            return new AddItemModifier(ailootcondition, addedItem);
        }

        public JsonObject write(AddItemModifier instance) {
            JsonObject json = makeConditions(instance.conditions);
            json.addProperty("item", ForgeRegistries.ITEMS.getKey(instance.addedItem).toString());
            return json;
        }
    }
}
