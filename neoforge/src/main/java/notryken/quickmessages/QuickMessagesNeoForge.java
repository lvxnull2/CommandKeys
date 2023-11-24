package notryken.quickmessages;

import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import notryken.quickmessages.gui.screen.ConfigScreenMono;

@Mod(Constants.MOD_ID)
public class QuickMessagesNeoForge {
    public QuickMessagesNeoForge() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (client, parent) -> new ConfigScreenMono(parent, client.options,
                                Component.translatable("screen.quickmessages.title"), null))
        );

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::clientSetup);
        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(this::registerKeyMappingsEvent);
        }
        QuickMessages.init();
    }

    @SubscribeEvent
    public void clientSetup(FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.addListener(ClientEventHandler::clientTickEvent);
    }

    @SubscribeEvent
    public void registerKeyMappingsEvent(RegisterKeyMappingsEvent event) {
        event.register(QuickMessages.CONFIG_KEY);
    }
}