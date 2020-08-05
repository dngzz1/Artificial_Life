package audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioFormat.Encoding;

public class SoundHandler {
	private static AudioFormat format = setDefaultAudioFormat();
	private static float minVolume, maxVolume;
	private static boolean mute = false;
	private static boolean musicMute = false;
	private static float volume = 0;
	private static float musicVolume = 0;
	private static Wave musicWave;
	
	private SoundHandler(){};
	
	public static AudioFormat getAudioFormat(){
		return format;
	}
	
	public static float getMaxVolume(){
		return maxVolume;
	}
	
	public static float getMinVolume(){
		return minVolume;
	}
	
	public static float getMusicVolume(){
		return SoundHandler.musicVolume;
	}
	
	public static float getVolume(){
		return SoundHandler.volume;
	}
	
	public static boolean isMusicMuted(){
		return musicMute;
	}
	
	public static boolean isMuted(){
		return mute;
	}
	
	public static void playMusic(String filename, boolean looping){
		if(!musicMute){
			if(musicWave != null){
				musicWave.stopPlaying();
			}
			if(filename != null){
				musicWave = new Wave(filename, Wave.Position.NORMAL, looping);
				musicWave.start();
				musicWave.setVolume(musicVolume);
			}
		}
	}
	
	public static void playSound(String filename){
		if(!mute){
			Wave wave = new Wave(filename, Wave.Position.NORMAL, false);
			wave.start();
			wave.setVolume(volume);
		}
	}
	
	public static void playSound(String filename, boolean looping){
		if(!mute){
			Wave wave = new Wave(filename, Wave.Position.NORMAL, looping);
			wave.start();
			wave.setVolume(volume);
		}
	}
	
	public static void playSound(String filename, boolean looping, float volumeAdjustment){
		if(!mute){
			Wave wave = new Wave(filename, Wave.Position.NORMAL, looping);
			wave.start();
			wave.setVolume(volume + volumeAdjustment);
		}
	}
	
	public static void setAudioFormat(AudioFormat format){
		SoundHandler.format = format;
		// Open a test line to find min/max volume. //
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		SourceDataLine auline = null;
		try { 
			auline = (SourceDataLine) AudioSystem.getLine(info);
			auline.open(format);
			if (auline.isControlSupported(FloatControl.Type.MASTER_GAIN)) { 
				FloatControl volumeControl = (FloatControl) auline.getControl(FloatControl.Type.MASTER_GAIN);
				minVolume = volumeControl.getMinimum();
				maxVolume = volumeControl.getMaximum();
			}
		} catch (LineUnavailableException e) { 
			e.printStackTrace();
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}
	
	private static AudioFormat setDefaultAudioFormat(){
		AudioFormat format = new AudioFormat(Encoding.PCM_SIGNED, 44100.0f, 16, 1, 2, 44100.0f, false);
		setAudioFormat(format);
		return format;
	}
	
	public static void setMusicMute(boolean musicMute){
		SoundHandler.musicMute = musicMute;
		if(musicMute && musicWave != null){
				musicWave.stopPlaying();
		}
	}
	
	public static void setMusicVolume(float volume){
		SoundHandler.musicVolume = volume;
		if(musicWave != null){
			musicWave.setVolume(volume);
		}
	}
	
	public static void setMute(boolean mute){
		SoundHandler.mute = mute;
	}
	
	public static void setVolume(float volume){
		SoundHandler.volume = volume;
	}
}   