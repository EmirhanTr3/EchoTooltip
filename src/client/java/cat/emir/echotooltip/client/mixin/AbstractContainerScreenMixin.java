package cat.emir.echotooltip.client.mixin;

import cat.emir.echotooltip.client.AbstractContainerScreenAccessor;
import cat.emir.echotooltip.client.GuiGraphicsTooltipAccess;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

    @Inject(
            method = "renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/resources/Identifier;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void echoTooltip$captureItem(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci) {
        AbstractContainerScreen<?> self = (AbstractContainerScreen<?>) (Object) this;
        Slot hoveredSlot = ((AbstractContainerScreenAccessor) self).echoTooltip$getHoveredSlot();
        if (hoveredSlot != null && hoveredSlot.hasItem()) {
            ((GuiGraphicsTooltipAccess) guiGraphics).echoTooltip$setPendingTooltipItem(hoveredSlot.getItem());
        }
    }
}