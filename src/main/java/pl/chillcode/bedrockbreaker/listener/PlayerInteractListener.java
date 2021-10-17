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
import pl.chillcode.bedrockbreaker.config.Config;
import pl.chillcode.bedrockbreaker.cooldown.Cooldown;
import pl.crystalek.crcapi.message.MessageAPI;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public final class PlayerInteractListener implements Listener {
    Config config;
    Cooldown cooldown = new Cooldown();

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
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
            MessageAPI.sendMessage("breakBedrockOutOfScope", player);
            return;
        }


        final Long coolDownTime = cooldown.getCooldown(player.getUniqueId());
        if (coolDownTime != null && System.currentTimeMillis() - coolDownTime < 500) {
            MessageAPI.sendMessage("cooldownBreakTime", player, ImmutableMap.of("{TIME}", System.currentTimeMillis() - coolDownTime));
            return;
        }

        cooldown.addColdown(player.getUniqueId());

        final Integer bedrockBreakerUseAmount = eventItemNBT.getInteger("bedrockBreakerUseAmount");
        eventItemNBT.setInteger("bedrockBreakerUseAmount", bedrockBreakerUseAmount - 1);
        eventItem.setDurability((short) (eventItem.getDurability() + config.getSubtracedValue()));
        if (bedrockBreakerUseAmount - 1 <= 0) {
            player.getInventory().remove(eventItem);
        }

        clickedBlock.setType(Material.AIR);
        MessageAPI.sendMessage("removeBedrock", player);
    }
}