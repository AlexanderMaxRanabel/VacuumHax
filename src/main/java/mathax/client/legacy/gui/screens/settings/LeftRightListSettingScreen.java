package mathax.client.legacy.gui.screens.settings;

import mathax.client.legacy.gui.GuiTheme;
import mathax.client.legacy.gui.WindowScreen;
import mathax.client.legacy.gui.utils.Cell;
import mathax.client.legacy.gui.widgets.WWidget;
import mathax.client.legacy.gui.widgets.containers.WTable;
import mathax.client.legacy.gui.widgets.input.WTextBox;
import mathax.client.legacy.gui.widgets.pressable.WPressable;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.utils.Utils;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public abstract class LeftRightListSettingScreen<T> extends WindowScreen {
    protected final Setting<?> setting;
    protected final Collection<T> collection;
    private final WTextBox filter;

    private String filterText = "";

    private WTable table;

    public LeftRightListSettingScreen(GuiTheme theme, String title, Setting<?> setting, Collection<T> collection, Registry<T> registry) {
        super(theme, title);

        this.setting = setting;
        this.collection = collection;

        // Filter
        filter = add(theme.textBox("")).minWidth(400).expandX().widget();
        filter.setFocused(true);
        filter.action = () -> {
            filterText = filter.get().trim();

            table.clear();
            initWidgets(registry);
        };

        table = add(theme.table()).expandX().widget();

        initWidgets(registry);
    }

    private void initWidgets(Registry<T> registry) {
        // Left (all)
        WTable left = abc(pairs -> registry.forEach(t -> {
            if (skipValue(t) || collection.contains(t)) return;

            int words = Utils.search(getValueName(t), filterText);
            if (words > 0) pairs.add(new Pair<>(t, words));
        }), true, t -> {
            addValue(registry, t);

            T v = getAdditionalValue(t);
            if (v != null) addValue(registry, v);
        });

        if (left.cells.size() > 0) table.add(theme.verticalSeparator()).expandWidgetY();

        // Right (selected)
        abc(pairs -> {
            for (T value : collection) {
                if (skipValue(value)) continue;

                int words = Utils.search(getValueName(value), filterText);
                if (words > 0) pairs.add(new Pair<>(value, words));
            }
        }, false, t -> {
            removeValue(registry, t);

            T v = getAdditionalValue(t);
            if (v != null) removeValue(registry, v);
        });
    }

    private void addValue(Registry<T> registry, T value) {
        if (!collection.contains(value)) {
            collection.add(value);

            setting.changed();
            table.clear();
            initWidgets(registry);
        }
    }

    private void removeValue(Registry<T> registry, T value) {
        if (collection.remove(value)) {
            setting.changed();
            table.clear();
            initWidgets(registry);
        }
    }

    private WTable abc(Consumer<List<Pair<T, Integer>>> addValues, boolean isLeft, Consumer<T> buttonAction) {
        // Create
        Cell<WTable> cell = this.table.add(theme.table()).top();
        WTable table = cell.widget();

        Consumer<T> forEach = t -> {
            if (!includeValue(t)) return;

            table.add(getValueWidget(t));

            WPressable button = table.add(isLeft ? theme.plus() : theme.minus()).expandCellX().right().widget();
            button.action = () -> buttonAction.accept(t);

            table.row();
        };

        // Sort
        List<Pair<T, Integer>> values = new ArrayList<>();
        addValues.accept(values);
        if (!filterText.isEmpty()) values.sort(Comparator.comparingInt(value -> -value.getRight()));
        for (Pair<T, Integer> pair : values) forEach.accept(pair.getLeft());

        if (table.cells.size() > 0) cell.expandX();

        return table;
    }

    protected boolean includeValue(T value) {
        return true;
    }

    protected abstract WWidget getValueWidget(T value);

    protected abstract String getValueName(T value);

    protected boolean skipValue(T value) {
        return false;
    }

    protected T getAdditionalValue(T value) {
        return null;
    }
}