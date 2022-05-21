package pl.chillcode.bedrockbreaker.command;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.chillcode.bedrockbreaker.config.Config;
import pl.crystalek.crcapi.command.impl.SingleCommand;
import pl.crystalek.crcapi.command.model.CommandData;
import pl.crystalek.crcapi.message.api.MessageAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class GiveCommand extends SingleCommand {
    Config config;

    public GiveCommand(final MessageAPI messageAPI, final Map<Class<? extends SingleCommand>, CommandData> commandDataMap, final Config config) {
        super(messageAPI, commandDataMap);

        this.config = config;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        ((Player) sender).getInventory().addItem(config.getBreakTool());
        messageAPI.sendMessage("giveItem", sender);
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return new ArrayList<>();
    }

    @Override
    public String getPermission() {
        return "chillcode.bedrockbreaker.give";
    }

    @Override
    public boolean isUseConsole() {
        return false;
    }

    @Override
    public String getCommandUsagePath() {
        return "commandUsage";
    }

    @Override
    public int maxArgumentLength() {
        return 0;
    }

    @Override
    public int minArgumentLength() {
        return 0;
    }
}
