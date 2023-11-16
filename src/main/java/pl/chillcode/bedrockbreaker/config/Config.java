package pl.chillcode.bedrockbreaker.config;

import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import pl.crystalek.crcapi.command.impl.Command;
import pl.crystalek.crcapi.command.loader.CommandLoader;
import pl.crystalek.crcapi.command.model.CommandData;
import pl.crystalek.crcapi.core.Recipe;
import pl.crystalek.crcapi.core.config.ConfigHelper;
import pl.crystalek.crcapi.core.config.ConfigParserUtil;
import pl.crystalek.crcapi.core.config.exception.ConfigLoadException;
import pl.crystalek.crcapi.core.util.ColorUtil;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public final class Config extends ConfigHelper {
    Map<Class<? extends Command>, CommandData> commandDataMap;
    String delimiter;
    String timeDelimiter;
    boolean shortFormTime;
    int minimumHeightToBreakBedrock;
    int maximumHeightToBreakBedrock;
    ItemStack breakTool;
    List<String> breakToolLore;
    String breakToolName;
    int useAmount;
    boolean repairItemInAnvil;
    int subtractedValue;
    boolean breakOnlyBedrock;
    Duration cooldownTime;

    public Config(final JavaPlugin plugin, final String fileName) {
        super(plugin, fileName);
    }

    public void reloadConfig() throws ConfigLoadException {
        this.delimiter = ColorUtil.color(ConfigParserUtil.getString(configuration, "delimiter"));
        this.timeDelimiter = ColorUtil.color(ConfigParserUtil.getString(configuration, "timeDelimiter"));
        this.shortFormTime = ConfigParserUtil.getBoolean(configuration, "shortFormTime");
        this.minimumHeightToBreakBedrock = ConfigParserUtil.getInt(configuration, "minimumHeightToBreakBedrock");
        this.maximumHeightToBreakBedrock = ConfigParserUtil.getInt(configuration, "maximumHeightToBreakBedrock");
        this.useAmount = ConfigParserUtil.getInt(configuration, "useAmount");
        this.repairItemInAnvil = ConfigParserUtil.getBoolean(configuration, "repairItemInAnvil");
        this.breakOnlyBedrock = ConfigParserUtil.getBoolean(configuration, "breakOnlyBedrock");
        this.cooldownTime = Duration.ofSeconds(ConfigParserUtil.getInt(configuration, "cooldownTime", time -> time > 0));

        try {
            this.breakTool = ConfigParserUtil.getItem(configuration.getConfigurationSection("breakTool"));

            this.breakToolName = breakTool.getItemMeta().getDisplayName();
            this.breakToolLore = breakTool.getItemMeta().getLore() != null ? breakTool.getItemMeta().getLore() : new ArrayList<>();
            final ItemMeta itemMeta = breakTool.getItemMeta();
            final List<String> lore = itemMeta.getLore();
            if (lore != null) {
                this.breakToolLore = new ArrayList<>(lore);
                lore.replaceAll(x -> x
                        .replace("{USE_AMOUNT}", String.valueOf(useAmount))
                        .replace("{MAX_USE_AMOUNT}", String.valueOf(useAmount))
                );

                itemMeta.setLore(lore);
            } else {
                this.breakToolLore = new ArrayList<>();
            }

            itemMeta.setDisplayName(itemMeta.getDisplayName()
                    .replace("{USE_AMOUNT}", String.valueOf(useAmount))
                    .replace("{MAX_USE_AMOUNT}", String.valueOf(useAmount))
            );

            breakTool.setItemMeta(itemMeta);

            final NBTItem nbtItem = new NBTItem(this.breakTool, true);
            nbtItem.setBoolean("bedrockBreakerItem", true);
            nbtItem.setInteger("bedrockBreakerUseAmount", useAmount);
        } catch (final ConfigLoadException exception) {
            plugin.getLogger().severe("Wystąpił błąd podczas ładowania sekcji breakTool!");
            throw new ConfigLoadException(exception.getMessage());
        }
        this.subtractedValue = breakTool.getType().getMaxDurability() / useAmount;

        final Recipe recipe = new Recipe("bedrock_breaker", breakTool);
        try {
            ConfigParserUtil.getRecipe(configuration.getConfigurationSection("crafting"), recipe);
        } catch (final ConfigLoadException exception) {
            plugin.getLogger().severe("Wystąpił błąd podczas ładowania receptury z pola crafting!");
            throw new ConfigLoadException(exception.getMessage());
        }

        recipe.registerRecipe(plugin);
    }

    public void loadConfig() throws ConfigLoadException {
        this.commandDataMap = CommandLoader.loadCommands(configuration.getConfigurationSection("command"), plugin.getClass().getClassLoader());
        reloadConfig();
    }
}
