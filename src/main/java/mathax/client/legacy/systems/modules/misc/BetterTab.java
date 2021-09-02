package mathax.client.legacy.systems.modules.misc;

import mathax.client.legacy.renderer.GL;
import mathax.client.legacy.renderer.Renderer2D;
import mathax.client.legacy.settings.*;
import mathax.client.legacy.systems.config.Config;
import mathax.client.legacy.systems.enemies.Enemies;
import mathax.client.legacy.systems.enemies.Enemy;
import mathax.client.legacy.systems.modules.Module;
import mathax.client.legacy.systems.friends.Friend;
import mathax.client.legacy.systems.friends.Friends;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.utils.render.color.Color;
import mathax.client.legacy.utils.render.color.SettingColor;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class BetterTab extends Module {
    private static final Identifier mathaxLogo = new Identifier("mathaxlegacy", "textures/logo/logo.png");

    private Color textureColor = new Color(255, 255, 255, 255);

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Integer> tabSize = sgGeneral.add(new IntSetting.Builder()
        .name("tablist-size")
        .description("Bypasses the 80 player limit on the tablist.")
        .defaultValue(1000)
        .min(1)
        .sliderMax(1000)
        .sliderMin(1)
        .build()
    );

    private final Setting<Boolean> self = sgGeneral.add(new BoolSetting.Builder()
        .name("highlight-self")
        .description("Highlights yourself in the tablist.")
        .defaultValue(true)
        .build()
    );

    private final Setting<SettingColor> selfColor = sgGeneral.add(new ColorSetting.Builder()
        .name("self-color")
        .description("The color to highlight your name with.")
        .defaultValue(new SettingColor(0, 165, 255))
        .visible(self::get)
        .build()
    );

    private final Setting<Boolean> friends = sgGeneral.add(new BoolSetting.Builder()
        .name("highlight-friends")
        .description("Highlights friends in the tablist.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> enemies = sgGeneral.add(new BoolSetting.Builder()
        .name("highlight-enemies")
        .description("Highlights enemies in the tablist.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> accurateLatency = sgGeneral.add(new BoolSetting.Builder()
        .name("accurate-latency")
        .description("Shows latency as a number in the tablist.")
        .defaultValue(true)
        .build()
    );

    public BetterTab() {
        super(Categories.Misc, "better-tab", "Various improvements to the tab list.");
    }

    public Text getPlayerName(PlayerListEntry playerListEntry) {
        Text name;
        Color color = null;

        name = playerListEntry.getDisplayName();
        if (name == null) {
            /*if ((playerListEntry.getProfile().getId().toString().equals("3e24ef27-e66d-45d2-bf4b-2c7ade68ff47") || playerListEntry.getProfile().getId().toString().equals("7c73f844-73c3-3a7d-9978-004ba0a6436e")) && Config.get().viewMatHaxLegacyUsers)
                name = new LiteralText("   " + playerListEntry.getProfile().getName());
            else*/
                name = new LiteralText(playerListEntry.getProfile().getName());
        }

        if (playerListEntry.getProfile().getId().toString().equals(mc.player.getGameProfile().getId().toString()) && self.get()) {
            color = selfColor.get();
        }
        else if (friends.get() && Friends.get().get(playerListEntry.getProfile().getName()) != null) {
            Friend friend = Friends.get().get(playerListEntry.getProfile().getName());
            if (friend != null) color = Friends.get().color;
        }
        else if (enemies.get() && Enemies.get().get(playerListEntry.getProfile().getName()) != null) {
            Enemy enemy = Enemies.get().get(playerListEntry.getProfile().getName());
            if (enemy != null) color = Enemies.get().color;
        }

        if (color != null) {
            String nameString = name.getString();

            for (Formatting format : Formatting.values()) {
                if (format.isColor()) nameString = nameString.replace(format.toString(), "");
            }

            name = new LiteralText(nameString).setStyle(name.getStyle().withColor(new TextColor(color.getPacked())));
        }

        return name;
    }
}