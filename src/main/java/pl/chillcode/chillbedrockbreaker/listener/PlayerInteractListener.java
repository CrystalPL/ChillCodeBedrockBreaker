package pl.chillcode.chillbedrockbreaker.listener;

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
import pl.chillcode.chillbedrockbreaker.config.Config;
import pl.chillcode.chillbedrockbreaker.cooldown.Cooldown;

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

        final ItemStack breakTool = config.getBreakTool();
        final ItemStack clone = eventItem.clone();
        clone.setDurability(breakTool.getDurability());

        if (!clone.isSimilar(breakTool)) {
            return;
        }

        final int y = clickedBlock.getY();
        if (y < config.getMinimumHeightToBreakBedrock() || y > config.getMaximumHeightToBreakBedrock()) {
            //nie mozesz niszczyc bedrocka w tym miejscu
            return;
        }

        final Player player = event.getPlayer();

        final Long coolDownTime = cooldown.getCooldown(player.getUniqueId());
        if (coolDownTime != null && System.currentTimeMillis() - coolDownTime < 500) {
            //musisz odczekać {TIME} przed nastepnym usunieciem bedrocka
            return;
        }

        cooldown.addColdown(player.getUniqueId());

        eventItem.setDurability((short) (eventItem.getDurability() + 5));
        if (eventItem.getType().getMaxDurability() - eventItem.getDurability() <= 0) {
            player.getInventory().remove(eventItem);
        }

        clickedBlock.setType(Material.AIR);
        //pomyślnie usunięto bedrock
    }
}
