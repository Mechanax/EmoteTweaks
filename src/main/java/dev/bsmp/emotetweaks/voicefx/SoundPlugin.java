package dev.bsmp.emotetweaks.voicefx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.jetbrains.annotations.Nullable;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.VolumeCategory;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;

public class SoundPlugin implements VoicechatPlugin {


    public static String EMOTE_CATEGORY = "emotes";

    public static VoicechatApi voicechatApi;
    @Nullable
    public static VoicechatServerApi voicechatServerApi;
    @Nullable
    public static VolumeCategory emotes;

    private static List<SFXThread> runningThreads = new ArrayList<>();

    @Override
    public String getPluginId() {
        return "emotetweaks";
    }

    @Override
    public void initialize(VoicechatApi api) {
        voicechatApi = api;
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
    }

    public void onServerStarted(VoicechatServerStartedEvent event) {
        voicechatServerApi = event.getVoicechat();
        /* emotes = voicechatServerApi.volumeCategoryBuilder()
                .setId(EMOTE_CATEGORY)
                .setName("Emotes")
                .setDescription("The volume of all sounds played via emotes")
                .setIcon(getIcon("category_emote.png"))
                .build();

        voicechatServerApi.registerVolumeCategory(emotes); */
    }

    public static void playSound(short[] data) {
        try {
            SFXThread thread = SFXThread.playSFX(data);
            runningThreads.add(thread);
            thread.startPlaying();
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void stopSounds() {
        for(SFXThread thread : runningThreads) {
            thread.interrupt();
        }
        runningThreads.clear();
    }

    @Nullable
    private int[][] getIcon(String path) {
        try {
            Enumeration<URL> resources = Plugin.class.getClassLoader().getResources(path);
            while (resources.hasMoreElements()) {
                BufferedImage bufferedImage = ImageIO.read(resources.nextElement().openStream());
                if (bufferedImage.getWidth() != 16) {
                    continue;
                }
                if (bufferedImage.getHeight() != 16) {
                    continue;
                }
                int[][] image = new int[16][16];
                for (int x = 0; x < bufferedImage.getWidth(); x++) {
                    for (int y = 0; y < bufferedImage.getHeight(); y++) {
                        image[x][y] = bufferedImage.getRGB(x, y);
                    }
                }
                return image;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
