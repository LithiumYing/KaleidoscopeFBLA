package io.github.lithiumying.kioskclient;

import java.io.File;
import javax.sound.sampled.*;

public class SoundPlayer {
    /**
     * Warms up the Java Audio Thread with an empty .wav file
     */

    public static void warmup() {
        try {
            File soundFile = new File("kiosk_client/app/src/main/sound/Empty.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
            System.out.println("Warming up AudioInputStream...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Plays the accepted sound once
     */
    public static void playAcceptedSound() {
        try {
            File soundFile = new File("kiosk_client/app/src/main/sound/Accepted.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
            Thread.sleep(clip.getMicrosecondLength() / 1000);
            System.out.println("Playing sound...");
            // clip.stop();
            // clip.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param times An int number of times to loop through the accepted sound
     * 
     */
    public static void testAcceptedSound(int times) {
        try {
            File soundFile = new File("kiosk_client/app/src/main/sound/Accepted.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.loop(--times);
            Thread.sleep(clip.getMicrosecondLength() / 1000);
            // clip.stop();
            // clip.close();
            System.out.println("Playing sound...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Plays the denied sound once
     */
    public static void playDeniedSound() {
        try {
            File soundFile = new File("kiosk_client/app/src/main/sound/Denied.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
            // Thread.sleep(clip.getMicrosecondLength() / 1000);
            // clip.stop();
            // clip.close();
            System.out.println("Playing sound...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param times an int number of times to loop through the denied sound
     */
    public static void testDeniedSound(int times) {
        try {
            File soundFile = new File("kiosk_client/app/src/main/sound/Denied.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.loop(--times);
            // System.out.println(clip.getMicrosecondLengtlh());
            Thread.sleep(clip.getMicrosecondLength() / 1000);
            // clip.stop();
            // clip.close();
            System.out.println("Playing sound...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        warmup();
    }
}
