package pl.chillcode.bedrockbreaker.listener;

import de.tr7zw.nbtapi.NBTItem;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.crystalek.crcapi.message.MessageAPI;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public final class InventoryClickListener implements Listener {
    MessageAPI messageAPI;

    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.ANVIL) {
            return;
        }

        final ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null) {
            return;
        }

        final Inventory inventory = event.getInventory();
        final ItemStack slot1 = inventory.getItem(0);
        final ItemStack slot2 = inventory.getItem(1);
        if (slot1 == null || slot2 == null) {
            return;
        }

        if (slot1.getType() == Material.AIR || slot2.getType() == Material.AIR || !new NBTItem(slot1).getBoolean("bedrockBreakerItem") || new NBTItem(slot2).getBoolean("bedrockBreakerItem")) {
            return;
        }

        event.setResult(Event.Result.DENY);
        event.setCancelled(true);
        messageAPI.sendMessage("noRepairInAnvil", event.getWhoClicked());
    }
}
