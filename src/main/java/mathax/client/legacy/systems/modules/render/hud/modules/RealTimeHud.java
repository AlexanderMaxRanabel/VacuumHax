package mathax.client.legacy.systems.modules.render.hud.modules;

import mathax.client.legacy.settings.BoolSetting;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.settings.SettingGroup;
import mathax.client.legacy.systems.modules.render.hud.HUD;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class RealTimeHud extends TripleTextHudElement {
    /*private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Boolean> euFormat = sgGeneral.add(new BoolSetting.Builder()
        .name("eu-format")
        .description("Changes the time to Europian format.")
        .defaultValue(false)
        .build()
    );*/
    //TODO: Change default to AM & PM. Add EU format setting 12:00.

    public RealTimeHud(HUD hud) {
        super(hud, "real-time", "Displays real world time.", "Time: ");
    }

    @Override
    protected String getRight() {
        return LocalTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
    }

    @Override
    public String getEnd() {
        return "";
    }
}