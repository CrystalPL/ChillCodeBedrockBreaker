package pl.chillcode.chillbedrockbreaker.util;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public final class ColorUtil {

    private ColorUtil() {
    }

    public static String fixColor(final String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> fixColor(final List<String> list) {
        return list.stream().map(ColorUtil::fixColor).collect(Collectors.toList());
    }
}