package pl.chillcode.chillbedrockbreaker.crafting;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public final class Crafting {
    Material slot1;
    Material slot2;
    Material slot3;
    Material slot4;
    Material slot5;
    Material slot6;
    Material slot7;
    Material slot8;
    Material slot9;

    public ShapedRecipe getRecipe(final JavaPlugin javaPlugin, final ItemStack resultItem) {
        ShapedRecipe recipe;
        try {
            Class.forName("org.bukkit.NamespacedKey");

            final NamespacedKey bedrock_breaker = new NamespacedKey(javaPlugin, "bedrock_breaker");
            recipe = new ShapedRecipe(bedrock_breaker, resultItem);
        } catch (final ClassNotFoundException exception) {
            recipe = new ShapedRecipe(resultItem);
        }

        recipe.shape("abc", "def", "ghi");

        setItem(recipe, slot1, 'a');
        setItem(recipe, slot2, 'b');
        setItem(recipe, slot3, 'c');
        setItem(recipe, slot4, 'd');
        setItem(recipe, slot5, 'e');
        setItem(recipe, slot6, 'f');
        setItem(recipe, slot7, 'g');
        setItem(recipe, slot8, 'h');
        setItem(recipe, slot9, 'i');

        return recipe;
    }

    private void setItem(final ShapedRecipe recipe, final Material material, final char character) {
        if (material != Material.AIR) {
            recipe.setIngredient(character, material);
        }
    }
}
