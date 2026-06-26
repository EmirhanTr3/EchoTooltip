package cat.emir.echotooltip.client.mixin;

import cat.emir.echotooltip.client.AbstractContainerScreenAccessor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenFieldMixin implements AbstractContainerScreenAccessor {

    @Shadow
    protected Slot hoveredSlot;

    @Override
    public Slot echoTooltip$getHoveredSlot() {
        return this.hoveredSlot;
    }
}