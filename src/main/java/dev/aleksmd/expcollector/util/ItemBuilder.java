package dev.aleksmd.expcollector.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class ItemBuilder {
    @NotNull
    private final ItemStack item;
    @NotNull
    private final ItemMeta meta;

    public ItemBuilder(@Nullable Material material, int amount) {
        if (material == null) {
            throw new IllegalArgumentException("Material cannot be null");
        }
        this.item = new ItemStack(material, amount);
        this.meta = this.item.getItemMeta();
        if (this.meta == null) {
            throw new IllegalStateException("ItemMeta cannot be null");
        }
    }

    @NotNull
    public ItemBuilder setName(@NotNull String value, @NotNull Map<String, String> args) {
        for (String key : args.keySet()) {
            String replacement = args.get(key);
            if (replacement != null) {
                value = value.replace(key, replacement);
            }
        }
        this.meta.setDisplayName(Hex.INSTANCE.color(value));
        return this;
    }

    @NotNull
    public ItemBuilder setLore(@NotNull List<String> lore, @NotNull Map<String, String> args) {
        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);
            for (String key : args.keySet()) {
                String replacement = args.get(key);
                if (replacement != null) {
                    line = line.replace(key, replacement);
                }
            }
            lore.set(i, Hex.INSTANCE.color(line));
        }
        this.meta.setLore(lore);
        return this;
    }

    @Nullable
    public <P, T> ItemBuilder addPDC(@NotNull String key, @NotNull PersistentDataType<P, T> dataType, @NotNull T value) {
        if (key == null || dataType == null) {
            throw new IllegalArgumentException("Key or dataType cannot be null");
        }
        PersistentDataContainer pdc = this.meta.getPersistentDataContainer();
        NamespacedKey namespacedKey = NamespacedKey.fromString(key);
        if (namespacedKey != null) {
            pdc.set(namespacedKey, dataType, value);
        }
        return this;
    }

    @NotNull
    public ItemStack build() {
        this.item.setItemMeta(this.meta);
        return this.item;
    }
}
