package pl.chillcode.chillbedrockbreaker;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.chillbedrockbreaker.config.Config;
import pl.chillcode.chillbedrockbreaker.item.ItemBuilder;
import pl.chillcode.chillbedrockbreaker.listener.InventoryClickListener;
import pl.chillcode.chillbedrockbreaker.listener.PlayerInteractListener;

public final class ChillBedrockBreaker extends JavaPlugin implements CommandExecutor {
    Config config;

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final ItemStack breakTool = config.getBreakTool();
        ((Player) sender).getInventory().addItem(breakTool);
        return true;
    }

    @Override
    public void onEnable() {
        config = Config.initConfig(this);
        if (config == null) {
            return;
        }

        //register listeners
        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerInteractListener(config), this);

        if (!config.isRepairItemInAnvil()) {
            pluginManager.registerEvents(new InventoryClickListener(config), this);
        }

        //register crafting
        final ShapedRecipe recipe = config.getCrafting().getRecipe(this, config.getBreakTool());
        Bukkit.addRecipe(recipe);

        getCommand("xd").setExecutor(this);
    }
}
