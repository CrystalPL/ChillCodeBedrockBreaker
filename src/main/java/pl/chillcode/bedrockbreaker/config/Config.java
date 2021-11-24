package pl.chillcode.bedrockbreaker.config;

import de.tr7zw.nbtapi.NBTItem;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import pl.crystalek.crcapi.Recipe;
import pl.crystalek.crcapi.config.ConfigParserUtil;
import pl.crystalek.crcapi.config.exception.ConfigLoadException;
import pl.crystalek.crcapi.util.NumberUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
public final class Config {
    final FileConfiguration config;
    final JavaPlugin plugin;
    int minimumHeightToBreakBedrock;
    int maximumHeightToBreakBedrock;
    ItemStack breakTool;
    List<String> breakToolLore;
    String breakToolName;
    int useAmount;
    boolean repairItemInAnvil;
    int subtractedValue;
    boolean breakOnlyBedrock;

    String bedrockBreakerCommand;
    List<String> bedrockBreakerCommandAliases;

    public boolean load() {
        this.bedrockBreakerCommand = config.getString("command.bedrockBreaker.name");
        this.bedrockBreakerCommandAliases = Arrays.asList(config.getString("command.bedrockBreaker.aliases").split(", "));

        this.breakOnlyBedrock = config.getBoolean("breakOnlyBedrock");

        final Optional<Integer> minimumHeightToBreakBedrockOptional = NumberUtil.getInt(config.get("minimumHeightToBreakBedrock"));
        if (!minimumHeightToBreakBedrockOptional.isPresent() || minimumHeightToBreakBedrockOptional.get() < 0) {
            Bukkit.getLogger().severe("Wartość pola minimumHeightToBreakBedrock musi być liczbą z zakresu <0, 2_147_483_468>!");
            return false;
        }
        this.minimumHeightToBreakBedrock = minimumHeightToBreakBedrockOptional.get();

        final Optional<Integer> maximumHeightToBreakBedrock = NumberUtil.getInt(config.get("maximumHeightToBreakBedrock"));
        if (!maximumHeightToBreakBedrock.isPresent() || maximumHeightToBreakBedrock.get() < 0) {
            Bukkit.getLogger().severe("Wartość pola maximumHeightToBreakBedrock musi być liczbą z zakresu <0, 2_147_483_468>!");
            return false;
        }
        this.maximumHeightToBreakBedrock = maximumHeightToBreakBedrock.get();

        final Optional<Integer> useAmountOptional = NumberUtil.getInt(config.get("useAmount"));
        if (!useAmountOptional.isPresent() || useAmountOptional.get() < 0) {
            Bukkit.getLogger().severe("Wartość pola useAmount musi być liczbą z zakresu <1, 2_147_483_468>!");
            return false;
        }
        this.useAmount = useAmountOptional.get();

        try {
            final ItemStack breakTool = ConfigParserUtil.getItem(config.getConfigurationSection("breakTool"));
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
            this.breakTool = breakTool;

            final NBTItem nbtItem = new NBTItem(this.breakTool, true);
            nbtItem.setBoolean("bedrockBreakerItem", true);
            nbtItem.setInteger("bedrockBreakerUseAmount", useAmount);
        } catch (final ConfigLoadException exception) {
            Bukkit.getLogger().severe("Wystąpił błąd podczas ładowania sekcji breakTool!");
            Bukkit.getLogger().severe(exception.getMessage());
            return false;
        }

        this.repairItemInAnvil = config.getBoolean("repairItemInAnvil");

        final Recipe recipe = new Recipe("bedrock_breaker", breakTool);
        try {
            ConfigParserUtil.getRecipe(config.getConfigurationSection("crafting"), recipe);
        } catch (final ConfigLoadException exception) {
            Bukkit.getLogger().severe("Wystąpił błąd podczas ładowania receptury z pola crafting!");
            Bukkit.getLogger().severe(exception.getMessage());
            return false;
        }
        recipe.registerRecipe(plugin);

        this.subtractedValue = breakTool.getType().getMaxDurability() / useAmount;

        return true;
    }
}
