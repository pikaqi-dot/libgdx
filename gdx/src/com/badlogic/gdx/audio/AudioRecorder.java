/** <b>音频录制器接口。</b>
* 封装了音频录制设备，用于录制 PCM 音频数据。
* @author mzechner */

package com.badlogic.gdx.audio;

import com.badlogic.gdx.utils.Disposable;

/** An AudioRecorder allows to record input from an audio device. It has a sampling rate and is either stereo or mono. Samples are
 * returned in signed 16-bit PCM format. Stereo samples are interleaved in the order left channel, right channel. The
 * AudioRecorder has to be disposed if no longer needed via the {@link #dispose()}.
 * 
 * @author mzechner */
public interface AudioRecorder extends Disposable {
	/** Reads in numSamples samples into the array samples starting at offset. If the recorder is in stereo you have to multiply
	 * numSamples by 2.
	 * 
	 * @param samples the array to write the samples to
	 * @param offset the offset into the array
	 * @param numSamples the number of samples to be read */
	public void read (short[] samples, int offset, int numSamples);

	/** Disposes the AudioRecorder */
	public void dispose ();
}
