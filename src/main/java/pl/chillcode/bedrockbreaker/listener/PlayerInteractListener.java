package pl.chillcode.bedrockbreaker.listener;

import de.tr7zw.nbtapi.NBTItem;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.chillcode.bedrockbreaker.config.Config;
import pl.chillcode.bedrockbreaker.cooldown.Cooldown;
import pl.crystalek.crcapi.core.time.TimeUtil;
import pl.crystalek.crcapi.message.api.MessageAPI;
import pl.crystalek.crcapi.message.api.replacement.Replacement;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public final class PlayerInteractListener implements Listener {
    Cooldown cooldown;
    Config config;
    MessageAPI messageAPI;

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        final ItemStack eventItem = event.getItem();
        if (eventItem == null) {
            return;
        }

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (!config.isBreakOnlyBedrock()) {
                return;
            }

            final Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) {
                return;
            }

            final NBTItem eventItemNBT = new NBTItem(eventItem, true);
            if (eventItemNBT.getBoolean("bedrockBreakerItem")) {
                messageAPI.sendMessage("breakOnlyBedrock", event.getPlayer());
                event.setCancelled(true);
                return;
            }
        } else if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        final Block clickedBlock = event.getClickedBlock();
        if (clickedBlock.getType() != Material.BEDROCK) {
            return;
        }

        final NBTItem eventItemNBT = new NBTItem(eventItem, true);
        if (!eventItemNBT.getBoolean("bedrockBreakerItem")) {
            return;
        }

        final Player player = event.getPlayer();
        final int y = clickedBlock.getY();
        if (y < config.getMinimumHeightToBreakBedrock() || y > config.getMaximumHeightToBreakBedrock()) {
            messageAPI.sendMessage("breakBedrockOutOfScope", player);
            return;
        }

        final Instant lastBedrockBreakerUse = cooldown.getCooldown(player.getUniqueId());
        if (lastBedrockBreakerUse != null) {
            final Duration timeBetweenUse = Duration.between(Instant.now(), lastBedrockBreakerUse).abs();
            if (timeBetweenUse.compareTo(config.getCooldownTime()) < 0) {
                final String formattedTime = TimeUtil.getFormattedTime(config.getCooldownTime().minus(timeBetweenUse).toMillis(), config.getDelimiter(), config.isShortFormTime());
                messageAPI.sendMessage("cooldownBreakTime", player, Replacement.of("{TIME}", formattedTime));
                return;
            }
        }

        cooldown.addCooldown(player.getUniqueId());

        final Integer bedrockBreakerUseAmount = eventItemNBT.getInteger("bedrockBreakerUseAmount");
        eventItemNBT.setInteger("bedrockBreakerUseAmount", bedrockBreakerUseAmount - 1);
        eventItem.setDurability((short) (eventItem.getDurability() + config.getSubtractedValue()));
        final String ActualUseAmount = String.valueOf(bedrockBreakerUseAmount - 1);
        final String maxUseAmount = String.valueOf(config.getUseAmount());

        if (bedrockBreakerUseAmount - 1 <= 0) {
            player.getInventory().remove(eventItem);
            messageAPI.sendMessage("breakIfLastUsage", player);
        } else {
            final ItemMeta itemMeta = eventItem.getItemMeta();
            final String displayName = config.getBreakToolName()
                    .replace("{USE_AMOUNT}", ActualUseAmount)
                    .replace("{MAX_USE_AMOUNT}", maxUseAmount
                    );
            final List<String> breakToolLore = new ArrayList<>(config.getBreakToolLore());
            breakToolLore.replaceAll(lore -> lore
                    .replace("{USE_AMOUNT}", ActualUseAmount)
                    .replace("{MAX_USE_AMOUNT}", maxUseAmount
                    ));

            itemMeta.setDisplayName(displayName);
            itemMeta.setLore(breakToolLore);
            eventItem.setItemMeta(itemMeta);
            messageAPI.sendMessage(
                    "removeBedrock", player,
                    Replacement.of("{USE_AMOUNT}", ActualUseAmount),
                    Replacement.of("{MAX_USE_AMOUNT}", maxUseAmount));
        }

        clickedBlock.setType(Material.AIR);
    }
}
