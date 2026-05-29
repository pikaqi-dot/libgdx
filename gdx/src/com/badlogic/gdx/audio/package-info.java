/**
 * <b>音频包 — 音效/音乐/音频设备</b>
 * 
 * <h2>架构</h2>
 * 
 * <pre>
 * audio/
 * ├── Sound       短音效接口（完全加载到内存，可并行播放多次）
 * ├── Music       音乐流接口（流式传输，适合背景音乐）
 * ├── AudioDevice 音频设备（直接写入 PCM 数据）
 * └── AudioRecorder 音频录制器（录制 PCM 数据）
 * </pre>
 * 
 * <h2>使用场景</h2>
 * 
 * <ul>
 *   <li><b>Sound</b> — 枪声、爆炸、UI点击等短促音效，用 {@link com.badlogic.gdx.audio.Sound#play()} 播放</li>
 *   <li><b>Music</b> — 背景音乐，用 {@link com.badlogic.gdx.audio.Music#play()} 开始，
 *       支持循环({@link com.badlogic.gdx.audio.Music#setLooping(boolean)})、暂停、音量控制</li>
 *   <li><b>AudioDevice</b> — 需要直接合成/处理音频数据时使用</li>
 * </ul>
 * 
 * 支持的音频格式：WAV、MP3、OGG
 * 
 * @see com.badlogic.gdx.audio.Sound
 * @see com.badlogic.gdx.audio.Music
 * @see com.badlogic.gdx.Audio
 */
package com.badlogic.gdx.audio;
