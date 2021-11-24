package pl.chillcode.bedrockbreaker.listener;

import com.google.common.collect.ImmutableMap;
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
import pl.crystalek.crcapi.message.MessageAPI;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public final class PlayerInteractListener implements Listener {
    Cooldown cooldown = new Cooldown();
    Config config;
    MessageAPI messageAPI;

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (!config.isBreakOnlyBedrock()) {
                return;
            }

            final Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) {
                return;
            }

            final NBTItem eventItemNBT = new NBTItem(event.getItem(), true);
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

        final ItemStack eventItem = event.getItem();
        if (eventItem == null) {
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


        final Long coolDownTime = cooldown.getCooldown(player.getUniqueId());
        if (coolDownTime != null && System.currentTimeMillis() - coolDownTime < 500) {
            messageAPI.sendMessage("cooldownBreakTime", player, ImmutableMap.of("{TIME}", System.currentTimeMillis() - coolDownTime));
            return;
        }

        cooldown.addColdown(player.getUniqueId());

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
            messageAPI.sendMessage("removeBedrock", player, ImmutableMap.of("{USE_AMOUNT}", ActualUseAmount, "{MAX_USE_AMOUNT}", maxUseAmount));
        }

        clickedBlock.setType(Material.AIR);
    }
}
