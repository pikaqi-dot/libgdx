/**
 * <b>libGDX 核心框架 — 跨平台游戏开发框架</b>
 * 
 * <h2>架构总览</h2>
 * 
 * libGDX 采用分层架构，核心模块通过 {@link com.badlogic.gdx.Gdx} 类的静态字段全局访问：
 * 
 * <pre>
 * ┌─────────────────────────────────────────────────────┐
 * │                  你的游戏代码                        │
 * │          (实现 ApplicationListener)                 │
 * ├─────────────────────────────────────────────────────┤
 * │                   ├─ Game + Screen                 │
 * └──────────────────────┬──────────────────────────────┘
 *                        │
 * ┌──────────────────────▼──────────────────────────────┐
 * │              Gdx 全局静态入口                        │
 * │  Gdx.app / Gdx.graphics / Gdx.audio / Gdx.input    │
 * │  Gdx.files / Gdx.net / Gdx.gl / Gdx.gl20~32        │
 * └──────────────────────┬──────────────────────────────┘
 *                        │
 * ┌──────────┬──────────┬┴──────────┬──────────┬────────┐
 * │  app     │ graphics │  audio    │  input   │ files  │
 * │ 生命周期  │ GL渲染   │ 音效/音乐 │ 键盘/触摸│ 文件IO  │
 * │ 日志     │ 摄像机   │ 音频设备  │ 加速度计 │        │
 * │ 偏好设置 │ 纹理/网格│          │ 振动     │  net   │
 * └──────────┴──────────┴──────────┴──────────┴────────┘
 * </pre>
 * 
 * <h2>核心包说明</h2>
 * 
 * <dl>
 *   <dt>{@link com.badlogic.gdx}（本包）</dt>
 *   <dd>核心接口和入口：Application(生命周期)、Graphics(图形)、Audio(音频)、
 *       Input(输入)、Files(文件)、Net(网络)</dd>
 * 
 *   <dt>{@link com.badlogic.gdx.math}</dt>
 *   <dd>数学工具：Vector2/3/4(向量)、Matrix3/4(矩阵)、Quaternion(四元数)、
 *       MathUtils(快速数学函数)、Intersector(碰撞检测)、各种曲线算法</dd>
 * 
 *   <dt>{@link com.badlogic.gdx.graphics}</dt>
 *   <dd>图形渲染：Camera(摄像机)、Texture(纹理)、Pixmap(位图)、Mesh(网格)、
 *       g2d(2D渲染：SpriteBatch/BitmapFont/Animation)、
 *       g3d(3D渲染：Model/Material/Light)、glutils(GL工具)</dd>
 * 
 *   <dt>{@link com.badlogic.gdx.audio}</dt>
 *   <dd>音频处理：Sound(短音效)、Music(背景音乐流)、AudioDevice(音频设备)</dd>
 * 
 *   <dt>{@link com.badlogic.gdx.maps}</dt>
 *   <dd>地图系统：Map(地图基类)、MapLayer(图层)、MapObject(地图对象)、
 *       tiled(Tiled编辑器地图加载与渲染)</dd>
 * 
 *   <dt>{@link com.badlogic.gdx.scenes.scene2d}</dt>
 *   <dd>2D场景图UI系统：Stage(舞台)、Actor(节点)、Group(容器)、
 *       ui(控件：Button/Label/TextField/Table/Window等)、
 *       actions(动作动画系统)、utils(工具：点击/拖拽/焦点)</dd>
 * 
 *   <dt>{@link com.badlogic.gdx.utils}</dt>
 *   <dd>工具集合：ObjectMap/Array等高性能集合类、Pool(对象池)、
 *       Json(JSON序列化)、Timer(定时器)、Logger(日志)、
 *       viewport(视口适配)、压缩算法(LZMA)</dd>
 * 
 *   <dt>{@link com.badlogic.gdx.assets}</dt>
 *   <dd>资源管理：AssetManager(异步资源加载与生命周期管理)</dd>
 * 
 *   <dt>{@link com.badlogic.gdx.net}</dt>
 *   <dd>网络通信：HTTP请求、TCP Socket(客户端/服务端)</dd>
 * </dl>
 * 
 * <h2>生命周期</h2>
 * 
 * 应用启动时，后端创建 Application 实例，调用 {@link ApplicationListener#create()}。
 * 随后每帧调用 {@link ApplicationListener#render()}，窗口变化时调用 resize()。
 * 应用切到后台时调用 pause()，返回时调用 resume()，销毁时调用 dispose()。
 * 
 * 使用 {@link com.badlogic.gdx.Game} 类配合 {@link com.badlogic.gdx.Screen} 可以
 * 方便地管理多个游戏屏幕（菜单、游戏、设置等）。
 * 
 * <h2>后端支持</h2>
 * 
 * libGDX 通过不同后端实现跨平台：
 * - LWJGL3 后端 (Desktop, Windows/Mac/Linux)
 * - Android 后端
 * - GWT/WebGL 后端 (HTML5)
 * - RoboVM 后端 (iOS)
 *
 * @see com.badlogic.gdx.ApplicationListener
 * @see com.badlogic.gdx.Gdx
 */
package com.badlogic.gdx;
