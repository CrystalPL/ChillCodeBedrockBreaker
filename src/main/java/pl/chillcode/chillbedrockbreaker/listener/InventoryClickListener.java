package pl.chillcode.chillbedrockbreaker.listener;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import pl.chillcode.chillbedrockbreaker.config.Config;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class InventoryClickListener implements Listener {
    Config config;

    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        final ItemStack currentItem = event.getCurrentItem();

        if (currentItem == null) {
            return;
        }

        if (currentItem.getType() == Material.AIR) {
            return;
        }

        if (event.getInventory().getType() != InventoryType.ANVIL) {
            return;
        }

        if (currentItem.isSimilar(config.getBreakTool())) {
            event.setCancelled(true);
            //nie możesz naprawić tego przedmiotu w kowadle
        }
    }
}
