package mathax.client.legacy.systems.modules.player;

import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import net.minecraft.item.Items;

public class Portals extends Module {
    public Portals() {
        super(Categories.Player, Items.OBSIDIAN, "portals", "Allows you to use GUIs normally while in a Nether Portal.");
    }
}
