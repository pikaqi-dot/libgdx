# libGDX 核心框架架构文档

## 一、整体架构

```
┌─────────────────────────────────────────────────────────┐
│                    你的游戏代码                          │
│              (实现 ApplicationListener)                  │
├─────────────────────────────────────────────────────────┤
│                    ├─ Game + Screen 屏幕管理             │
└──────────────────────────┬──────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────┐
│                Gdx 全局静态入口                          │
│  Gdx.app / Gdx.graphics / Gdx.audio / Gdx.input        │
│  Gdx.files / Gdx.net / Gdx.gl / Gdx.gl20~32            │
└──────────────────────────┬──────────────────────────────┘
                           │
┌──────────┬──────────┬────┴──────┬──────────┬────────────┐
│  app     │ graphics │  audio    │  input   │   files    │
│ 生命周期  │ GL渲染   │ 音效/音乐 │ 键盘/触摸 │  文件IO    │
│ 日志     │ 摄像机   │ 音频设备  │ 加速度计  │            │
│ 偏好设置  │ 纹理/网格 │          │  振动     │    net     │
└──────────┴──────────┴──────────┴──────────┴────────────┘
```

### 生命周期

```
ApplicationListener:
  create()   →   resize()   →   render()循环   →   pause()   →   resume()   →   dispose()
  (创建)          (调整窗口)      (每帧渲染)        (暂停)        (恢复)          (销毁)
```

## 二、根包 `com.badlogic.gdx`

| 接口/类 | 说明 |
|---------|------|
| [`Application`](gdx/src/com/badlogic/gdx/Application.java) | 应用入口，管理 Graphics/Audio/Input/Files/Net 模块 |
| [`ApplicationListener`](gdx/src/com/badlogic/gdx/ApplicationListener.java) | 生命周期回调：create → render → pause → resume → dispose |
| [`ApplicationAdapter`](gdx/src/com/badlogic/gdx/ApplicationAdapter.java) | ApplicationListener 适配器，可只重写需要的方法 |
| [`Game`](gdx/src/com/badlogic/gdx/Game.java) | 屏幕管理器，配合 Screen 实现多屏幕切换 |
| [`Screen`](gdx/src/com/badlogic/gdx/Screen.java) | 屏幕接口（菜单/游戏/设置等） |
| [`Gdx`](gdx/src/com/badlogic/gdx/Gdx.java) | 全局静态入口，所有模块通过 Gdx.xxx 访问 |
| [`Input`](gdx/src/com/badlogic/gdx/Input.java) | 键盘/鼠标/触摸/加速度计/振动 |
| [`Graphics`](gdx/src/com/badlogic/gdx/Graphics.java) | 显示模式/帧率/VSync/OpenGL版本 |
| [`Audio`](gdx/src/com/badlogic/gdx/Audio.java) | 创建 Sound/Music/AudioDevice |
| [`Files`](gdx/src/com/badlogic/gdx/Files.java) | 文件类型：Classpath/Internal/External/Absolute/Local |
| [`Net`](gdx/src/com/badlogic/gdx/Net.java) | HTTP 请求 + TCP Socket |

## 三、`com.badlogic.gdx.math` — 数学工具包

```
math/
├── 向量       Vector(接口) ← Vector2 / Vector3 / Vector4
├── 矩阵       Matrix3(3x3, 2D变换) / Matrix4(4x4, 3D变换)
├── 旋转       Quaternion(四元数, 避免万向锁)
├── 几何体     Rectangle / Circle / Ellipse / Polygon / Polyline / Plane
├── 数学工具   MathUtils(快速三角函数/随机数/插值)
├── 碰撞检测   Intersector(相交测试) / Frustum(视锥体裁剪)
├── 曲线算法   Bezier / BSpline / CatmullRomSpline / Path(接口)
├── 三角剖分   EarClippingTriangulator(耳切法) / DelaunayTriangulator
├── 凸包       ConvexHull(Andrew's Monotone Chain)
├── 空间索引   Octree(八叉树)
└── collision/ BoundingBox(AABB) / OBB / Ray(射线) / Sphere(球体) / Segment(线段)
```

### 关键算法说明

| 算法 | 文件 | 方法 | 说明 |
|------|------|------|------|
| **凸包** | `ConvexHull.java` | `computePolygon()` | Andrew's Monotone Chain，O(n log n)：先按 x 排序，分别构建上下凸包 |
| **耳切法** | `EarClippingTriangulator.java` | `computeTriangles()` | 重复查找"耳朵"（凸顶点且三角形内不含其他点）并切除 |
| **贝塞尔** | `Bezier.java` | `valueAt()` | De Casteljau 递归线性插值算法 |
| **B样条** | `BSpline.java` | `valueAt()` | Cox-de Boor 递归公式 |
| **快速三角** | `MathUtils.java` | `sin()`/`cos()` | 14位查表法，比 Math 快 5-10 倍 |
| **快速取整** | `MathUtils.java` | `floor()`/`ceil()` | IEEE 754 位操作实现 |
| **相交检测** | `Intersector.java` | `isPointInTriangle()` | 重心坐标法 P = A + u(C-A) + v(B-A) |

### 坐标系

- **2D**：屏幕坐标系，原点左上角，Y 轴向下
- **3D**：右手坐标系，X 向右，Y 向上，Z 向屏幕外

## 四、`com.badlogic.gdx.graphics` — 图形渲染包

```
graphics/
├── 摄像机       Camera ← OrthographicCamera(2D) / PerspectiveCamera(3D)
├── 颜色         Color / Colors(颜色常量)
├── 纹理         Texture / TextureData / GLTexture / Cubemap
├── 位图         Pixmap(内存操作) / PixmapIO(PNG读写)
├── 网格         Mesh(顶点数据) / VertexAttribute / VertexAttributes
├── 光标         Cursor(自定义指针)
├── OpenGL       GL20 / GL30 / GL31 / GL32 接口
│
├── g2d/          2D 渲染子系统
│   ├── SpriteBatch     ★核心批处理绘制器
│   ├── Batch           绘制接口
│   ├── Sprite          精灵（位置/旋转/缩放）
│   ├── TextureRegion   纹理区域
│   ├── TextureAtlas    纹理图集（合并多图减少绑定）
│   ├── BitmapFont      位图字体渲染
│   ├── Animation       帧动画
│   ├── NinePatch       九宫格缩放图片
│   └── ParticleEffect  粒子系统
│
├── g3d/          3D 渲染子系统
│   ├── Model / ModelInstance / ModelBatch
│   ├── Material / Environment(光照环境)
│   └── decals/   贴花系统
│
└── glutils/      OpenGL 工具
    ├── ShaderProgram    着色器程序
    ├── FrameBuffer      帧缓冲(FBO, 离屏渲染)
    ├── VertexArray/VBO  顶点数组/缓冲
    └── MipMapGenerator  MipMap 生成
```

### 2D 渲染流程

```java
batch.begin();                          // 开始批处理
batch.draw(texture, x, y);              // 提交绘制命令
batch.draw(region, x, y, w, h);
batch.end();                            // 结束批处理，提交 GPU
```

SpriteBatch 将多个绘制命令合并为一次 OpenGL 调用，大幅减少 draw call 数量。

### 3D 渲染流程

```java
modelBatch.begin(camera);
modelBatch.render(instance, environment);
modelBatch.end();
```

## 五、`com.badlogic.gdx.maps` — 地图系统包

```
maps/
├── Map                 地图基类，包含图层集和属性
├── MapLayer            图层基类（透明度/偏移/视差滚动）
│   └── MapGroupLayer   组图层（图层分组）
├── MapLayers           图层集合
├── MapObject           地图对象基类
├── MapObjects          对象集合
├── MapProperties       属性集合（键值对）
├── MapRenderer         渲染器接口
│
├── objects/            地图对象类型
│   ├── Rectangle/Circle/Ellipse/Point  几何形状
│   ├── Polygon/Polyline                多边形/折线
│   ├── TextMapObject                   文本对象
│   └── TextureMapObject                纹理对象
│
└── tiled/              Tiled 编辑器支持
    ├── TiledMap                       瓦片地图
    ├── TiledMapTileLayer  (Cell 网格)  瓦片图层
    ├── TiledMapTileSet                 瓦片集合
    ├── TmxMapLoader   (XML格式)        加载器
    ├── TmjMapLoader   (JSON格式)        加载器
    ├── AtlasTmxMapLoader               图集优化加载器
    │
    ├── renderers/      渲染器
    │   ├── OrthogonalTiledMapRenderer         ★正交(2D俯视)
    │   ├── IsometricTiledMapRenderer          等距(45度伪3D)
    │   ├── IsometricStaggeredTiledMapRenderer 交错等距
    │   ├── HexagonalTiledMapRenderer          六边形
    │   └── OrthoCachedTiledMapRenderer        缓存优化
    │
    ├── tiles/          瓦片类型
    │   ├── StaticTiledMapTile      静态瓦片
    │   └── AnimatedTiledMapTile    动画瓦片(帧序列)
    │
    └── objects/TiledMapTileMapObject 瓦片地图对象
```

### 使用流程

```java
// 1. 加载地图
TmxMapLoader loader = new TmxMapLoader();
TiledMap map = loader.load("map.tmx");

// 2. 创建渲染器
OrthogonalTiledMapRenderer renderer = new OrthogonalTiledMapRenderer(map);

// 3. 渲染
renderer.setView(camera);
renderer.render();

// 4. 访问图层和对象
TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("ground");
Cell cell = layer.getCell(10, 5);
```

### 图层属性

| 属性 | 范围 | 说明 |
|------|------|------|
| `opacity` | [0,1] | 透明度，父子叠加 |
| `tintColor` | Color | 色调，颜色乘法叠加 |
| `parallaxX/Y` | float | 视差滚动因子，多层背景效果 |
| `offsetX/Y` | float | 偏移，子累加到父 |

## 六、`com.badlogic.gdx.scenes.scene2d` — 2D 场景图 UI 系统

```
scene2d/
├── Stage            ★舞台（场景图根节点，管理输入和渲染）
├── Actor            ★节点基类（位置/大小/缩放/旋转/颜色/事件）
├── Group            Actor容器（可嵌套，支持变换）
├── Event            事件基类
├── EventListener    事件监听器接口
├── InputEvent       输入事件
│
├── actions/         动作系统
│   ├── TemporalAction         随时间变化的动作(基类)
│   ├── MoveTo/By              移动
│   ├── RotateTo/By            旋转
│   ├── ScaleTo/By             缩放
│   ├── AlphaAction            透明度变化
│   ├── Sequence/Parallel      顺序/并行组合
│   ├── Repeat/Delay           重复/延迟
│   └── Actions                 ★动作工厂工具类
│
├── ui/              ★UI控件
│   ├── Button / TextButton / ImageButton / CheckBox  按钮类
│   ├── Label / TextField / TextArea                  文本类
│   ├── Table     ★表格布局容器(类似HTML表格)
│   ├── Container / Stack                              容器类
│   ├── List / SelectBox / Tree                        选择类
│   ├── Slider / ProgressBar / Touchpad                数值类
│   ├── ScrollPane / SplitPane / Window / Dialog       面板类
│   └── Skin     ★皮肤资源管理器
│
└── utils/          UI工具
    ├── ClickListener / DragListener / DragAndDrop      交互
    ├── FocusListener / ChangeListener                  事件
    ├── Drawable / BaseDrawable                         可绘制对象
    ├── Selection / ArraySelection                      选择管理
    └── ScissorStack                                    裁剪栈
```

### 使用流程

```java
// 1. 创建舞台
Stage stage = new Stage(viewport);
Gdx.input.setInputProcessor(stage);

// 2. 添加 Actor
TextButton button = new TextButton("Click", skin);
stage.addActor(button);

// 3. 每帧更新和渲染
stage.act(delta);   // 更新逻辑和动作
stage.draw();       // 渲染场景
```

### 事件系统

事件从 Stage 分发到 Actor 层次结构：
1. **捕获阶段** — 从父到子传递
2. **目标阶段** — 在目标 Actor 上触发
3. **冒泡阶段** — 从子到父传递
4. `Event.stop()` — 停止传播

## 七、`com.badlogic.gdx.utils` — 工具包

```
utils/
├── 集合类（无装箱开销的高性能实现）
│   ├── Array / BooleanArray / ByteArray / CharArray
│   │   FloatArray / IntArray / LongArray / ShortArray
│   ├── ObjectMap / IntMap / LongMap / IdentityMap
│   │   ObjectIntMap / ObjectFloatMap / ObjectLongMap
│   ├── ObjectSet / IntSet / LongSet / IdentitySet
│   ├── OrderedMap / OrderedSet
│   ├── Queue / LongQueue / BinaryHeap(优先队列)
│   └── DelayedRemovalArray(遍历安全删除)
│
├── 对象池
│   ├── Pool(接口) / Pools(工具类)
│   └── DefaultPool / FlushablePool / ReflectionPool
│
├── JSON 处理
│   ├── Json(序列化/反序列化)
│   ├── JsonReader / JsonWriter
│   └── JsonValue(JSON树节点)
│
├── 排序算法
│   ├── Sort(TimSort: 归并+插入混合排序)
│   ├── QuickSelect(快速选择, 第k小元素)
│   └── TimSort / ComparableTimSort
│
├── 时间/性能
│   ├── Timer      渲染线程定时器（延时/重复任务）
│   ├── TimeUtils  纳秒时间工具
│   └── PerformanceCounter 性能计数器
│
├── I/O工具
│   ├── DataInput / DataOutput / BufferUtils
│   ├── StreamUtils / LittleEndianInputStream
│   └── Base64Coder / PropertiesUtils
│
├── 日志        Logger(debug/info/error)
├── 国际化      I18NBundle(多语言支持)
├── 压缩        compression/LZMA
│
├── 视口        viewport/
│   ├── FitViewport       保持比例，黑边
│   ├── FillViewport      保持比例，裁剪
│   ├── StretchViewport   拉伸填满
│   ├── ScreenViewport    1:1 像素映射
│   ├── ExtendViewport    保持比例，扩展视野
│   └── ScalingViewport   使用 Scaling 策略
│
├── 异步       async/(异步任务执行器)
├── 空间索引   QuadTreeFloat(四叉树)
└── 反射       reflect/(反射封装)
```

### 集合类特点

libGDX 集合类相比 Java 标准库：

| 特性 | 说明 |
|------|------|
| **避免自动装箱** | IntMap/IntArray 直接存储基本类型 |
| **开放地址法** | ObjectMap 使用开放地址+二次探测，比 HashMap 更快 |
| **有序变体** | OrderedMap/OrderedSet 保持插入顺序 |
| **遍历安全** | DelayedRemovalArray 支持遍历时安全删除 |

### 二叉堆 BinaryHeap

```java
heap.add(node);   // 插入到堆尾 → 上浮(swim)恢复堆性质
heap.pop();       // 堆顶与堆尾交换 → 移除 → 下沉(sink)
// swim: 与父节点比较交换直到堆顶
// sink: 与较小子节点交换直到满足堆性质
```

## 八、`com.badlogic.gdx.audio` — 音频包

```
audio/
├── Sound       短音效（完全加载到内存，可并行播放）
├── Music       音乐流（流式传输，适合背景音乐）
├── AudioDevice 音频设备（直接写入 PCM 数据）
└── AudioRecorder 音频录制器
```

| 类型 | 用途 | 加载方式 | 并行播放 |
|------|------|----------|----------|
| Sound | 枪声/爆炸/UI音效 | 完全加载到内存 | ✅ |
| Music | 背景音乐 | 流式传输 | ❌ |

支持格式：WAV、MP3、OGG

## 九、`com.badlogic.gdx.net` — 网络通信包

```
net/
├── HttpRequest / HttpResponse     HTTP请求/响应
├── HttpMethods                    GET/POST/PUT/DELETE
├── Socket(客户端) / ServerSocket(服务端)  TCP
└── HttpRequestBuilder             请求构建器
```

### HTTP 请求示例

```java
HttpRequest request = new HttpRequest(HttpMethods.GET);
request.setUrl("https://api.example.com/data");

Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
    public void handleHttpResponse(HttpResponse response) {
        String result = response.getResultAsString();
    }
    public void failed(Throwable t) { }
});
```

## 十、`com.badlogic.gdx.assets` — 资源管理包

```
assets/
├── AssetManager         ★资源管理器（加载/卸载/引用计数）
├── AssetDescriptor      资源描述符
├── AssetLoadingTask     加载任务
│
└── loaders/             资源加载器
    ├── TextureLoader / SoundLoader / MusicLoader
    ├── BitmapFontLoader / SkinLoader
    ├── TextureAtlasLoader / ModelLoader
    └── resolvers/       文件路径解析器
        ├── InternalFileHandleResolver
        ├── ExternalFileHandleResolver
        ├── AbsoluteFileHandleResolver
        ├── ClasspathFileHandleResolver
        └── ResolutionFileResolver(分辨率适配)
```

### 使用示例

```java
AssetManager manager = new AssetManager();
manager.load("bg.png", Texture.class);   // 异步加载
manager.finishLoading();                  // 等待完成
Texture bg = manager.get("bg.png", Texture.class);  // 获取
manager.unload("bg.png");                 // 卸载
```

AssetManager 特性：异步加载、引用计数、上下文丢失后自动重载、依赖管理。
