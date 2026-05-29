/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx;

import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Null;

/**
 * <b>音频模块接口。</b>
 * 封装了音频资源的创建和管理。通过此接口可以：
 * <ul>
 *   <li>通过 {@link AudioDevice} 直接访问音频硬件</li>
 *   <li>通过 {@link AudioRecorder} 录制音频</li>
 *   <li>通过 {@link Sound} 创建和播放音效（如枪声、爆炸声等短音频）</li>
 *   <li>通过 {@link Music} 播放音乐流（长音频文件）</li>
 * </ul>
 * 
 * <p>
 * 通过此接口创建的所有资源在不再使用时都必须调用 dispose() 释放。
 * </p>
 * 
 * <p>
 * <b>注意：</b>所有 {@link Music} 实例在 {@link ApplicationListener#pause()} 时会自动暂停，
 * 在 {@link ApplicationListener#resume()} 时会自动恢复。
 * </p>
 * 
 * @author mzechner */
public interface Audio extends Disposable {
	/** 创建新的 {@link AudioDevice}（单声道或立体声）。
	 * AudioDevice 不再使用时必须通过 {@link AudioDevice#dispose()} 释放。
	 * 
	 * @param samplingRate 采样率（Hz）
	 * @param isMono 是否单声道（false 为立体声）
	 * @return AudioDevice 实例
	 * 
	 * @throws GdxRuntimeException 如果设备无法创建 */
	public AudioDevice newAudioDevice (int samplingRate, boolean isMono);

	/** 创建新的 {@link AudioRecorder}（音频录制器）。
	 * AudioRecorder 不再使用时必须释放。
	 * 
	 * @param samplingRate 采样率（Hz）
	 * @param isMono 是否单声道录制
	 * @return AudioRecorder 实例
	 * 
	 * @throws GdxRuntimeException 如果录制器无法创建 */
	public AudioRecorder newAudioRecorder (int samplingRate, boolean isMono);

	/**
	 * <p>
	 * 创建新的 {@link Sound}（音效），用于播放如枪声、爆炸等短音频效果。
	 * 音频数据从 {@link FileHandle} 指定的文件中加载。
	 * </p>
	 * 
	 * <p>
	 * <b>注意：</b>完整的音频数据会被加载到 RAM 中，因此不要用此方法加载大文件。
	 * 解码后的音频上限为 1 MB。
	 * </p>
	 * 
	 * <p>
	 * 目前支持的格式：WAV、MP3 和 OGG。
	 * </p>
	 * 
	 * <p>
	 * Sound 不再使用时必须通过 {@link Sound#dispose()} 释放。
	 * </p>
	 * 
	 * @return 新的 Sound 实例
	 * @throws GdxRuntimeException 如果音效无法加载 */
	public Sound newSound (FileHandle fileHandle);

	/** 创建新的 {@link Music}（音乐流）实例，用于播放音乐文件。
	 * 目前支持的格式：WAV、MP3 和 OGG。
	 * Music 不再使用时必须通过 {@link Music#dispose()} 释放。
	 * Music 实例在 {@link ApplicationListener#pause()} 时自动暂停，
	 * 在 {@link ApplicationListener#resume()} 时自动恢复。
	 * 
	 * @param file 文件句柄
	 * @return 新的 Music 实例，如果无法加载则返回 null
	 * @throws GdxRuntimeException 如果音乐无法加载 */
	public Music newMusic (FileHandle file);

	/** 切换音频输出设备。设备标识符可通过 {@link Audio#getAvailableOutputDevices()} 获取。
	 * 传入 null 将切换到自动模式。
	 *
	 * @param deviceIdentifier 设备标识符，或 null 表示自动
	 * @return 是否切换成功 */
	public boolean switchOutputDevice (@Null String deviceIdentifier);

	/** 返回可用的音频输出设备列表。此功能仅在桌面和 Web 上实现。
	 * 注意，在 GWT 上需要将 GwtApplicationConfiguration#fetchAvailableOutputDevices 设置为 true，
	 * 以请求用户权限。在其他平台上返回空数组。出错时也返回空数组。
	 *
	 * @return 可用输出设备名称的数组 */
	public String[] getAvailableOutputDevices ();

}
