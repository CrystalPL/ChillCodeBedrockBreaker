package pl.chillcode.chillbedrockbreaker.config.serializer;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.SerdesContext;
import eu.okaeri.configs.serdes.TwoSideObjectTransformer;
import org.bukkit.Material;

final class MaterialTransformer extends TwoSideObjectTransformer<String, Material> {
    @Override
    public GenericsPair<String, Material> getPair() {
        return this.genericsPair(String.class, Material.class);
    }

    @Override
    public Material leftToRight(final String material, final SerdesContext serdesContext) {
        return Material.valueOf(material.toUpperCase());
    }

    @Override
    public String rightToLeft(final Material material, final SerdesContext serdesContext) {
        return material.name();
    }
}
