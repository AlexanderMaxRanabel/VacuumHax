package mathax.client.legacy.systems.modules.combat;

import mathax.client.legacy.events.world.TickEvent;
import mathax.client.legacy.settings.BoolSetting;
import mathax.client.legacy.settings.IntSetting;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.settings.SettingGroup;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import mathax.client.legacy.utils.player.InvUtils;
import mathax.client.legacy.bus.EventHandler;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Items;

public class BowSpam extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> charge = sgGeneral.add(new IntSetting.Builder()
        .name("charge")
        .description("How long to charge the bow before releasing in ticks.")
        .defaultValue(5)
        .min(5)
        .max(20)
        .sliderMin(5)
        .sliderMax(20)
        .build()
    );

    private final Setting<Boolean> onlyWhenHoldingRightClick = sgGeneral.add(new BoolSetting.Builder()
        .name("when-holding-right-click")
        .description("Works only when holding right click.")
        .defaultValue(false)
        .build()
    );

    private boolean wasBow = false;
    private boolean wasHoldingRightClick = false;

    public BowSpam() {
        super(Categories.Combat, Items.BOW, "bow-spam", "Spams arrows.");
    }

    @Override
    public void onActivate() {
        wasBow = false;
        wasHoldingRightClick = false;
    }

    @Override
    public void onDeactivate() {
        setPressed(false);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!mc.player.getAbilities().creativeMode && !InvUtils.find(itemStack -> itemStack.getItem() instanceof ArrowItem).found())
            return;

        if (!onlyWhenHoldingRightClick.get() || mc.options.keyUse.isPressed()) {
            boolean isBow = mc.player.getMainHandStack().getItem() == Items.BOW;
            if (!isBow && wasBow) setPressed(false);

            wasBow = isBow;
            if (!isBow) return;

            if (mc.player.getItemUseTime() >= charge.get()) {
                mc.player.stopUsingItem();
                mc.interactionManager.stopUsingItem(mc.player);
            } else {
                setPressed(true);
            }

            wasHoldingRightClick = mc.options.keyUse.isPressed();
        } else {
            if (wasHoldingRightClick) {
                setPressed(false);
                wasHoldingRightClick = false;
            }
        }
    }

    private void setPressed(boolean pressed) {
        mc.options.keyUse.setPressed(pressed);
    }
}
