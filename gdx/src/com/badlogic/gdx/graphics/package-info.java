/**
 * <b>图形渲染包 — 2D/3D 渲染管线</b>
 * 
 * <h2>架构</h2>
 * 
 * <pre>
 * graphics/
 * ├── 摄像机           Camera(基类) ← OrthographicCamera(2D) / PerspectiveCamera(3D)
 * ├── 颜色             Color / Colors(颜色常量)
 * ├── 纹理             Texture / TextureData / GLTexture / Cubemap / Texture3D / TextureArray
 * ├── 像素图           Pixmap(内存位图，可读写像素) / PixmapIO(PNG读写)
 * ├── 网格             Mesh(顶点数据) / VertexAttribute / VertexAttributes
 * ├── 光标             Cursor(自定义鼠标指针)
 * ├── FPS日志          FPSLogger(每秒输出帧率)
 * ├── OpenGL           GL20 / GL30 / GL31 / GL32 接口
 * ├── g2d/             2D渲染子系统
 * │   ├── SpriteBatch      核心批处理绘制器
 * │   ├── Batch            绘制接口
 * │   ├── Sprite           精灵（位置/旋转/缩放）
 * │   ├── TextureRegion    纹理区域
 * │   ├── TextureAtlas     纹理图集（合并多图减少绑定）
 * │   ├── BitmapFont       位图字体渲染
 * │   ├── Animation        帧动画
 * │   ├── NinePatch        九宫格缩放图片
 * │   ├── ParticleEffect   粒子系统
 * │   └── PolygonSprite    多边形精灵
 * ├── g3d/             3D渲染子系统
 * │   ├── Model / ModelInstance / ModelBatch
 * │   ├── Material / Environment(光照环境)
 * │   ├── Attribute / Attributes(材质属性)
 * │   ├── decals/          贴花系统
 * │   └── particles/       3D粒子
 * └── glutils/         OpenGL工具
 *     ├── ShaderProgram    着色器程序
 *     ├── FrameBuffer      帧缓冲(FBO,离屏渲染)
 *     ├── VertexArray/VertexBufferObject  顶点数组/缓冲
 *     ├── IndexBufferObject 索引缓冲
 *     ├── MipMapGenerator  MipMap生成
 *     └── GLVersion        OpenGL版本信息
 * </pre>
 * 
 * <h2>2D 渲染流程</h2>
 * 
 * <pre>
 * batch.begin();       // 开始批处理
 * batch.draw(texture, x, y);  // 提交绘制命令
 * batch.draw(region, x, y, w, h);
 * batch.end();         // 结束批处理，提交 GPU 渲染
 * </pre>
 * 
 * SpriteBatch 将多个绘制命令合并为一次 OpenGL 调用，
 * 大幅减少 draw call 数量，提高性能。
 * 
 * <h2>3D 渲染流程</h2>
 * 
 * <pre>
 * modelBatch.begin(camera);
 * modelBatch.render(instance, environment);
 * modelBatch.end();
 * </pre>
 * 
 * @see com.badlogic.gdx.graphics.g2d.SpriteBatch
 * @see com.badlogic.gdx.graphics.Camera
 * @see com.badlogic.gdx.graphics.glutils.ShaderProgram
 */
package com.badlogic.gdx.graphics;
