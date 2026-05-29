/**
 * <b>工具包 — 高性能集合/对象池/JSON/工具类</b>
 * 
 * <h2>架构</h2>
 * 
 * <pre>
 * utils/
 * ├── 集合类（无装箱开销的高性能实现）
 * │   ├── Array / BooleanArray / ByteArray / CharArray
 * │   │   FloatArray / IntArray / LongArray / ShortArray
 * │   ├── ObjectMap / IntMap / LongMap / IdentityMap
 * │   │   ObjectIntMap / ObjectFloatMap / ObjectLongMap
 * │   │   IntIntMap / IntFloatMap
 * │   ├── ObjectSet / IntSet / LongSet / IdentitySet
 * │   ├── OrderedMap / OrderedSet
 * │   ├── Queue / LongQueue
 * │   ├── DelayedRemovalArray / SnapshotArray（遍历安全）
 * │   └── BinaryHeap（二叉堆，优先队列）
 * ├── 对象池
 * │   ├── Pool（池接口） / Pools（工具类）
 * │   ├── DefaultPool / FlushablePool / ReflectionPool
 * │   └── PooledLinkedList（池化链表）
 * ├── JSON 处理
 * │   ├── Json（序列化/反序列化）
 * │   ├── JsonReader / JsonWriter（读写器）
 * │   └── JsonValue（JSON树节点）
 * ├── 排序算法
 * │   ├── Sort（TimSort：归并+插入混合排序）
 * │   ├── QuickSelect（快速选择，第k小元素）
 * │   └── Select / TimSort / ComparableTimSort
 * ├── 时间/性能
 * │   ├── Timer（渲染线程定时器）
 * │   ├── TimeUtils（纳秒时间）
 * │   └── PerformanceCounter（性能计数器）
 * ├── I/O工具
 * │   ├── DataInput / DataOutput / BufferUtils
 * │   ├── StreamUtils / LittleEndianInputStream
 * │   └── Base64Coder / PropertiesUtils
 * ├── 日志        Logger（debug/info/error三级日志）
 * ├── 国际化      I18NBundle（多语言支持）
 * ├── 压缩        compression/LZMA（LZMA压缩算法）
 * ├── 视口        viewport/（屏幕适配策略）
 * │   ├── FitViewport / FillViewport / StretchViewport
 * │   ├── ScreenViewport / ExtendViewport / ScalingViewport
 * │   └── Viewport（基类）
 * ├── 异步       async/（异步任务执行器）
 * ├── 空间索引   QuadTreeFloat（四叉树）
 * └── 反射       reflect/（反射封装工具）
 * </pre>
 * 
 * <h2>集合类特点</h2>
 * 
 * libGDX 的集合类相比 Java 标准库有以下优化：
 * <ul>
 *   <li><b>避免自动装箱</b> — IntMap/IntArray 等直接存储基本类型</li>
 *   <li><b>开放地址法</b> — ObjectMap/IntMap 使用开放地址+二次探测，比 HashMap 更快</li>
 *   <li><b>有序变体</b> — OrderedMap/OrderedSet 保持插入顺序</li>
 *   <li><b>遍历安全</b> — DelayedRemovalArray 支持遍历时安全删除</li>
 * </ul>
 * 
 * @see com.badlogic.gdx.utils.ObjectMap
 * @see com.badlogic.gdx.utils.Pool
 * @see com.badlogic.gdx.utils.Json
 * @see com.badlogic.gdx.utils.viewport.Viewport
 */
package com.badlogic.gdx.utils;
