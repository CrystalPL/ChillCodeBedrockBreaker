package pl.chillcode.chillbedrockbreaker.config.serializer;

import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.SerdesRegistry;

public final class Serdes implements OkaeriSerdesPack {

    @Override
    public void register(final SerdesRegistry serdesRegistry) {
        serdesRegistry.register(new MaterialTransformer());
        serdesRegistry.register(new ItemStackSerializer());
        serdesRegistry.register(new ItemMetaSerializer());
        serdesRegistry.register(new CraftingSerializer());
    }
}
