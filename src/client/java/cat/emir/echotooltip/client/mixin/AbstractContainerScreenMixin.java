package cat.emir.echotooltip.client.mixin;

import cat.emir.echotooltip.client.GuiGraphicsTooltipAccess;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

    @Shadow protected Slot hoveredSlot;

    @Inject(
            method = "extractTooltip(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/resources/Identifier;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void echoTooltip$captureFromContainer(GuiGraphicsExtractor guiGraphics, int i, int j, CallbackInfo ci) {
        if (hoveredSlot != null && hoveredSlot.hasItem()) {
            ((GuiGraphicsTooltipAccess) guiGraphics).echoTooltip$setPendingTooltipItem(hoveredSlot.getItem());
        }
    }
}