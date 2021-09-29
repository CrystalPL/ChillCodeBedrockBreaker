package pl.chillcode.chillbedrockbreaker.config.serializer;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.Material;
import pl.chillcode.chillbedrockbreaker.crafting.Crafting;

final class CraftingSerializer implements ObjectSerializer<Crafting> {
    @Override
    public boolean supports(final Class<? super Crafting> type) {
        return Crafting.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(final Crafting crafting, final SerializationData data) {
        data.add("1", crafting.getSlot1());
        data.add("2", crafting.getSlot2());
        data.add("3", crafting.getSlot3());
        data.add("4", crafting.getSlot4());
        data.add("5", crafting.getSlot5());
        data.add("6", crafting.getSlot6());
        data.add("7", crafting.getSlot7());
        data.add("8", crafting.getSlot8());
        data.add("9", crafting.getSlot9());
    }

    @Override
    public Crafting deserialize(final DeserializationData data, final GenericsDeclaration generics) {
        final Material slot1 = data.get("1", Material.class);
        final Material slot2 = data.get("2", Material.class);
        final Material slot3 = data.get("3", Material.class);
        final Material slot4 = data.get("4", Material.class);
        final Material slot5 = data.get("5", Material.class);
        final Material slot6 = data.get("6", Material.class);
        final Material slot7 = data.get("7", Material.class);
        final Material slot8 = data.get("8", Material.class);
        final Material slot9 = data.get("9", Material.class);

        return new Crafting(slot1, slot2, slot3, slot4, slot5, slot6, slot7, slot8, slot9);
    }
}
