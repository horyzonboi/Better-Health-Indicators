package net.horyzon.client.Rendering;

import net.horyzon.client.HealthStore;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;


public class SettingScreen extends Screen {
    protected SettingScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        Button buttonWidget = Button.builder(Component.literal((HealthStore.displayOwnHealth ? "ON" : "OFF")), (btn) -> {
            // When the button is clicked, we can display a toast to the screen.
            HealthStore.displayOwnHealth = !HealthStore.displayOwnHealth;
            btn.setMessage(Component.literal(
                            (HealthStore.displayOwnHealth ? "ON" : "OFF")
            ));
        }).bounds(40, 40, 120, 20).build();
     
        addRenderableWidget(buttonWidget);
    }


    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        graphics.text(this.font, "Display Own Health ?", 40, 40 - font.lineHeight- 10, 0xFFFFFFFF, true);
    }
}