package micdoodle8.mods.galacticraft.core.energy.tile;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import cpw.mods.fml.common.eventhandler.Event;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.item.IElectricItem;
import mekanism.api.energy.ICableOutputter;
import mekanism.api.energy.IStrictEnergyAcceptor;
import micdoodle8.mods.galacticraft.api.item.ElectricItemHelper;
import micdoodle8.mods.galacticraft.api.item.IItemElectric;
import micdoodle8.mods.galacticraft.api.transmission.tile.IConductor;
import micdoodle8.mods.galacticraft.api.transmission.tile.IElectrical;
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler;
import micdoodle8.mods.galacticraft.core.tile.ReceiverMode;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import java.lang.reflect.Constructor;
import java.util.EnumSet;

@InterfaceList({
        @Interface(modid = "IC2API", iface = "ic2.api.energy.tile.IEnergySink"),
        @Interface(modid = "CoFHAPI|energy", iface = "cofh.api.energy.IEnergyHandler"),
        @Interface(modid = "MekanismAPI|energy", iface = "mekanism.api.energy.IStrictEnergyAcceptor"),
        @Interface(modid = "MekanismAPI|energy", iface = "mekanism.api.energy.ICableOutputter"),
})
public abstract class TileBaseUniversalElectrical extends EnergyStorageTile implements IEnergySink, IEnergyHandler, IStrictEnergyAcceptor, ICableOutputter
{
    protected boolean isAddedToEnergyNet;
    protected Object powerHandlerBC;

    //	@NetworkedField(targetSide = Side.CLIENT)
    //	public float energyStored = 0;
    private float IC2surplusInGJ = 0F;

    @Override
    public double getPacketRange()
    {
        return 12.0D;
    }

    @Override
    public int getPacketCooldown()
    {
        return 3;
    }

    @Override
    public boolean isNetworkedTile()
    {
        return true;
    }

    public EnumSet<ForgeDirection> getElectricalInputDirections()
    {
        return EnumSet.allOf(ForgeDirection.class);
    }

    public EnumSet<ForgeDirection> getElectricalOutputDirections()
    {
        return EnumSet.noneOf(ForgeDirection.class);
    }

    @Override
    public float getRequest(ForgeDirection direction)
    {
        if (this.getElectricalInputDirections().contains(direction) || direction == ForgeDirection.UNKNOWN)
        	return super.getRequest(direction);
        
        return 0F;
    }

    @Override
    public float receiveElectricity(ForgeDirection from, float receive, int tier, boolean doReceive)
    {
        if (this.getElectricalInputDirections().contains(from) || from == ForgeDirection.UNKNOWN)
        {
        	return super.receiveElectricity(from, receive, tier, doReceive);
        }
        
        return 0F;
    }
    
    //	@Override
    //	public float receiveElectricity(ForgeDirection from, ElectricityPack receive, boolean doReceive)
    //	{
    //		if (from == ForgeDirection.UNKNOWN || this.getElectricalInputDirections().contains(from))
    //		{
    //			if (!doReceive)
    //			{
    //				return this.getRequest(from);
    //			}
    //
    //			return this.receiveElectricity(receive, doReceive);
    //		}
    //
    //		return 0F;
    //	}

    /**
     * A non-side specific version of receiveElectricity for you to optionally
     * use it internally.
     */
    //	public float receiveElectricity(ElectricityPack receive, boolean doReceive)
    //	{
    //		if (receive != null)
    //		{
    //			float prevEnergyStored = this.getEnergyStored();
    //			float newStoredEnergy = Math.min(this.getEnergyStored() + receive.getWatts(), this.getMaxEnergyStored());
    //
    //			if (doReceive)
    //			{
    //				this.setEnergyStored(newStoredEnergy);
    //			}
    //
    //			return Math.max(newStoredEnergy - prevEnergyStored, 0);
    //		}
    //
    //		return 0;
    //	}

    //	public float receiveElectricity(float energy, boolean doReceive)
    //	{
    //		return this.receiveElectricity(ElectricityPack.getFromWatts(energy, this.getVoltage()), doReceive);
    //	}

    //	@Override
    //	public void setEnergyStored(float energy)
    //	{
    //		this.energyStored = Math.max(Math.min(energy, this.getMaxEnergyStored()), 0);
    //	}

    //	@Override
    //	public float getEnergyStored()
    //	{
    //		return this.energyStored;
    //	}

    //	public boolean canConnect(ForgeDirection direction, NetworkType type)
    //	{
    //		if (direction == null || direction.equals(ForgeDirection.UNKNOWN) || type != NetworkType.POWER)
    //		{
    //			return false;
    //		}
    //
    //		return this.getElectricalInputDirections().contains(direction) || this.getElectricalOutputDirections().contains(direction);
    //	}

    //	@Override
    //	public float getVoltage()
    //	{
    //		return 0.120F;
    //	}
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        //		this.energyStored = nbt.getFloat("energyStored");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        //		nbt.setFloat("energyStored", this.energyStored);
    }

    /**
     * Discharges electric item.
     */
    public void discharge(ItemStack itemStack)
    {
        if (itemStack != null)
        {
            Item item = itemStack.getItem();
            float energyToDischarge = this.getRequest(ForgeDirection.UNKNOWN);

            if (item instanceof IItemElectric)
            {
                this.storage.receiveEnergyGC(ElectricItemHelper.dischargeItem(itemStack, energyToDischarge));
            }
            else if (EnergyConfigHandler.isRFAPILoaded() && item instanceof IEnergyContainerItem)
            {
                this.storage.receiveEnergyGC(((IEnergyContainerItem)item).extractEnergy(itemStack, (int) (energyToDischarge / EnergyConfigHandler.RF_RATIO), false) * EnergyConfigHandler.RF_RATIO);
            }
            else if (EnergyConfigHandler.isIndustrialCraft2Loaded())
            {
                if (item instanceof IElectricItem)
                {
                    IElectricItem electricItem = (IElectricItem) item;
                    if (electricItem.canProvideEnergy(itemStack))
                    {
                        double result = 0;
                        double energyDischargeIC2 = energyToDischarge / EnergyConfigHandler.IC2_RATIO;
                        result = ic2.api.item.ElectricItem.manager.discharge(itemStack, energyDischargeIC2, 4, false, false, false);
                        float energyDischarged = (float) result * EnergyConfigHandler.IC2_RATIO;
                        this.storage.receiveEnergyGC(energyDischarged);
                    }
                }
            }
        }
    }

    @Override
    public void initiate()
    {
        super.initiate();
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (!this.worldObj.isRemote)
        {
            if (!this.isAddedToEnergyNet)
            {
                // Register to the IC2 Network
                this.initIC();
            }

            if (EnergyConfigHandler.isIndustrialCraft2Loaded() && this.IC2surplusInGJ >= 0.001F)
            {
                this.IC2surplusInGJ -= this.storage.receiveEnergyGC(this.IC2surplusInGJ);
                if (this.IC2surplusInGJ < 0.001F)
                {
                    this.IC2surplusInGJ = 0;
                }
            }
        }
    }


    /**
     * IC2 Methods
     */
    @Override
    public void invalidate()
    {
        this.unloadTileIC2();
        super.invalidate();
    }

    @Override
    public void onChunkUnload()
    {
        this.unloadTileIC2();
        super.onChunkUnload();
    }

    protected void initIC()
    {
        if (EnergyConfigHandler.isIndustrialCraft2Loaded())
        {
            try
            {
                Class<?> tileLoadEvent = Class.forName("ic2.api.energy.event.EnergyTileLoadEvent");
                Class<?> energyTile = Class.forName("ic2.api.energy.tile.IEnergyTile");
                Constructor<?> constr = tileLoadEvent.getConstructor(energyTile);
                Object o = constr.newInstance(this);

                if (o != null && o instanceof Event)
                {
                    MinecraftForge.EVENT_BUS.post((Event) o);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        this.isAddedToEnergyNet = true;
    }

    private void unloadTileIC2()
    {
        if (this.isAddedToEnergyNet && this.worldObj != null)
        {
            if (EnergyConfigHandler.isIndustrialCraft2Loaded() && !this.worldObj.isRemote)
            {
                try
                {
                    Class<?> tileLoadEvent = Class.forName("ic2.api.energy.event.EnergyTileUnloadEvent");
                    Class<?> energyTile = Class.forName("ic2.api.energy.tile.IEnergyTile");
                    Constructor<?> constr = tileLoadEvent.getConstructor(energyTile);
                    Object o = constr.newInstance(this);

                    if (o != null && o instanceof Event)
                    {
                        MinecraftForge.EVENT_BUS.post((Event) o);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            this.isAddedToEnergyNet = false;
        }
    }

    @Override
    public double getDemandedEnergy()
    {
        if (EnergyConfigHandler.disableIC2Input)
        {
            return 0.0;
        }

        try
        {
            if (this.IC2surplusInGJ < 0.001F)
            {
                this.IC2surplusInGJ = 0F;
                return Math.ceil((this.storage.receiveEnergyGC(Integer.MAX_VALUE, true)) / EnergyConfigHandler.IC2_RATIO);
            }

            float received = this.storage.receiveEnergyGC(this.IC2surplusInGJ, true);
            if (received == this.IC2surplusInGJ)
            {
                return Math.ceil((this.storage.receiveEnergyGC(Integer.MAX_VALUE, true) - this.IC2surplusInGJ) / EnergyConfigHandler.IC2_RATIO);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0D;
    }

    @Override
    public double injectEnergy(ForgeDirection direction, double amount, double voltage)
    {
        if (!EnergyConfigHandler.disableIC2Input && (direction == ForgeDirection.UNKNOWN || this.getElectricalInputDirections().contains(direction)))
        {
            float convertedEnergy = (float) amount * EnergyConfigHandler.IC2_RATIO;
            int tierFromIC2 = ((int) voltage > 120) ? 2 : 1;
            float receive = this.receiveElectricity(direction, convertedEnergy, tierFromIC2, true);

            if (convertedEnergy > receive)
            {
                this.IC2surplusInGJ = convertedEnergy - receive;
            }
            else
            {
                this.IC2surplusInGJ = 0F;
            }

            // injectEnergy returns left over energy but all is used or goes into 'surplus'
            return 0D;
        }

        return amount;
    }

    @Override
    public int getSinkTier()
    {
        return 3;
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction)
    {
        //Don't add connection to IC2 grid if it's a Galacticraft tile
        if (emitter instanceof IElectrical || emitter instanceof IConductor)
        {
            return false;
        }

        try
        {
            Class<?> energyTile = Class.forName("ic2.api.energy.tile.IEnergyTile");
            if (!energyTile.isInstance(emitter))
            {
                return false;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return this.getElectricalInputDirections().contains(direction);
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
    {
        if (EnergyConfigHandler.disableRFInput)
        {
            return 0;
        }

    	if (!this.getElectricalInputDirections().contains(from))
    	{
    		return 0;
    	}

    	return MathHelper.floor_float(super.receiveElectricity(from, maxReceive * EnergyConfigHandler.RF_RATIO, 1, !simulate) / EnergyConfigHandler.RF_RATIO);
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from)
    {
    	return this.getElectricalInputDirections().contains(from) || this.getElectricalOutputDirections().contains(from);
    }

    @Override
    public int getEnergyStored(ForgeDirection from)
    {
    	return MathHelper.floor_float(this.getEnergyStoredGC() / EnergyConfigHandler.RF_RATIO);
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from)
    {
    	return MathHelper.floor_float(this.getMaxEnergyStoredGC() / EnergyConfigHandler.RF_RATIO);
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
    {
        return 0;
    }

    @Override
    public double transferEnergyToAcceptor(ForgeDirection from, double amount)
    {
        if (EnergyConfigHandler.disableMekanismInput)
        {
            return 0;
        }

        if (!this.getElectricalInputDirections().contains(from))
        {
            return 0;
        }

        return this.receiveElectricity(from, (float) amount * EnergyConfigHandler.MEKANISM_RATIO, 1, true) / EnergyConfigHandler.MEKANISM_RATIO;
    }

    @Override
    public boolean canReceiveEnergy(ForgeDirection side)
    {
        return this.getElectricalInputDirections().contains(side);
    }

    @Override
    public double getEnergy()
    {
        if (EnergyConfigHandler.disableMekanismInput)
        {
            return 0.0;
        }

        return this.getEnergyStoredGC() / EnergyConfigHandler.MEKANISM_RATIO;
    }

    @Override
    public void setEnergy(double energy)
    {
        if (EnergyConfigHandler.disableMekanismInput)
        {
            return;
        }

        this.storage.setEnergyStored((float) energy * EnergyConfigHandler.MEKANISM_RATIO);
    }

    @Override
    public double getMaxEnergy()
    {
        if (EnergyConfigHandler.disableMekanismInput)
        {
            return 0.0;
        }

        return this.getMaxEnergyStoredGC() / EnergyConfigHandler.MEKANISM_RATIO;
    }

    @Override
    public boolean canOutputTo(ForgeDirection side)
    {
        return false;
    }

    @Override
    public ReceiverMode getModeFromDirection(ForgeDirection direction)
    {
        if (this.getElectricalInputDirections().contains(direction))
        {
            return ReceiverMode.RECEIVE;
        }
        else if (this.getElectricalOutputDirections().contains(direction))
        {
            return ReceiverMode.EXTRACT;
        }

        return null;
    }

    /*
     * Compatibility: call this if the facing metadata is updated
     */
    public void updateFacing()
    {
        if (EnergyConfigHandler.isIndustrialCraft2Loaded() && !this.worldObj.isRemote)
        {
            //This seems the only method to tell IC2 the connection sides have changed
            //(Maybe there is an internal refresh() method but it's not in the API)
            this.unloadTileIC2();
            //This will do an initIC2 on next tick update.
        }
    }
}
