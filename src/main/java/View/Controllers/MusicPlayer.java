package View.Controllers;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MusicPlayer {

    private static MediaPlayer mediaPlayer;
    private static boolean musicEnabled = true;


    public static void setMusicEnabled(boolean enabled) {
        musicEnabled = enabled;
        if (!enabled) {
            dispose(); // stop and dispose any current music
        } else if (!isPlaying()) {
            startMusic(); // only start if not already playing
        }
    }

    public static boolean isMusicEnabled() {
        return musicEnabled;
    }

    public static void startMusic() {
        if (!musicEnabled) return;
        dispose();

        Media sound = new Media(MusicPlayer.class.getResource("/music.mp3").toExternalForm());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // music in loop

        mediaPlayer.play();
    }

    public static void playVictoryMusic() {
        if (!musicEnabled) return;

        dispose(); // stop current music

        Media sound = new Media(MusicPlayer.class.getResource("/victory.mp3").toExternalForm());
        mediaPlayer = new MediaPlayer(sound);

        // Resume background music when victory ends
        mediaPlayer.setOnEndOfMedia(() -> {
            if (musicEnabled) startMusic();
        });
        mediaPlayer.play();
    }

    public static void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public static void dispose() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    public static boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }
}
