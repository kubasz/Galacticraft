package micdoodle8.mods.galacticraft.core.inventory;

import cpw.mods.fml.common.registry.GameRegistry;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.util.RecipeUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotBuggyBench extends Slot
{
    private final int index;
    private final int x, y, z;
    private final EntityPlayer player;

    public SlotBuggyBench(IInventory par2IInventory, int par3, int par4, int par5, int x, int y, int z, EntityPlayer player)
    {
        super(par2IInventory, par3, par4, par5);
        this.index = par3;
        this.x = x;
        this.y = y;
        this.z = z;
        this.player = player;
    }

    @Override
    public void onSlotChanged()
    {
        if (this.player instanceof EntityPlayerMP)
        {
            final Object[] toSend = { this.x, this.y, this.z };

            for (int var12 = 0; var12 < this.player.worldObj.playerEntities.size(); ++var12)
            {
                final EntityPlayerMP var13 = (EntityPlayerMP) this.player.worldObj.playerEntities.get(var12);

                if (var13.dimension == this.player.worldObj.provider.dimensionId)
                {
                    final double var14 = this.x - var13.posX;
                    final double var16 = this.y - var13.posY;
                    final double var18 = this.z - var13.posZ;

                    if (var14 * var14 + var16 * var16 + var18 * var18 < 20 * 20)
                    {
                        GalacticraftCore.packetPipeline.sendTo(new PacketSimple(EnumSimplePacket.C_SPAWN_SPARK_PARTICLES, new Object[] { this.x, this.y, this.z }), var13);
                    }
                }
            }
        }
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {
        if(index == 1) {
            return itemStack.getItem() == GCItems.basicItem && itemStack.getItemDamage() == 19;
        } else if(index == 2) {
            return itemStack.getItem() == GCItems.partBuggy && itemStack.getItemDamage() == 1;
        } else if(index == 3 && GalacticraftCore.isGalaxySpaceLoaded) {
            return itemStack.getItem() == GameRegistry.findItem(Constants.MOD_ID_GALAXYSPACE, "item.RocketControlComputer") && itemStack.getItemDamage() == 100;
        } else if(index >= 4 && index <= 7) {
            return itemStack.getItem() == GCItems.partBuggy && itemStack.getItemDamage() == 0;
        } else if(index >= 8 && index <= 11) {
            return itemStack.getItem() == GameRegistry.findItem(Constants.MOD_ID_GREGTECH, "gt.metaitem.01") && itemStack.getItemDamage() == 23306;
        } else if(index >= 12 && index <= 16) {
            return itemStack.getItem() == GCItems.meteoricIronIngot && itemStack.getItemDamage() == 1;
        } else if(index >= 17 && index <= 24) {
            return itemStack.getItem() == GameRegistry.findItem(Constants.MOD_ID_GREGTECH, "gt.metaitem.01") && itemStack.getItemDamage() == 27306;
        } else if(index >= 25 && index <= 34) {
            return itemStack.getItem() == GCItems.heavyPlatingTier1;
        } else if(index == 35) {
            return itemStack.getItem() == Item.getItemFromBlock(RecipeUtil.getChestBlock()) && (itemStack.getItemDamage() == 0 || itemStack.getItemDamage() == 1 || itemStack.getItemDamage() == 3);
        } else {
            return false;
        }
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as
     * getInventoryStackLimit(), but 1 in the case of armor slots)
     */
    @Override
    public int getSlotStackLimit()
    {
        return 1;
    }
}
