package pl.chillcode.chillbedrockbreaker.config;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.chillbedrockbreaker.config.serializer.Serdes;
import pl.chillcode.chillbedrockbreaker.crafting.Crafting;
import pl.chillcode.chillbedrockbreaker.item.ItemBuilder;

import java.io.File;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Config extends OkaeriConfig {

    @Comment("od którego poziomu włącznie, może być niszczony bedrock")
    int minimumHeightToBreakBedrock = 1;

    @Comment("")
    @Comment("do którego poziomu włącznie, może być niszczony bedrock")
    int maximumHeightToBreakBedrock = 256;

    @Comment("")
    @Comment("narzędzie, którym będzie niszczony bedrock")
    ItemStack breakTool = defaultBreakTool();

    @Comment("")
    @Comment("ilość użyć narzędzia")
    int useAmount = 20;

    @Comment("")
    @Comment("czy narzędzie niszczące bedrock może być naprawiane w kowadle")
    boolean repairItemInAnvil = false;

    @Comment("")
    @Comment("receptura narzędzia niszczącego bedrock")
    @Comment("1 | 2 | 3")
    @Comment("4 | 5 | 6")
    @Comment("7 | 8 | 9")
    Crafting crafting = new Crafting(Material.GOLD_BLOCK, Material.GOLD_BLOCK, Material.GOLD_BLOCK, Material.AIR, Material.GOLD_BLOCK, Material.AIR, Material.AIR, Material.GOLD_BLOCK, Material.AIR);

    public static Config initConfig(final JavaPlugin plugin) {
        try {
            return ConfigManager.create(Config.class, (it) -> {
                it.withConfigurer(new YamlBukkitConfigurer(), new Serdes());
                it.withBindFile(new File(plugin.getDataFolder(), "config.yml"));
                it.saveDefaults();
                it.load(true);
            });
        } catch (final Exception exception) {
            plugin.getLogger().severe("Error loading config.yml");
            plugin.getPluginLoader().disablePlugin(plugin);
            exception.printStackTrace();
            return null;
        }
    }

    private ItemStack defaultBreakTool() {
        Material material;

        try {
            material = Material.GOLD_SPADE;
        } catch (final NoSuchFieldError exception) {
            material = Material.valueOf("GOLDEN_SHOVEL");
        }

        final ItemStack build = ItemBuilder.builder().material(material).data((short) 15).name("&2&lŁopata niszcząca bedrock").build();

        return build;
    }
}
