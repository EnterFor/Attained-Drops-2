package shadows.attained.integration;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import shadows.attained.blocks.BlockBulb;
import shadows.attained.blocks.BlockCreator;
import shadows.attained.blocks.BlockPlant;
import shadows.attained.blocks.BlockVitalized;
import shadows.attained.blocks.BulbType;
import shadows.attained.blocks.SoilType;
import shadows.attained.init.ModRegistry;

public class Waila {

	public static void callbackRegister(IWailaRegistrar registrar) {
		registrar.registerStackProvider(new Provider(), BlockVitalized.class);
		registrar.registerStackProvider(new Provider(), BlockBulb.class);
		registrar.registerStackProvider(new Provider(), BlockPlant.class);
		registrar.registerBodyProvider(new Provider(), BlockPlant.class);
		registrar.registerBodyProvider(new Provider(), BlockVitalized.class);
		registrar.registerBodyProvider(new Provider(), BlockCreator.class);
	}

	public static class Provider implements IWailaDataProvider {

		@Override
		public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
			if (accessor.getBlock() != null) {
				IBlockState state = accessor.getWorld().getBlockState(accessor.getPosition());
				Block block = state.getBlock();
				if (block instanceof BlockVitalized) return state.getValue(BlockVitalized.SOIL).get();
				if (block instanceof BlockBulb) return state.getValue(BlockBulb.BULB).get();
				if (block instanceof BlockPlant) return new ItemStack(block, 1, 0);
			}
			return null;
		}

		@Override
		public List<String> getWailaBody(ItemStack stack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
			if (accessor.getBlock() != null) {
				BlockPos pos = accessor.getPosition();
				World world = accessor.getWorld();
				IBlockState state = world.getBlockState(pos);
				Block block = state.getBlock();
				if (block instanceof BlockPlant) {
					currenttip.add(I18n.format("tooltip.attaineddrops2.growth") + " " + (int) (100 * ((float) state.getValue(BlockPlant.AGE) / ((BlockPlant) ModRegistry.PLANT).getMaxAge())) + "%");
					int k = (int) (100 * (1F / (5F - (float) state.getValue(BlockPlant.CHARGE))));
					currenttip.add(I18n.format("tooltip.attaineddrops2.revertchance") + " " + (k > 20 ? k : 0) + "%");
					IBlockState state2 = BlockVitalized.getBulbFromState(world.getBlockState(pos.down()));
					currenttip.add(I18n.format("tooltip.attaineddrops2.growing") + " " + (state2 == null ? I18n.format("tooltip.attaineddrops2.nothing") : ModRegistry.BULB.getPickBlock(state2, null, null, null, null).getDisplayName()));
				} else if (block instanceof BlockVitalized) {
					if (state.getValue(BlockVitalized.SOIL) != SoilType.NONE) {
						currenttip.add(I18n.format("tooltip.attaineddrops2.enrichedwith", BulbType.values()[state.getValue(BlockVitalized.SOIL).ordinal() - 1].getDisplayName()));
					}
				} else if (block instanceof BlockCreator) {
					currenttip.add(I18n.format("tooltip.attaineddrops2.creatorcharge", state.getValue(BlockCreator.CHARGE)));
				}
			}
			return currenttip;
		}

		@Override
		public List<String> getWailaHead(ItemStack stack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
			return currenttip;
		}

		@Override
		public List<String> getWailaTail(ItemStack stack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
			return currenttip;
		}

		@Override
		public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
			return tag;
		}
	}

}
