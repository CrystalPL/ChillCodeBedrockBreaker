package pl.chillcode.chillbedrockbreaker.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.chillcode.chillbedrockbreaker.util.ColorUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
public final class ItemBuilder {
    Material material;
    int amount = 1;
    short data;
    String name;
    List<String> lore;
    List<Enchant> enchantList;
    List<ItemFlag> itemFlagList;

    public static ItemBuilder builder() {
        return new ItemBuilder();
    }

    public ItemBuilder wrap(final ItemStack itemStack) {
        this.material = itemStack.getType();
        this.amount = itemStack.getAmount();
        this.data = itemStack.getDurability();
        return this;
    }

    public ItemBuilder material(final Material material) {
        this.material = material;
        return this;
    }

    public ItemBuilder amount(final int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder data(final short data) {
        this.data = data;
        return this;
    }

    public ItemBuilder name(final String name) {
        this.name = ColorUtil.fixColor(name);
        return this;
    }

    public ItemBuilder lore(final List<String> lore) {
        this.lore = lore;
        return this;
    }

    public ItemBuilder lore(final String lore) {
        if (this.lore == null) {
            this.lore = new ArrayList<>();
        }

        this.lore.add(ColorUtil.fixColor(lore));
        return this;
    }

    public ItemBuilder lore(final String... lore) {
        this.lore = Arrays.asList(lore);
        return this;
    }

    public ItemBuilder enchant(final List<Enchant> enchantList) {
        this.enchantList = enchantList;
        return this;
    }

    public ItemBuilder enchant(final Enchant enchant) {
        if (this.enchantList == null) {
            this.enchantList = new ArrayList<>();
        }

        this.enchantList.add(enchant);
        return this;
    }

    public ItemBuilder enchant(final Enchant... enchants) {
        this.enchantList = Arrays.asList(enchants);
        return this;
    }

    public ItemBuilder enchant(final String enchantName, final int level) {
        return this.enchant(Enchant.builder().enchant(enchantName).level(level));
    }

    public ItemBuilder enchant(final Enchantment enchantment, final int level) {
        return this.enchant(Enchant.builder().enchant(enchantment).level(level));
    }

    public ItemBuilder flag(final List<ItemFlag> itemFlagList) {
        this.itemFlagList = itemFlagList;
        return this;
    }

    public ItemBuilder hideAttributes() {
        this.itemFlagList = Arrays.asList(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        return this;
    }

    public ItemBuilder flag(final ItemFlag... flags) {
        this.itemFlagList = Arrays.asList(flags);
        return this;
    }

    public ItemBuilder flag(final ItemFlag itemFlag) {
        if (this.itemFlagList == null) {
            this.itemFlagList = new ArrayList<>();
        }

        this.itemFlagList.add(itemFlag);
        return this;
    }

    public ItemStack build() {
        final ItemStack itemStack = new ItemStack(material, amount, (short) (material.getMaxDurability() - data));

        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (name != null) {
            itemMeta.setDisplayName(ColorUtil.fixColor(name));
        }

        if (lore != null) {
            itemMeta.setLore(ColorUtil.fixColor(lore));
        }

        if (itemFlagList != null) {
            itemMeta.addItemFlags(itemFlagList.toArray(new ItemFlag[0]));
        }

        itemStack.setItemMeta(itemMeta);

        if (enchantList != null) {
            itemMeta.getEnchants().keySet().forEach(itemStack::removeEnchantment);
            enchantList.forEach(enchant -> enchant.apply(itemStack));
        }

        return itemStack;
    }

}
