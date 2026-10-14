package net.sachikonya.brewmaster.screen;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sachikonya.brewmaster.Brewmaster;
import net.sachikonya.brewmaster.screen.custom.WitchCauldronMenu;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, Brewmaster.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<WitchCauldronMenu>> WITCH_CAULDRON_MENU =
            registerMenuType("witch_cauldron_menu", WitchCauldronMenu::new);

    private static <T extends AbstractContainerMenu>DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name,
                                                                                                              IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register (IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
