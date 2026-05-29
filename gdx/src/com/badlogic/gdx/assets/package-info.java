/**
 * <b>资源管理包 — 异步加载与生命周期管理</b>
 * 
 * <h2>架构</h2>
 * 
 * <pre>
 * assets/
 * ├── AssetManager          资源管理器（加载/卸载/引用计数）
 * ├── AssetDescriptor       资源描述符（类型+路径+参数）
 * ├── AssetLoaderParameters 加载参数基类
 * ├── AssetLoadingTask      加载任务
 * ├── AssetErrorListener    加载错误监听器
 * └── loaders/              资源加载器
 *     ├── AssetLoader / SynchronousAssetLoader / AsynchronousAssetLoader
 *     ├── TextureLoader / PixmapLoader / CubemapLoader
 *     ├── SoundLoader / MusicLoader
 *     ├── BitmapFontLoader / SkinLoader
 *     ├── TextureAtlasLoader / ShaderProgramLoader
 *     ├── ModelLoader / ParticleEffectLoader
 *     ├── I18NBundleLoader
 *     └── resolvers/        文件路径解析器
 *         ├── InternalFileHandleResolver // 内部文件
 *         ├── ExternalFileHandleResolver // 外部文件
 *         ├── AbsoluteFileHandleResolver  // 绝对路径
 *         ├── ClasspathFileHandleResolver  // Classpath
 *         ├── LocalFileHandleResolver     // 本地文件
 *         ├── PrefixFileHandleResolver    // 前缀路由
 *         └── ResolutionFileResolver      // 分辨率适配
 * </pre>
 * 
 * <h2>使用示例</h2>
 * 
 * <pre>
 * AssetManager manager = new AssetManager();
 * 
 * // 异步加载
 * manager.load("bg.png", Texture.class);
 * manager.load("sound.wav", Sound.class);
 * 
 * // 等待加载完成
 * manager.finishLoading();
 * 
 * // 获取资源
 * Texture bg = manager.get("bg.png", Texture.class);
 * 
 * // 卸载资源
 * manager.unload("bg.png");
 * </pre>
 * 
 * AssetManager 的特性：
 * - 异步加载，不会阻塞主线程
 * - 引用计数，多个地方引用同一资源不会重复加载
 * - OpenGL 上下文丢失后自动重载托管资源
 * - 支持依赖管理（如加载 Skin 时会自动加载其引用的纹理）
 * 
 * @see com.badlogic.gdx.assets.AssetManager
 * @see com.badlogic.gdx.assets.loaders.AssetLoader
 */
package com.badlogic.gdx.assets;
