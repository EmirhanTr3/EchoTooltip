package cat.emir.echotooltip.client;

import net.minecraft.world.item.ItemStack;

public interface GuiGraphicsTooltipAccess {
    ItemStack echoTooltip$getPendingTooltipItem();
    void echoTooltip$setPendingTooltipItem(ItemStack stack);
}