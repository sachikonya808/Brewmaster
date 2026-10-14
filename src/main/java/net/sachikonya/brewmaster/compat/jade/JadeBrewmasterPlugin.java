package net.sachikonya.brewmaster.compat.jade;

import net.minecraft.resources.ResourceLocation;
import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.block.custom.WitchCauldronBlock;
import net.sachikonya.brewmaster.block.entity.WitchCauldronBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadeBrewmasterPlugin implements IWailaPlugin {
    public static final ResourceLocation WITCH_CAULDRON =
            ResourceLocation.fromNamespaceAndPath(Brewmaster.MODID, "witch_cauldron");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(WitchCauldronComponentProvider.INSTANCE, WitchCauldronBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(WitchCauldronComponentProvider.INSTANCE, WitchCauldronBlock.class);
    }
}
