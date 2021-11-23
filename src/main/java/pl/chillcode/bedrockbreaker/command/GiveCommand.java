package pl.chillcode.bedrockbreaker.command;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.chillcode.bedrockbreaker.config.Config;
import pl.crystalek.crcapi.message.MessageAPI;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class GiveCommand extends Command {
    MessageAPI messageAPI;
    Config config;

    public GiveCommand(final Config config, final MessageAPI messageAPI) {
        super(config.getBedrockBreakerCommand());
        setAliases(config.getBedrockBreakerCommandAliases());

        this.config = config;
        this.messageAPI = messageAPI;
    }

    @Override
    public boolean execute(final CommandSender sender, final String commandLabel, final String[] args) {
        if (!sender.hasPermission("chillcode.bedrockbreaker.give")) {
            messageAPI.sendMessage("noPermission", sender, ImmutableMap.of("{PERMISSION}", "chillcode.bedrockbreaker.give"));
            return true;
        }

        if (!(sender instanceof Player)) {
            messageAPI.sendMessage("noConsole", sender);
            return true;
        }

        if (args.length != 0) {
            messageAPI.sendMessage("commandUsage", sender);
            return true;
        }

        ((Player) sender).getInventory().addItem(config.getBreakTool());
        messageAPI.sendMessage("giveItem", sender);
        return false;
    }
}
