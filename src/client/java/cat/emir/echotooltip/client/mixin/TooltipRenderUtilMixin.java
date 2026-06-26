package cat.emir.echotooltip.client.mixin;

import cat.emir.echotooltip.client.GuiGraphicsTooltipAccess;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(TooltipRenderUtil.class)
public abstract class TooltipRenderUtilMixin {

    @Redirect(
            method = "renderTooltipBackground",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V",
                    ordinal = 1
            )
    )
    private static void echoTooltip$tintFrame(
            GuiGraphics graphics,
            RenderPipeline pipeline,
            Identifier sprite,
            int x, int y, int width, int height
    ) {
        ItemStack stack = ((GuiGraphicsTooltipAccess) graphics).echoTooltip$getPendingTooltipItem();
        System.out.println("echoTooltip tint: stack=" + (stack != null ? stack.getDisplayName().getString() : "null") + " empty=" + (stack == null || stack.isEmpty()));
        int color = 0xFFFFFFFF;
        if (stack != null && !stack.isEmpty()) {
            color = 0xFF000000 | getItemColor(stack);
        }
        graphics.blitSprite(pipeline, sprite, x, y, width, height, color);
    }

    private static int getItemColor(ItemStack stack) {
        // name color
        Component customName = stack.get(DataComponents.CUSTOM_NAME);
        if (customName != null) {
            int[] found = {-1};
            customName.visit((style, text) -> {
                if (found[0] == -1 && !text.isEmpty() && style.getColor() != null) {
                    found[0] = style.getColor().getValue();
                }
                return found[0] == -1 ? Optional.empty() : Optional.of(Unit.INSTANCE);
            }, Style.EMPTY);
            if (found[0] != -1) {
                return found[0];
            }
        }

        // rarity
        ChatFormatting formatting = stack.getRarity().color();
        Integer colorValue = formatting.getColor();
        if (colorValue != null) {
            return colorValue;
        }

        return 0xFFFFFF;
    }
}