package dev.terminalmc.commandkeys;

import com.mojang.blaze3d.platform.InputConstants;
import dev.terminalmc.commandkeys.config.Config;
import dev.terminalmc.commandkeys.config.Macro;
import dev.terminalmc.commandkeys.config.Profile;
import dev.terminalmc.commandkeys.gui.screen.OptionsScreen;
import dev.terminalmc.commandkeys.util.ModLogger;
import dev.terminalmc.commandkeys.util.PlaceholderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

import static dev.terminalmc.commandkeys.util.Localization.translationKey;

public class CommandKeys {
    public static final String MOD_ID = "commandkeys";
    public static final String MOD_NAME = "CommandKeys";
    public static final ModLogger LOG = new ModLogger(MOD_NAME);
    public static final KeyMapping CONFIG_KEY = new KeyMapping(
            translationKey("key", "main.edit"), InputConstants.Type.KEYSYM,
            InputConstants.KEY_K, translationKey("key", "main"));
    public static String lastConnection = "";

    public static void init() {
        Config.getAndSave();
    }

    public static void onEndTick(Minecraft mc) {
        // Open config screen
        while (CONFIG_KEY.consumeClick()) {
            mc.setScreen(new OptionsScreen(mc.screen, true));
        }
        if (mc.player != null && mc.level != null && !mc.isPaused()) {
            Config.get().activeProfile().getMacros().forEach(Macro::tick);
        }
    }

    public static void onConfigSaved(Config config) {
        // Cache update event (not currently used)
    }

    public static Profile profile() {
        return Config.get().activeProfile();
    }

    public static Screen getConfigScreen(Screen lastScreen) {
        return new OptionsScreen(lastScreen, inGame());
    }

    public static boolean inGame() {
        LocalPlayer player = Minecraft.getInstance().player;
        return (player != null && player.connection.getConnection().isConnected());
    }

    public static void send(String message, boolean addToHistory, boolean showHudMsg) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        message = PlaceholderUtil.replace(message);
        // new ChatScreen("").handleChatInput(message, addToHistory)
        // could be slightly better for compat but costs performance.
        if (message.startsWith("/")) {
            mc.player.connection.sendCommand(message.substring(1));
        } else {
            mc.player.connection.sendChat(message);
        }
        if (addToHistory) mc.gui.getChat().addRecentChat(message);
        if (showHudMsg) mc.gui.setOverlayMessage(Component.literal(message)
                .withStyle(ChatFormatting.GRAY), false);
    }

    public static void type(String message) {
        Minecraft.getInstance().setScreen(new ChatScreen(PlaceholderUtil.replace(message)));
    }
}
