package com.example.mod.utils;

import com.mojang.authlib.minecraft.BanDetails;
import com.mojang.logging.LogUtils;
import io.jsonwebtoken.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.AccessibilityOnboardingButtons;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsNotificationsScreen;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.storage.LevelStorage;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import static com.example.mod.client.GameAccessor.mc;

@Environment(EnvType.CLIENT)
public class GuiMainMenu extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Text NARRATOR_SCREEN_TITLE = Text.translatable("narrator.screen.title");
    private static final String DEMO_WORLD_NAME = "Demo_World";
    private static final float FADE_DURATION = 2000.0F;

    @Nullable
    private SplashTextRenderer splashText;
    @Nullable
    private RealmsNotificationsScreen realmsNotificationGui;
    private float backgroundAlpha;
    private boolean doBackgroundFade;
    private long backgroundFadeStart;
    private final LogoDrawer logoDrawer;

    public GuiMainMenu() {
        this(false);
    }

    public GuiMainMenu(boolean doBackgroundFade) {
        this(doBackgroundFade, null);
    }

    public GuiMainMenu(boolean doBackgroundFade, @Nullable LogoDrawer logoDrawer) {
        super(NARRATOR_SCREEN_TITLE);
        this.backgroundAlpha = 100F;
        this.doBackgroundFade = doBackgroundFade;
        this.logoDrawer = logoDrawer != null ? logoDrawer : new LogoDrawer(false);
    }

    private boolean isRealmsNotificationsGuiDisplayed() {
        return this.realmsNotificationGui != null;
    }

    public void tick() {
        if (this.isRealmsNotificationsGuiDisplayed()) {
            this.realmsNotificationGui.tick();
        }
    }

    public static void registerTextures(TextureManager textureManager) {
        textureManager.registerTexture(LogoDrawer.LOGO_TEXTURE);
        textureManager.registerTexture(LogoDrawer.EDITION_TEXTURE);
        textureManager.registerTexture(RotatingCubeMapRenderer.OVERLAY_TEXTURE);
        PANORAMA_RENDERER.registerTextures(textureManager);
    }

    public boolean shouldPause() {
        return false;
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    protected void init() {
        if (this.splashText == null) {
            this.splashText = this.client.getSplashTextLoader().get();
        }

        int l = this.height / 4 + 48;
        if (this.client.isDemo()) {
            l = this.addDemoWidgets(l, 24);
        } else {
            l = this.addNormalWidgets(l, 24);
        }

        l = this.addDevelopmentWidgets(l, 24);
        TextIconButtonWidget textIconButtonWidget = (TextIconButtonWidget)this.addDrawableChild(AccessibilityOnboardingButtons.createLanguageButton(20, (button) -> this.client.setScreen(new LanguageOptionsScreen(this, this.client.options, this.client.getLanguageManager())), true));
        textIconButtonWidget.setPosition(this.width / 2 - 124, l);
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.options"), (button) -> this.client.setScreen(new OptionsScreen(this, this.client.options)))
                .dimensions(this.width / 2 - 100, l, 98, 20)
                .build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.quit"), (button) -> this.client.scheduleStop())
                .dimensions(this.width / 2 + 2, l, 98, 20)
                .build());
        TextIconButtonWidget accessibilityButton = (TextIconButtonWidget)this.addDrawableChild(AccessibilityOnboardingButtons.createAccessibilityButton(20, (button) -> this.client.setScreen(new AccessibilityOptionsScreen(this, this.client.options)), true));
        accessibilityButton.setPosition(this.width / 2 + 104, l);

        // Read.me按钮 - 放在右上角，1/4大小
        ButtonWidget button = ButtonWidget.builder(Text.of("Read.me"), (buttonWidget) -> {
            Util.getOperatingSystem().open("https://www.l.wiki");  // 打开网页链接
        }).dimensions(this.width -25, 0, 25, 15).build();  // 1/4大小：宽 24 高 5
        this.addDrawableChild(button);
        button.setFocused(false);

        // 账户管理按钮 - 正方形，1/3大小，距离右下角40个像素
        ButtonWidget button2 = ButtonWidget.builder(Text.of("ACT"), (buttonWidget) -> {
            Util.getOperatingSystem().open("http://localhost:1337/manage");
        }).dimensions(this.width - 110 ,this.height - 84, 20, 20).build();  // 正方形，宽 30 高 30，距离底部和右边40个像素
        this.addDrawableChild(button2);
        button2.setFocused(false);

        if (this.realmsNotificationGui == null) {
            this.realmsNotificationGui = new RealmsNotificationsScreen();
        }

        if (this.isRealmsNotificationsGuiDisplayed()) {
            this.realmsNotificationGui.init(this.client, this.width, this.height);
        }
    }


    private int addDevelopmentWidgets(int y, int spacingY) {
        if (SharedConstants.isDevelopment) {
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Create Test World"), (button) -> CreateWorldScreen.showTestWorld(this.client, this))
                    .dimensions(this.width / 2 - 100, y += spacingY, 200, 20)
                    .build());
        }

        return y;
    }

    private int addNormalWidgets(int y, int spacingY) {
        // Singleplayer Button
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.singleplayer"), (button) -> this.client.setScreen(new SelectWorldScreen(this)))
                .dimensions(this.width / 2 - 100, y, 200, 20)
                .build());

        // Multiplayer Button (with Tooltip)
        Text multiplayerDisabledText = this.getMultiplayerDisabledText();
        boolean multiplayerEnabled = multiplayerDisabledText == null;
        Tooltip multiplayerTooltip = multiplayerDisabledText != null ? Tooltip.of(multiplayerDisabledText) : null;

        int buttonY = y + spacingY;
        ButtonWidget multiplayerButton = ButtonWidget.builder(Text.translatable("menu.multiplayer"), (button) -> {
            Screen screen = this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
            this.client.setScreen(screen);
        }).dimensions(this.width / 2 - 100, buttonY, 200, 20).tooltip(multiplayerTooltip).build();
        multiplayerButton.active = multiplayerEnabled;
        this.addDrawableChild(multiplayerButton);

        // Online Button (Realms)
        buttonY += spacingY;
        ButtonWidget onlineButton = ButtonWidget.builder(Text.translatable("menu.online"), (button) -> this.client.setScreen(new RealmsMainScreen(this)))
                .dimensions(this.width / 2 - 100, buttonY, 200, 20)
                .tooltip(multiplayerTooltip)
                .build();
        onlineButton.active = multiplayerEnabled;
        this.addDrawableChild(onlineButton);

        return buttonY + spacingY;
    }

    @Nullable
    private Text getMultiplayerDisabledText() {
        if (this.client.isMultiplayerEnabled()) {
            return null;
        } else if (this.client.isUsernameBanned()) {
            return Text.translatable("title.multiplayer.disabled.banned.name");
        } else {
            BanDetails banDetails = this.client.getMultiplayerBanDetails();
            if (banDetails != null) {
                return banDetails.expires() != null
                        ? Text.translatable("title.multiplayer.disabled.banned.temporary")
                        : Text.translatable("title.multiplayer.disabled.banned.permanent");
            } else {
                return Text.translatable("title.multiplayer.disabled");
            }
        }
    }

    private int addDemoWidgets(int y, int spacingY) {
        boolean canReadDemoWorld = this.canReadDemoWorldData();

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.playdemo"), (button) -> {
            if (canReadDemoWorld) {
                this.client.createIntegratedServerLoader().start("Demo_World", () -> this.client.setScreen(this));
            } else {
                this.client.createIntegratedServerLoader().createAndStart("Demo_World", MinecraftServer.DEMO_LEVEL_INFO, GeneratorOptions.DEMO_OPTIONS, WorldPresets::createDemoOptions, this);
            }
        }).dimensions(this.width / 2 - 100, y, 200, 20).build());

        ButtonWidget buttonResetDemo = this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.resetdemo"), (button) -> {
            LevelStorage levelStorage = this.client.getLevelStorage();
            try (LevelStorage.Session session = levelStorage.createSessionWithoutSymlinkCheck("Demo_World")) {
                if (session.levelDatExists()) {
                    this.client.setScreen(new ConfirmScreen(this::onDemoDeletionConfirmed,
                            Text.translatable("selectWorld.deleteQuestion"),
                            Text.translatable("selectWorld.deleteWarning", MinecraftServer.DEMO_LEVEL_INFO.getLevelName()),
                            Text.translatable("selectWorld.deleteButton"),
                            ScreenTexts.CANCEL));
                }
            } catch (IOException | java.io.IOException e) {
                SystemToast.addWorldAccessFailureToast(this.client, "Demo_World");
                LOGGER.warn("Failed to access demo world", e);
            }
        }).dimensions(this.width / 2 - 100, y + spacingY, 200, 20).build());

        buttonResetDemo.active = canReadDemoWorld;
        return y + 48;
    }

    private boolean canReadDemoWorldData() {
        try (LevelStorage.Session session = this.client.getLevelStorage().createSessionWithoutSymlinkCheck("Demo_World")) {
            return session.levelDatExists();
        } catch (IOException e) {
            SystemToast.addWorldAccessFailureToast(this.client, "Demo_World");
            LOGGER.warn("Failed to read demo world data", e);
            return false;
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
            this.backgroundFadeStart = Util.getMeasuringTimeMs();
        }

        float fadeFactor = calculateFadeFactor();
        renderBackground(context, delta, fadeFactor);
        renderPanoramaBackground(context, delta);

        int alpha = MathHelper.ceil(fadeFactor * 255.0F) << 24;
        if ((alpha & -67108864) != 0) {
            super.render(context, mouseX, mouseY, delta);
            this.logoDrawer.draw(context, this.width, fadeFactor);
            if (this.splashText != null && !this.client.options.getHideSplashTexts().getValue()) {
                this.splashText.render(context, this.width, this.textRenderer, alpha);
            }

            String string = "Minecraft " + SharedConstants.getGameVersion().getName();
            if (this.client.isDemo()) {
                string = string + " Demo";
            } else {
                string = string + ("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType());
            }

            if (MinecraftClient.getModStatus().isModded()) {
                //string = string + I18n.translate("menu.modded");
                string = "This Is A ExampleClient";
            }

            context.drawTextWithShadow(this.textRenderer, string, 2, this.height - 10, 16777215 | alpha);

            // 绘制玩家名字在右下角.
            String playerName = mc.getSession().getUsername();  // 获取玩家名字
            int playerNameWidth = this.textRenderer.getWidth(playerName);  // 获取玩家名字宽度
            int playerNameX = this.width - playerNameWidth ;  // 设置右下角位置
            int playerNameY = this.height - 10;  // 距离底部10个像素

            context.drawTextWithShadow(this.textRenderer, playerName, playerNameX, playerNameY, 16777215 | alpha);

            if (this.isRealmsNotificationsGuiDisplayed() && fadeFactor >= 1.0F) {
                this.realmsNotificationGui.render(context, mouseX, mouseY, delta);
            }
        }
    }


    private float calculateFadeFactor() {
        if (!this.doBackgroundFade) return 1.0F;

        float fadeTime = (float) (Util.getMeasuringTimeMs() - this.backgroundFadeStart) / FADE_DURATION;
        if (fadeTime > 1.0F) {
            this.doBackgroundFade = false;
            this.backgroundAlpha = 1.0F;
            return 1.0F;
        } else {
            fadeTime = MathHelper.clamp(fadeTime, 0.0F, 1.0F);
            this.backgroundAlpha = MathHelper.clampedMap(fadeTime, 0.0F, 0.5F, 0.0F, 1.0F);
            return MathHelper.clampedMap(fadeTime, 0.5F, 1.0F, 0.0F, 1.0F);
        }
    }

    private void renderBackground(DrawContext context, float delta, float fadeFactor) {
        setWidgetAlpha(fadeFactor);
        ROTATING_PANORAMA_RENDERER.render(context, this.width, this.height, this.backgroundAlpha, delta);
    }

    private void setWidgetAlpha(float alpha) {
        for (Element element : this.children()) {
            if (element instanceof ClickableWidget clickableWidget) {
                clickableWidget.setAlpha(alpha);
            }
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button) || (this.isRealmsNotificationsGuiDisplayed() && this.realmsNotificationGui.mouseClicked(mouseX, mouseY, button));
    }

    public void removed() {
        if (this.realmsNotificationGui != null) {
            this.realmsNotificationGui.removed();
        }
    }

    private void onDemoDeletionConfirmed(boolean delete) {
        if (delete) {
            try (LevelStorage.Session session = this.client.getLevelStorage().createSessionWithoutSymlinkCheck("Demo_World")) {
                try {
                    session.deleteSessionLock();
                } catch (java.io.IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException | java.io.IOException e) {
                SystemToast.addWorldDeleteFailureToast(this.client, "Demo_World");
                LOGGER.warn("Failed to delete demo world", e);
            }
        }

        this.client.setScreen(this);
    }
}
