package pl.chillcode.chillbedrockbreaker.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Enchant {
    Enchantment enchantment;
    int level = 1;

    public static Enchant builder() {
        return new Enchant();
    }

    public Enchant enchant(final Enchantment enchantment) {
        this.enchantment = enchantment;
        return this;
    }

    public Enchant enchant(final String name) {
        this.enchantment = Enchantment.getByName(name);
        return this;
    }

    public Enchant level(final int level) {
        this.level = level;
        return this;
    }

    public void apply(final ItemStack itemStack) {
        itemStack.addUnsafeEnchantment(enchantment, level);
    }
}
