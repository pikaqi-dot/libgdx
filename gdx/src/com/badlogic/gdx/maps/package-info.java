/**
 * <b>地图系统包 — 通用地图与 Tiled 编辑器支持</b>
 * 
 * <h2>架构</h2>
 * 
 * <pre>
 * maps/
 * ├── Map               地图基类，包含图层集和属性
 * ├── MapLayer          图层基类（透明度/偏移/视差滚动）
 * │   └── MapGroupLayer 组图层（图层分组，包含子图层）
 * ├── MapLayers         图层集合（按顺序排列）
 * ├── MapObject         地图对象基类
 * ├── MapObjects        对象集合（可按名称查找）
 * ├── MapProperties     属性集合（键值对）
 * ├── MapRenderer       渲染器接口
 * ├── objects/          地图对象类型
 * │   ├── Rectangle/Circle/Ellipse/Point 几何形状
 * │   ├── Polygon/Polyline              多边形/折线
 * │   ├── TextMapObject                 文本对象
 * │   └── TextureMapObject              纹理对象
 * ├── tiled/            Tiled 编辑器支持（TMX/XML 和 TMJ/JSON 格式）
 * │   ├── TiledMap                      瓦片地图
 * │   ├── TiledMapTileLayer             瓦片图层（Cell 网格）
 * │   ├── TiledMapTileSet/TileSets      瓦片集合
 * │   ├── TmxMapLoader / TmjMapLoader   格式加载器
 * │   ├── AtlasTmxMapLoader             纹理图集优化加载器
 * │   ├── renderers/                    渲染器
 * │   │   ├── OrthogonalTiledMapRenderer     正交（2D俯视）
 * │   │   ├── IsometricTiledMapRenderer      等距（45度伪3D）
 * │   │   ├── IsometricStaggeredTiledMapRenderer 交错等距
 * │   │   ├── HexagonalTiledMapRenderer      六边形
 * │   │   └── OrthoCachedTiledMapRenderer    缓存优化正交
 * │   ├── tiles/       瓦片类型
 * │   │   ├── StaticTiledMapTile              静态瓦片
 * │   │   └── AnimatedTiledMapTile            动画瓦片（帧序列）
 * │   └── objects/TiledMapTileMapObject 瓦片地图对象
 * └── ImageResolver    图片解析器接口
 * </pre>
 * 
 * <h2>使用流程</h2>
 * 
 * <pre>
 * // 1. 加载地图
 * TmxMapLoader loader = new TmxMapLoader();
 * TiledMap map = loader.load("map.tmx");
 * 
 * // 2. 创建渲染器
 * OrthogonalTiledMapRenderer renderer = 
 *     new OrthogonalTiledMapRenderer(map);
 * 
 * // 3. 渲染
 * renderer.setView(camera);
 * renderer.render();
 * 
 * // 4. 访问图层和对象
 * TiledMapTileLayer layer = 
 *     (TiledMapTileLayer) map.getLayers().get("ground");
 * Cell cell = layer.getCell(10, 5);
 * </pre>
 * 
 * <h2>图层属性</h2>
 * 
 * 每个图层 {@link com.badlogic.gdx.maps.MapLayer} 支持：
 * - <b>透明度</b> opacity [0,1]，父子图层透明度叠加
 * - <b>色调</b> tintColor，通过颜色乘法叠加
 * - <b>视差滚动</b> parallaxX/Y，多层背景效果
 * - <b>偏移量</b> offsetX/Y，子图层偏移累加到父图层
 * - <b>嵌套</b> parent 属性，多层图层结构
 * 
 * @see com.badlogic.gdx.maps.Map
 * @see com.badlogic.gdx.maps.tiled.TiledMap
 * @see com.badlogic.gdx.maps.tiled.TmxMapLoader
 */
package com.badlogic.gdx.maps;
