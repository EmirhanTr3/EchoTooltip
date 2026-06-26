package cat.emir.echotooltip.client.mixin;

import cat.emir.echotooltip.client.GuiGraphicsTooltipAccess;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsTooltipMixin implements GuiGraphicsTooltipAccess {

    @Unique
    private ItemStack echoTooltip$pendingTooltipItem = ItemStack.EMPTY;

    @Unique
    private boolean echoTooltip$itemCaptured = false;

    @Override
    @Unique
    public ItemStack echoTooltip$getPendingTooltipItem() {
        return this.echoTooltip$pendingTooltipItem;
    }

    @Override
    @Unique
    public void echoTooltip$setPendingTooltipItem(ItemStack stack) {
        this.echoTooltip$pendingTooltipItem = stack != null ? stack : ItemStack.EMPTY;
        this.echoTooltip$itemCaptured = true;
    }

    @Inject(
            method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V",
            at = @At("HEAD")
    )
    private void echoTooltip$captureFromItemStack(Font font, ItemStack stack, int x, int y, CallbackInfo ci) {
        this.echoTooltip$pendingTooltipItem = stack != null ? stack : ItemStack.EMPTY;
        this.echoTooltip$itemCaptured = true;
    }

    @Inject(
            method = "setTooltipForNextFrameInternal",
            at = @At("HEAD")
    )
    private void echoTooltip$handleInternal(Font font, List<ClientTooltipComponent> list, int i, int j, ClientTooltipPositioner positioner, Identifier identifier, boolean bl, CallbackInfo ci) {
        if (!this.echoTooltip$itemCaptured) {
            this.echoTooltip$pendingTooltipItem = ItemStack.EMPTY;
        }
        this.echoTooltip$itemCaptured = false;
    }

    @Inject(
            method = "renderDeferredElements",
            at = @At("RETURN")
    )
    private void echoTooltip$clearAfterRender(CallbackInfo ci) {
        this.echoTooltip$pendingTooltipItem = ItemStack.EMPTY;
        this.echoTooltip$itemCaptured = false;
    }
}