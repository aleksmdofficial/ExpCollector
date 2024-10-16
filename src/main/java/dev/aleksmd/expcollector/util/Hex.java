package dev.aleksmd.expcollector.util;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Hex {
    @NotNull
    public static final Hex INSTANCE = new Hex();

    private Hex() {
    }

    @NotNull
    public static String color(@Nullable String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        String result = text;
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(result);
        while (matcher.find()) {
            String hexCode = result.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');
            StringBuilder builder = new StringBuilder();
            replaceSharp.chars().forEach(c -> {
                builder.append("&").append((char) c);
            });
            result = result.replace(hexCode, builder.toString());
            matcher = pattern.matcher(result);
        }
        result = ChatColor.translateAlternateColorCodes('&', result);
        return result.replace("&", "");
    }

    public void send(@NotNull CommandSender sender, @Nullable String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        sender.sendMessage(this.color(message));
    }

    @NotNull
    public List<String> color(@NotNull List<String> text) {
        List<String> coloredTexts = new ArrayList<>(text.size());
        for (String line : text) {
            coloredTexts.add(this.color(line));
        }
        return coloredTexts;
    }
}
