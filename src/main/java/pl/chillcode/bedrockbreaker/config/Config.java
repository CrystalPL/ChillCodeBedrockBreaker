package pl.chillcode.bedrockbreaker.config;

import de.tr7zw.nbtapi.NBTItem;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import pl.crystalek.crcapi.config.ConfigParserUtil;
import pl.crystalek.crcapi.config.exception.ConfigLoadException;
import pl.crystalek.crcapi.util.LogUtil;
import pl.crystalek.crcapi.util.NumberUtil;

import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
public final class Config {
    final FileConfiguration config;
    int minimumHeightToBreakBedrock;
    int maximumHeightToBreakBedrock;
    ItemStack breakTool;
    int useAmount;
    boolean repairItemInAnvil;
    int subtractedValue;

    public boolean load() {
        final Optional<Integer> minimumHeightToBreakBedrockOptional = NumberUtil.getInt(config.get("minimumHeightToBreakBedrock"));
        if (!minimumHeightToBreakBedrockOptional.isPresent() || minimumHeightToBreakBedrockOptional.get() < 0) {
            LogUtil.error("Wartość pola minimumHeightToBreakBedrock musi być liczbą z zakresu <0, 2_147_483_468>!");
            return false;
        }
        this.minimumHeightToBreakBedrock = minimumHeightToBreakBedrockOptional.get();

        final Optional<Integer> maximumHeightToBreakBedrock = NumberUtil.getInt(config.get("maximumHeightToBreakBedrock"));
        if (!maximumHeightToBreakBedrock.isPresent() || maximumHeightToBreakBedrock.get() < 0) {
            LogUtil.error("Wartość pola maximumHeightToBreakBedrock musi być liczbą z zakresu <0, 2_147_483_468>!");
            return false;
        }
        this.maximumHeightToBreakBedrock = maximumHeightToBreakBedrock.get();

        final Optional<Integer> useAmountOptional = NumberUtil.getInt(config.get("useAmount"));
        if (!useAmountOptional.isPresent() || useAmountOptional.get() < 0) {
            LogUtil.error("Wartość pola useAmount musi być liczbą z zakresu <1, 2_147_483_468>!");
            return false;
        }
        this.useAmount = useAmountOptional.get();

        try {
            this.breakTool = ConfigParserUtil.getItem(config.getConfigurationSection("breakTool"));

            final NBTItem nbtItem = new NBTItem(breakTool, true);
            nbtItem.setBoolean("bedrockBreakerItem", true);
            nbtItem.setInteger("bedrockBreakerUseAmount", useAmount);
        } catch (final ConfigLoadException exception) {
            LogUtil.error("Wystąpił błąd podczas ładowania sekcji breakTool!");
            LogUtil.error(exception.getMessage());
            return false;
        }

        this.repairItemInAnvil = config.getBoolean("repairItemInAnvil");

        try {
            ConfigParserUtil.getRecipe(config.getConfigurationSection("crafting")).recipeName("bedrock_breaker").resultItem(breakTool).build().registerRecipe();
        } catch (final ConfigLoadException exception) {
            LogUtil.error("Wystąpił błąd podczas ładowania receptury z pola crafting!");
            LogUtil.error(exception.getMessage());
            return false;
        }

        this.subtractedValue = breakTool.getType().getMaxDurability() / useAmount;

        return true;
    }
}
