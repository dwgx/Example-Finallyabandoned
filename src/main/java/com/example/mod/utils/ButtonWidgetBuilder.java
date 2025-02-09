package com.example.mod.utils;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ButtonWidgetBuilder {
    private final Text text;
    private int x, y, width, height;
    private ButtonWidget.PressAction onPress;
    private Tooltip tooltip;
    private boolean isEnabled = true;

    public ButtonWidgetBuilder(Text text, ButtonWidget.PressAction onPress) {
        this.text = text;
        this.onPress = onPress;
    }

    public ButtonWidgetBuilder dimensions(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        return this;
    }

    public ButtonWidgetBuilder tooltip(Tooltip tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public ButtonWidgetBuilder enabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
        return this;
    }

    public ButtonWidget build() {
        // Use the ButtonWidget.Builder to build the button
        ButtonWidget buttonWidget = new ButtonWidget.Builder(this.text, this.onPress)
                .position(this.x, this.y)
                .size(this.width, this.height)
                .tooltip(this.tooltip)
                .build();
        buttonWidget.active = isEnabled;
        return buttonWidget;
    }
}
