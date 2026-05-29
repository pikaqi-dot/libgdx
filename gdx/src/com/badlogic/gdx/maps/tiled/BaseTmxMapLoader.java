/*******************************************************************************
 * <b>TMX 格式（XML）Tiled 地图加载器基类。</b>
 * 
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

package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.ImageResolver;
import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.XmlReader.Element;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

/** <b>TMX (XML) 格式 Tiled 地图加载器基类。</b>
 * 解析 Tiled 编辑器导出的 .tmx (XML 格式) 地图文件。
 * 处理以下 TMX 数据格式：
 * - 瓦片层数据编码：CSV / Base64 / Base64+GZip / Base64+Zlib
 * - 地图对象：矩形、圆形、椭圆、多边形、折线、点、文本、瓦片对象
 * - 瓦片翻转标志：水平翻转、垂直翻转、对角线翻转（编码在 GID 高位）
 * - 图块集：内联 tileset 或外部 .tsx 文件引用
 * - 模板对象：.tx 文件
 * - 图层分组、图片图层
 * - 视差滚动因子（父子累乘）
 * - Tiled 工程文件 class 属性系统 */
public abstract class BaseTmxMapLoader<P extends BaseTiledMapLoader.Parameters> extends BaseTiledMapLoader<P> {

	/** XML 解析器，用于解析 .tmx 文件 */
	protected XmlReader xml = new XmlReader();
	/** XML 根元素 */
	protected Element root;

	/** 模板对象缓存，key=.tx 文件路径，value=解析后的 Element */
	protected ObjectMap<String, Element> templateCache;

	public BaseTmxMapLoader (FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public Array<AssetDescriptor> getDependencies (String fileName, FileHandle tmxFile, P parameter) {
		this.root = xml.parse(tmxFile);

		TextureLoader.TextureParameter textureParameter = new TextureLoader.TextureParameter();
		if (parameter != null) {
			textureParameter.genMipMaps = parameter.generateMipMaps;
			textureParameter.minFilter = parameter.textureMinFilter;
			textureParameter.magFilter = parameter.textureMagFilter;
		}

		return getDependencyAssetDescriptors(tmxFile, textureParameter);
	}

	/** 加载 Tiled 地图数据的核心方法。
	 * 
	 * 解析流程：
	 * 1. 读取 map 根节点的属性（orientation、width、height、tilewidth、tileheight、stagger 等）
	 * 2. 处理交错地图(staggered)的像素尺寸修正
	 * 3. 加载地图级自定义属性
	 * 4. 读取所有 tileset 定义（内联或外部引用）
	 * 5. 逐层加载（瓦片层、对象层、图片层、组层）
	 * 6. 更新视差滚动因子：Tiled 中图层的最终视差 = 自身系数 × 所有父图层系数的累乘
	 * 7. 执行所有延后回调
	 *
	 * @param tmxFile .tmx 文件句柄
	 * @param parameter 加载参数
	 * @param imageResolver 图片解析器
	 * @return 加载完成的 TiledMap */
	@Override
	protected TiledMap loadTiledMap (FileHandle tmxFile, P parameter, ImageResolver imageResolver) {
		this.map = new TiledMap();
		this.idToObject = new IntMap<>();
		this.runOnEndOfLoadTiled = new Array<>();
		this.templateCache = new ObjectMap<>();

		if (parameter != null) {
			this.convertObjectToTileSpace = parameter.convertObjectToTileSpace;
			this.flipY = parameter.flipY;
			loadProjectFile(parameter.projectFilePath);
		} else {
			this.convertObjectToTileSpace = false;
			this.flipY = true;
		}

		// === 读取 map 根节点属性 ===
		String mapOrientation = root.getAttribute("orientation", null);
		int mapWidth = root.getIntAttribute("width", 0);
		int mapHeight = root.getIntAttribute("height", 0);
		int tileWidth = root.getIntAttribute("tilewidth", 0);
		int tileHeight = root.getIntAttribute("tileheight", 0);
		int hexSideLength = root.getIntAttribute("hexsidelength", 0);
		String staggerAxis = root.getAttribute("staggeraxis", null);
		String staggerIndex = root.getAttribute("staggerindex", null);
		String mapBackgroundColor = root.getAttribute("backgroundcolor", null);

		// 存储到地图属性中，供渲染器使用
		MapProperties mapProperties = map.getProperties();
		if (mapOrientation != null) mapProperties.put("orientation", mapOrientation);
		mapProperties.put("width", mapWidth);
		mapProperties.put("height", mapHeight);
		mapProperties.put("tilewidth", tileWidth);
		mapProperties.put("tileheight", tileHeight);
		mapProperties.put("hexsidelength", hexSideLength);
		if (staggerAxis != null) mapProperties.put("staggeraxis", staggerAxis);
		if (staggerIndex != null) mapProperties.put("staggerindex", staggerIndex);
		if (mapBackgroundColor != null) mapProperties.put("backgroundcolor", mapBackgroundColor);
		this.mapTileWidth = tileWidth;
		this.mapTileHeight = tileHeight;
		this.mapWidthInPixels = mapWidth * tileWidth;
		this.mapHeightInPixels = mapHeight * tileHeight;

		// 特殊处理交错(staggered)地图的像素尺寸计算
		// 在交错布局中，奇数列向下偏移半块，所以总宽度需要加半个瓦片
		if (mapOrientation != null) {
			if ("staggered".equals(mapOrientation)) {
				if (mapHeight > 1) {
					this.mapWidthInPixels += tileWidth / 2;
					this.mapHeightInPixels = mapHeightInPixels / 2 + tileHeight / 2;
				}
			}
		}

		// 加载地图级自定义属性
		Element properties = root.getChildByName("properties");
		if (properties != null) {
			loadProperties(map.getProperties(), properties);
		}

		// 加载所有 tileset
		Array<Element> tilesets = root.getChildrenByName("tileset");
		for (Element element : tilesets) {
			TiledMapTileSet tileSet = loadTileSet(element, tmxFile, imageResolver);
			root.removeChild(element);
			if (tileSet != null) {
				map.getTileSets().addTileSet(tileSet);
			}
		}

		// 逐层加载（自动识别图层类型）
		for (int i = 0, j = root.getChildCount(); i < j; i++) {
			Element element = root.getChild(i);
			loadLayer(map, map.getLayers(), element, tmxFile, imageResolver);
		}

		// === 更新视差滚动因子 ===
		// Tiled 中，子图层的最终视差 = 子图层系数 × 所有父图层系数的累乘
		// 这里通过 BFS（广度优先）遍历所有组层，将父层级的视差因子传播给子层
		final Array<MapGroupLayer> groups = map.getLayers().getByType(MapGroupLayer.class);
		while (groups.notEmpty()) {
			final MapGroupLayer group = groups.first();
			groups.removeIndex(0);

			for (MapLayer child : group.getLayers()) {
				child.setParallaxX(child.getParallaxX() * group.getParallaxX());
				child.setParallaxY(child.getParallaxY() * group.getParallaxY());
				if (child instanceof MapGroupLayer) {
					groups.add((MapGroupLayer)child); // 继续处理嵌套组
				}
			}
		}

		// 执行加载结束后的延后回调
		for (Runnable runnable : runOnEndOfLoadTiled) {
			runnable.run();
		}
		runOnEndOfLoadTiled = null;

		return map;
	}

	/** 加载单一图层。根据 XML 元素名称自动识别图层类型。
	 * - "group" → 组图层（递归包含子图层）
	 * - "layer" → 瓦片图层（二维瓦片网格）
	 * - "objectgroup" → 对象图层（地图对象集合）
	 * - "imagelayer" → 图片图层 */
	protected void loadLayer (TiledMap map, MapLayers parentLayers, Element element, FileHandle tmxFile,
		ImageResolver imageResolver) {
		String name = element.getName();
		if (name.equals("group")) {
			loadLayerGroup(map, parentLayers, element, tmxFile, imageResolver);
		} else if (name.equals("layer")) {
			loadTileLayer(map, parentLayers, element);
		} else if (name.equals("objectgroup")) {
			loadObjectGroup(map, parentLayers, element, tmxFile);
		} else if (name.equals("imagelayer")) {
			loadImageLayer(map, parentLayers, element, tmxFile, imageResolver);
		}
	}

	/** 加载组图层（递归）。组图层可以包含子图层、子组，形成树状结构。 */
	protected void loadLayerGroup (TiledMap map, MapLayers parentLayers, Element element, FileHandle tmxFile,
		ImageResolver imageResolver) {
		if (element.getName().equals("group")) {
			MapGroupLayer groupLayer = new MapGroupLayer();
			loadBasicLayerInfo(groupLayer, element);

			Element properties = element.getChildByName("properties");
			if (properties != null) {
				loadProperties(groupLayer.getProperties(), properties);
			}

			// 递归加载子图层
			for (int i = 0, j = element.getChildCount(); i < j; i++) {
				Element child = element.getChild(i);
				loadLayer(map, groupLayer.getLayers(), child, tmxFile, imageResolver);
			}

			// 设置父子关系
			for (MapLayer layer : groupLayer.getLayers()) {
				layer.setParent(groupLayer);
			}

			parentLayers.add(groupLayer);
		}
	}

	/** 加载瓦片图层。从 <data> 元素中解析瓦片 GID 并创建 Cell。
	 * 
	 * 瓦片 GID 编码规则（Tiled 标准）：
	 * - 位 31 = 水平翻转标志 (FLAG_FLIP_HORIZONTALLY)
	 * - 位 30 = 垂直翻转标志 (FLAG_FLIP_VERTICALLY)
	 * - 位 29 = 对角线翻转标志 (FLAG_FLIP_DIAGONALLY)
	 * - 位 0-28 = 瓦片 ID
	 * 
	 * 如果 flipY=true（默认），则 Y 轴反转（Tiled 坐标 vs libGDX 坐标） */
	protected void loadTileLayer (TiledMap map, MapLayers parentLayers, Element element) {
		if (element.getName().equals("layer")) {
			int width = element.getIntAttribute("width", 0);
			int height = element.getIntAttribute("height", 0);
			int tileWidth = map.getProperties().get("tilewidth", Integer.class);
			int tileHeight = map.getProperties().get("tileheight", Integer.class);
			TiledMapTileLayer layer = new TiledMapTileLayer(width, height, tileWidth, tileHeight);

			loadBasicLayerInfo(layer, element);

			// 从 <data> 元素中解析所有瓦片 GID
			int[] ids = getTileIds(element, width, height);
			TiledMapTileSets tilesets = map.getTileSets();
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int id = ids[y * width + x];
					// 提取翻转标志（高位）
					boolean flipHorizontally = ((id & FLAG_FLIP_HORIZONTALLY) != 0);
					boolean flipVertically = ((id & FLAG_FLIP_VERTICALLY) != 0);
					boolean flipDiagonally = ((id & FLAG_FLIP_DIAGONALLY) != 0);

					// 清除高位后得到真实的瓦片 ID
					TiledMapTile tile = tilesets.getTile(id & ~MASK_CLEAR);
					if (tile != null) {
						Cell cell = createTileLayerCell(flipHorizontally, flipVertically, flipDiagonally);
						cell.setTile(tile);
						// 如果 flipY=true，Y 轴反转（Tiled 的原点左上 vs libGDX 的原点左下）
						layer.setCell(x, flipY ? height - 1 - y : y, cell);
					}
				}
			}

			Element properties = element.getChildByName("properties");
			if (properties != null) {
				loadProperties(layer.getProperties(), properties);
			}
			parentLayers.add(layer);
		}
	}

	/** 加载对象图层。遍历所有 <object> 元素，识别模板引用并解析。 */
	protected void loadObjectGroup (TiledMap map, MapLayers parentLayers, Element element, FileHandle tmxFile) {
		if (element.getName().equals("objectgroup")) {
			MapLayer layer = new MapLayer();
			loadBasicLayerInfo(layer, element);
			Element properties = element.getChildByName("properties");
			if (properties != null) {
				loadProperties(layer.getProperties(), properties);
			}

			for (Element objectElement : element.getChildrenByName("object")) {
				Element elementToLoad = objectElement;
				// 如果对象引用了 .tx 模板文件，先解析模板并合并
				if (objectElement.hasAttribute("template")) {
					elementToLoad = resolveTemplateObject(map, layer, objectElement, tmxFile);
				}
				loadObject(map, layer, elementToLoad);
			}
			parentLayers.add(layer);
		}
	}

	/** 加载图片图层。解析 <image> 元素中的图片路径，加载纹理。 */
	protected void loadImageLayer (TiledMap map, MapLayers parentLayers, Element element, FileHandle tmxFile,
		ImageResolver imageResolver) {
		if (element.getName().equals("imagelayer")) {
			float x = 0;
			float y = 0;
			if (element.hasAttribute("offsetx")) {
				x = Float.parseFloat(element.getAttribute("offsetx", "0"));
			} else {
				x = Float.parseFloat(element.getAttribute("x", "0"));
			}
			if (element.hasAttribute("offsety")) {
				y = Float.parseFloat(element.getAttribute("offsety", "0"));
			} else {
				y = Float.parseFloat(element.getAttribute("y", "0"));
			}
			if (flipY) y = mapHeightInPixels - y;

			boolean repeatX = element.getIntAttribute("repeatx", 0) == 1;
			boolean repeatY = element.getIntAttribute("repeaty", 0) == 1;

			TextureRegion texture = null;

			Element image = element.getChildByName("image");

			if (image != null) {
				String source = image.getAttribute("source");
				FileHandle handle = getRelativeFileHandle(tmxFile, source);
				texture = imageResolver.getImage(handle.path());
				y -= texture.getRegionHeight();
			}

			TiledMapImageLayer layer = new TiledMapImageLayer(texture, x, y, repeatX, repeatY);

			loadBasicLayerInfo(layer, element);

			Element properties = element.getChildByName("properties");
			if (properties != null) {
				loadProperties(layer.getProperties(), properties);
			}

			parentLayers.add(layer);
		}
	}

	/** 加载图层基本信息：名称、透明度、色调颜色、可见性、偏移量、视差滚动因子。
	 * 特别注意：Tiled 的颜色格式是 #AARRGGBB，需要转换为 libGDX 的 Color 格式。 */
	protected void loadBasicLayerInfo (MapLayer layer, Element element) {
		String name = element.getAttribute("name", null);
		float opacity = Float.parseFloat(element.getAttribute("opacity", "1.0"));
		String tintColor = element.getAttribute("tintcolor", "#ffffffff");
		boolean visible = element.getIntAttribute("visible", 1) == 1;
		float offsetX = element.getFloatAttribute("offsetx", 0);
		float offsetY = element.getFloatAttribute("offsety", 0);
		float parallaxX = element.getFloatAttribute("parallaxx", 1f);
		float parallaxY = element.getFloatAttribute("parallaxy", 1f);

		layer.setName(name);
		layer.setOpacity(opacity);
		layer.setVisible(visible);
		layer.setOffsetX(offsetX);
		layer.setOffsetY(offsetY);
		layer.setParallaxX(parallaxX);
		layer.setParallaxY(parallaxY);

		// 转换颜色格式：#AARRGGBB → #RRGGBBAA
		layer.setTintColor(Color.valueOf(tiledColorToLibGDXColor(tintColor)));
	}

	/** 加载地图对象（重载：放入图层）。 */
	protected void loadObject (TiledMap map, MapLayer layer, Element element) {
		loadObject(map, layer.getObjects(), element, mapHeightInPixels);
	}

	/** 加载地图对象（重载：放入瓦片的对象集）。 */
	protected void loadObject (TiledMap map, TiledMapTile tile, Element element) {
		loadObject(map, tile.getObjects(), element, tile.getTextureRegion().getRegionHeight());
	}

	/** 加载地图对象的核心方法。根据子元素自动识别对象类型。
	 * 
	 * 支持的对象类型（优先级从高到低）：
	 * <polygon> → PolygonMapObject（多边形）
	 * <polyline> → PolylineMapObject（折线）
	 * <ellipse> → EllipseMapObject（椭圆）
	 * <point> → PointMapObject（点）
	 * <text> → TextMapObject（文本，支持字体/大小/对齐/样式）
	 * 有 gid 属性 → TiledMapTileMapObject（瓦片对象）
	 * 无子元素 → RectangleMapObject（矩形，默认） */
	protected void loadObject (TiledMap map, MapObjects objects, Element element, float heightInPixels) {
		if (element.getName().equals("object")) {
			MapObject object = null;

			// 如果 convertObjectToTileSpace=true，坐标转换到瓦片空间（除以瓦片尺寸）
			float scaleX = convertObjectToTileSpace ? 1.0f / mapTileWidth : 1.0f;
			float scaleY = convertObjectToTileSpace ? 1.0f / mapTileHeight : 1.0f;

			float x = element.getFloatAttribute("x", 0) * scaleX;
			float y = (flipY ? (heightInPixels - element.getFloatAttribute("y", 0)) : element.getFloatAttribute("y", 0)) * scaleY;

			float width = element.getFloatAttribute("width", 0) * scaleX;
			float height = element.getFloatAttribute("height", 0) * scaleY;

			if (element.getChildCount() > 0) {
				Element child = null;
				// 多边形：解析 "x1,y1 x2,y2 ..." 格式的顶点字符串
				if ((child = element.getChildByName("polygon")) != null) {
					String[] points = child.getAttribute("points").split(" ");
					float[] vertices = new float[points.length * 2];
					for (int i = 0; i < points.length; i++) {
						String[] point = points[i].split(",");
						vertices[i * 2] = Float.parseFloat(point[0]) * scaleX;
						vertices[i * 2 + 1] = Float.parseFloat(point[1]) * scaleY * (flipY ? -1 : 1);
					}
					Polygon polygon = new Polygon(vertices);
					polygon.setPosition(x, y);
					object = new PolygonMapObject(polygon);
				} else if ((child = element.getChildByName("polyline")) != null) {
					// 折线：同多边形解析格式
					String[] points = child.getAttribute("points").split(" ");
					float[] vertices = new float[points.length * 2];
					for (int i = 0; i < points.length; i++) {
						String[] point = points[i].split(",");
						vertices[i * 2] = Float.parseFloat(point[0]) * scaleX;
						vertices[i * 2 + 1] = Float.parseFloat(point[1]) * scaleY * (flipY ? -1 : 1);
					}
					Polyline polyline = new Polyline(vertices);
					polyline.setPosition(x, y);
					object = new PolylineMapObject(polyline);
				} else if ((child = element.getChildByName("ellipse")) != null) {
					object = new EllipseMapObject(x, flipY ? y - height : y, width, height);
				} else if ((child = element.getChildByName("point")) != null) {
					object = new PointMapObject(x, flipY ? y - height : y);
				} else if ((child = element.getChildByName("text")) != null) {
					// 文本对象：支持丰富的文本样式属性
					TextMapObject textMapObject = new TextMapObject(x, flipY ? y - height : y, width, height, child.getText());
					textMapObject.setFontFamily(child.getAttribute("fontfamily", ""));
					textMapObject.setPixelSize(child.getIntAttribute("pixelSize", 16));
					textMapObject.setHorizontalAlign(child.getAttribute("halign", "left"));
					textMapObject.setVerticalAlign(child.getAttribute("valign", "top"));
					textMapObject.setBold(child.getIntAttribute("bold", 0) == 1);
					textMapObject.setItalic(child.getIntAttribute("italic", 0) == 1);
					textMapObject.setUnderline(child.getIntAttribute("underline", 0) == 1);
					textMapObject.setStrikeout(child.getIntAttribute("strikeout", 0) == 1);
					textMapObject.setWrap(child.getIntAttribute("wrap", 0) == 1);
					textMapObject.setKerning(child.getIntAttribute("kerning", 1) == 1);
					String textColor = child.getAttribute("color", "#000000");
					textMapObject.setColor(Color.valueOf(tiledColorToLibGDXColor(textColor)));
					object = textMapObject;
				}
			}
			if (object == null) {
				// 没有子元素 → 可能是瓦片对象或矩形
				String gid = null;
				if ((gid = element.getAttribute("gid", null)) != null) {
					// 有 gid 属性 → 瓦片对象（嵌入在对象层中的瓦片）
					int id = (int)Long.parseLong(gid);
					boolean flipHorizontally = ((id & FLAG_FLIP_HORIZONTALLY) != 0);
					boolean flipVertically = ((id & FLAG_FLIP_VERTICALLY) != 0);

					TiledMapTile tile = map.getTileSets().getTile(id & ~MASK_CLEAR);
					TiledMapTileMapObject tiledMapTileMapObject = new TiledMapTileMapObject(tile, flipHorizontally, flipVertically);
					TextureRegion textureRegion = tiledMapTileMapObject.getTextureRegion();
					tiledMapTileMapObject.getProperties().put("gid", id);
					tiledMapTileMapObject.setX(x);
					tiledMapTileMapObject.setY(flipY ? y : y - height);
					float objectWidth = element.getFloatAttribute("width", textureRegion.getRegionWidth());
					float objectHeight = element.getFloatAttribute("height", textureRegion.getRegionHeight());
					tiledMapTileMapObject.setScaleX(scaleX * (objectWidth / textureRegion.getRegionWidth()));
					tiledMapTileMapObject.setScaleY(scaleY * (objectHeight / textureRegion.getRegionHeight()));
					tiledMapTileMapObject.setRotation(element.getFloatAttribute("rotation", 0));
					object = tiledMapTileMapObject;
				} else {
					// 无子元素也无 gid → 默认为矩形对象
					object = new RectangleMapObject(x, flipY ? y - height : y, width, height);
				}
			}
			// 设置对象公共属性
			object.setName(element.getAttribute("name", null));
			String rotation = element.getAttribute("rotation", null);
			if (rotation != null) {
				object.getProperties().put("rotation", Float.valueOf(rotation));
			}
			String type = element.getAttribute("type", null);
			if (type != null) {
				object.getProperties().put("type", type);
			}
			int id = element.getIntAttribute("id", 0);
			if (id != 0) {
				object.getProperties().put("id", id);
			}
			object.getProperties().put("x", x);

			if (object instanceof TiledMapTileMapObject) {
				object.getProperties().put("y", y);
			} else {
				object.getProperties().put("y", (flipY ? y - height : y));
			}
			object.getProperties().put("width", width);
			object.getProperties().put("height", height);
			object.setVisible(element.getIntAttribute("visible", 1) == 1);
			Element properties = element.getChildByName("properties");
			if (properties != null) {
				loadProperties(object.getProperties(), properties);
			}

			// 如果指定了 type(=class)，加载该 class 的默认属性值
			loadMapPropertiesClassDefaults(type, object.getProperties());

			idToObject.put(id, object);
			objects.add(object);
		}
	}

	/** 解析模板对象 (.tx 文件)。
	 * 
	 * 模板对象在 <object> 元素中通过 "template" 属性引用 .tx 文件。
	 * 解析流程：
	 * 1. 检查缓存中是否已解析过该 .tx 文件
	 * 2. 如果未缓存，读取并解析 .tx 文件
	 * 3. 将模板元素与父元素合并（父元素属性优先）
	 * 
	 * @param map 正在加载的地图
	 * @param layer 当前图层
	 * @param mapElement 引用了模板的 <object> 元素
	 * @param tmxFile .tmx 文件句柄
	 * @return 合并后的元素 */
	protected Element resolveTemplateObject (TiledMap map, MapLayer layer, Element mapElement, FileHandle tmxFile) {
		String txFileName = mapElement.getAttribute("template");
		Element templateElement = templateCache.get(txFileName);
		if (templateElement == null) {
			FileHandle templateFile = getRelativeFileHandle(tmxFile, txFileName);
			try {
				templateElement = xml.parse(templateFile);
			} catch (Exception e) {
				throw new GdxRuntimeException("解析模板文件失败: " + txFileName, e);
			}
			templateCache.put(txFileName, templateElement);
		}
		Element templateObjectElement = templateElement.getChildByName("object");
		return mergeParentElementWithTemplate(mapElement, templateObjectElement);
	}

	/** 浅拷贝 XML 元素（只复制自身属性，不复制子元素）。 */
	protected Element cloneElementShallow (Element sourceElement) {
		Element copyElement = new Element(sourceElement.getName(), null);
		ObjectMap<String, String> attrs = sourceElement.getAttributes();
		if (attrs != null) {
			for (ObjectMap.Entry<String, String> entry : attrs.entries()) {
				copyElement.setAttribute(entry.key, entry.value);
			}
		}
		if (sourceElement.getText() != null) copyElement.setText(sourceElement.getText());
		return copyElement;
	}

	/** 合并父元素和模板元素的 <properties> 子标签。父元素的同名属性会覆盖模板中的值。 */
	protected Element mergeProperties (Element parentProps, Element templateProps) {
		if (templateProps == null) return parentProps;
		if (parentProps == null) return templateProps;
		Element merged = new Element("properties", null);
		// 先加入模板的属性
		for (Element property : templateProps.getChildrenByName("property")) {
			merged.addChild(cloneElementShallow(property));
		}
		// 再加入父元素的属性，同名则覆盖
		for (Element property : parentProps.getChildrenByName("property")) {
			String name = property.getAttribute("name", null);
			Element existing = null;
			for (int i = 0; i < merged.getChildCount(); i++) {
				Element child = merged.getChild(i);
				if ("property".equals(child.getName()) && name.equals(child.getAttribute("name", null))) {
					existing = child;
					break;
				}
			}
			if (existing != null) merged.removeChild(existing);
			merged.addChild(cloneElementShallow(property));
		}
		return merged;
	}

	/** 递归合并父对象元素和模板对象元素。父元素的属性和子元素会覆盖模板中的对应项。
	 * 合并策略：属性级合并 + 子元素级合并（包括 properties 和点、多边形等子标签）。 */
	protected Element mergeParentElementWithTemplate (Element parent, Element template) {
		if (template == null) return parent;
		if (parent == null) return template;
		Element merged = new Element(template.getName(), null);
		// 属性合并：先模板后父元素
		if (template.getAttributes() != null) {
			for (ObjectMap.Entry<String, String> a : template.getAttributes().entries()) {
				merged.setAttribute(a.key, a.value);
			}
		}
		if (parent.getAttributes() != null) {
			for (ObjectMap.Entry<String, String> a : parent.getAttributes().entries()) {
				merged.setAttribute(a.key, a.value);
			}
		}
		// 文本内容合并
		String txt = (parent.getText() != null && parent.getText().length() > 0) ? parent.getText() : template.getText();
		if (txt != null) {
			merged.setText(txt);
		}
		// 子元素合并：收集所有标签名，递归合并
		ObjectSet<String> tagNames = new ObjectSet<>();
		for (int i = 0; i < template.getChildCount(); i++)
			tagNames.add(template.getChild(i).getName());
		for (int i = 0; i < parent.getChildCount(); i++)
			tagNames.add(parent.getChild(i).getName());

		for (String tag : tagNames) {
			Element mapChild = parent.getChildByName(tag);
			Element tmplChild = template.getChildByName(tag);
			Element mergedChild = "properties".equals(tag) ? mergeProperties(mapChild, tmplChild)
				: mergeParentElementWithTemplate(mapChild, tmplChild);
			merged.addChild(mergedChild);
		}
		return merged;
	}
	/* * ==== 模板加载结束 ==== */

	/** 加载自定义属性列表。处理 property 元素的类型属性：
	 * - "object"：对象引用属性（通过 ID 引用其他对象）
	 * - "class"：类属性（嵌套的属性集合，来自 Tiled 工程文件）
	 * - 其他类型（string、int、float、bool、color、file）：基础属性 */
	protected void loadProperties (final MapProperties properties, Element element) {
		if (element == null) return;
		if (element.getName().equals("properties")) {
			for (Element property : element.getChildrenByName("property")) {
				final String name = property.getAttribute("name", null);
				String value = getPropertyValue(property);
				String type = property.getAttribute("type", null);
				if ("object".equals(type)) {
					loadObjectProperty(properties, name, value);
				} else if ("class".equals(type)) {
					MapProperties classProperties = new MapProperties();
					String className = property.getAttribute("propertytype");
					classProperties.put("type", className);
					properties.put(name, classProperties);
					loadClassProperties(className, classProperties, property.getChildByName("properties"));
				} else {
					loadBasicProperty(properties, name, value, type);
				}
			}
		}
	}

	/** 从 Tiled 工程文件加载 class 属性的默认值。如果 XML 中未提供值，则使用工程文件中的默认值。 */
	protected void loadClassProperties (String className, MapProperties classProperties, XmlReader.Element classElement) {
		if (projectClassInfo == null) {
			throw new GdxRuntimeException(
				"未加载 class 信息。请设置 'projectFilePath' 参数指向 .tiled-project 文件");
		}
		if (projectClassInfo.isEmpty()) {
			throw new GdxRuntimeException(
				"class 信息为空。请确认 'projectFilePath' 参数指向了正确的 Tiled 工程文件");
		}
		Array<ProjectClassMember> projectClassMembers = projectClassInfo.get(className);
		if (projectClassMembers == null) {
			throw new GdxRuntimeException("Tiled 工程文件中不存在名为 '" + className + "' 的类");
		}

		for (ProjectClassMember projectClassMember : projectClassMembers) {
			String propName = projectClassMember.name;
			XmlReader.Element classProp = classElement == null ? null : getPropertyByName(classElement, propName);
			switch (projectClassMember.type) {
			case "object": {
				String value = classProp == null ? projectClassMember.defaultValue.asString() : getPropertyValue(classProp);
				loadObjectProperty(classProperties, propName, value);
				break;
			}
			case "class": {
				MapProperties nestedClassProperties = new MapProperties();
				String nestedClassName = projectClassMember.propertyType;
				nestedClassProperties.put("type", nestedClassName);
				classProperties.put(propName, nestedClassProperties);
				if (classProp == null) {
					loadJsonClassProperties(nestedClassName, nestedClassProperties, projectClassMember.defaultValue);
				} else {
					loadClassProperties(nestedClassName, nestedClassProperties, classProp);
				}
				break;
			}
			default: {
				String value = classProp == null ? projectClassMember.defaultValue.asString() : getPropertyValue(classProp);
				loadBasicProperty(classProperties, propName, value, projectClassMember.type);
				break;
			}
			}
		}
	}

	private static String getPropertyValue (Element classProp) {
		return classProp.getAttribute("value", classProp.getText());
	}

	protected Element getPropertyByName (Element classElement, String propName) {
		// 使用递归遍历以支持嵌套类中的属性查找
		for (Element property : classElement.getChildrenByNameRecursively("property")) {
			if (propName.equals(property.getAttribute("name"))) {
				return property;
			}
		}
		return null;
	}

	/** 解析瓦片层数据的核心方法。
	 * 
	 * Tiled 支持三种数据编码方式：
	 * 1. **CSV**：逗号分隔的纯文本数字，每行对应一个瓦片的 GID
	 * 2. **Base64**：Base64 编码的二进制数据，每个瓦片 4 字节（int32）
	 * 3. **Base64 + 压缩**：Base64 编码后可选 GZip 或 Zlib 压缩
	 * 
	 * 每个瓦片 GID 的编码：
	 * - 高位 bit31=水平翻转，bit30=垂直翻转，bit29=对角线翻转
	 * - 低位 bit0-28=真实的瓦片 ID
	 * 
	 * @param element <layer> XML 元素
	 * @param width 图层宽度（瓦片列数）
	 * @param height 图层高度（瓦片行数）
	 * @return 一维 int 数组 [y*width + x] = GID */
	static public int[] getTileIds (Element element, int width, int height) {
		Element data = element.getChildByName("data");
		String encoding = data.getAttribute("encoding", null);
		if (encoding == null) { // 无 encoding 属性 = XML 格式（已废弃，抛出异常）
			throw new GdxRuntimeException("不支持的编码格式 (XML) for TMX Layer Data");
		}
		int[] ids = new int[width * height];
		if (encoding.equals("csv")) {
			// CSV 模式：直接分割字符串解析
			String[] array = data.getText().split(",");
			for (int i = 0; i < array.length; i++)
				ids[i] = (int)Long.parseLong(array[i].trim());
		} else {
			if (true) if (encoding.equals("base64")) {
				InputStream is = null;
				try {
					String compression = data.getAttribute("compression", null);
					byte[] bytes = Base64Coder.decode(data.getText());
					if (compression == null)
						is = new ByteArrayInputStream(bytes);
					else if (compression.equals("gzip"))
						is = new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes), bytes.length));
					else if (compression.equals("zlib"))
						is = new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(bytes)));
					else
						throw new GdxRuntimeException("不支持的压缩格式 (" + compression + ") for TMX Layer Data");

					// 每个瓦片 4 字节（int32），小端序
					byte[] temp = new byte[4];
					for (int y = 0; y < height; y++) {
						for (int x = 0; x < width; x++) {
							int read = is.read(temp);
							while (read < temp.length) {
								int curr = is.read(temp, read, temp.length - read);
