/*******************************************************************************
 * <b>纹理图集优化的 TMJ 地图加载器。</b>
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
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.ImageResolver;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;

/** <b>纹理图集优化的 TMJ (JSON) 地图加载器。</b>
 * 
 * 与 {@link TmxMapLoader} 从单独的图片文件中加载瓦片不同，
 * 本加载器从 {@link TextureAtlas}（纹理图集）中加载瓦片。
 * 纹理图集将多个小图片合并到一张大纹理中，大幅减少 OpenGL 纹理绑定次数。
 * 
 * <b>使用前提：</b>
 * 地图的属性中必须包含名为 "atlas" 的属性，其值为指向 .atlas 文件的相对路径。
 * 图集中的区域(region)必须以 tileset 的名称命名，并使用索引(index)区分。
 * 创建图集时不应使用 strip whitespace（去空白）和 rotation（旋转）。
 * 
 * 图集区域命名约定：
 * - 区域名 = tileset 名称
 * - region.index = 瓦片在 tileset 中的本地索引（非全局 ID）
 * 
 * @author Justin Shapcott
 * @author Manuel Bua */
public class AtlasTmjMapLoader extends BaseTmjMapLoader<BaseTiledMapLoader.Parameters> {

	/** 图集解析器接口。从图集中查找纹理区域。 */
	protected interface AtlasResolver extends ImageResolver {

		/** @return 当前使用的纹理图集 */
		public TextureAtlas getAtlas ();

		/** 直接使用 TextureAtlas 对象的解析器（非 AssetManager 方式加载）。 */
		public static class DirectAtlasResolver implements AtlasTmjMapLoader.AtlasResolver {
			private final TextureAtlas atlas;

			public DirectAtlasResolver (TextureAtlas atlas) {
				this.atlas = atlas;
			}

			@Override
			public TextureAtlas getAtlas () {
				return atlas;
			}

			@Override
			public TextureRegion getImage (String name) {
				// 检查是否包含 imagelayer 标记，若包含则去除路径前缀
				String regionName = parseRegionName(name);
				return atlas.findRegion(regionName);
			}
		}

		/** 通过 AssetManager 管理的图集解析器（用于异步加载场景）。 */
		public static class AssetManagerAtlasResolver implements AtlasTmjMapLoader.AtlasResolver {
			private final AssetManager assetManager;
			private final String atlasName;

			public AssetManagerAtlasResolver (AssetManager assetManager, String atlasName) {
				this.assetManager = assetManager;
				this.atlasName = atlasName;
			}

			@Override
			public TextureAtlas getAtlas () {
				return assetManager.get(atlasName, TextureAtlas.class);
			}

			@Override
			public TextureRegion getImage (String name) {
				String regionName = parseRegionName(name);
				return getAtlas().findRegion(regionName);
			}
		}
	}

	/** 追踪已加载的纹理列表，用于后续设置纹理过滤参数 */
	protected Array<Texture> trackedTextures = new Array<Texture>();

	/** 图集解析器实例 */
	protected AtlasResolver atlasResolver;

	/** 使用内部文件解析器创建加载器 */
	public AtlasTmjMapLoader () {
		super(new InternalFileHandleResolver());
	}

	public AtlasTmjMapLoader (FileHandleResolver resolver) {
		super(resolver);
	}

	/** 同步加载地图（直接加载，不使用 AssetManager）。
	 * 
	 * 加载流程：
	 * 1. 解析 .tmj (JSON) 文件
	 * 2. 从地图属性中获取 "atlas" 属性指向的 .atlas 文件路径
	 * 3. 加载纹理图集
	 * 4. 使用图集加载地图中的瓦片 */
	public TiledMap load (String fileName) {
		return load(fileName, new Parameters());
	}

	public TiledMap load (String fileName, Parameters parameter) {
		FileHandle tmjFile = resolve(fileName);

		this.root = json.parse(tmjFile);

		final FileHandle atlasFileHandle = getAtlasFileHandle(tmjFile);
		TextureAtlas atlas = new TextureAtlas(atlasFileHandle);
		this.atlasResolver = new AtlasResolver.DirectAtlasResolver(atlas);

		TiledMap map = loadTiledMap(tmjFile, parameter, atlasResolver);
		map.setOwnedResources(new Array<TextureAtlas>(new TextureAtlas[] {atlas}));
		setTextureFilters(parameter.textureMinFilter, parameter.textureMagFilter);
		return map;
	}

	/** 异步加载（供 AssetManager 使用）：仅解析地图结构，不加载纹理。 */
	@Override
	public void loadAsync (AssetManager manager, String fileName, FileHandle tmjFile, Parameters parameter) {
		FileHandle atlasHandle = getAtlasFileHandle(tmjFile);
		this.atlasResolver = new AtlasResolver.AssetManagerAtlasResolver(manager, atlasHandle.path());

		this.map = loadTiledMap(tmjFile, parameter, atlasResolver);
	}

	/** 同步加载完成（供 AssetManager 使用）：设置纹理过滤参数。 */
	@Override
	public TiledMap loadSync (AssetManager manager, String fileName, FileHandle file, Parameters parameter) {
		if (parameter != null) {
			setTextureFilters(parameter.textureMinFilter, parameter.textureMagFilter);
		}
		return map;
	}

	/** 获取资源依赖列表：仅依赖 .atlas 纹理图集文件。 */
	@Override
	protected Array<AssetDescriptor> getDependencyAssetDescriptors (FileHandle tmxFile,
		TextureLoader.TextureParameter textureParameter) {
		Array<AssetDescriptor> descriptors = new Array<AssetDescriptor>();

		// 添加图集依赖
		final FileHandle atlasFileHandle = getAtlasFileHandle(tmxFile);
		if (atlasFileHandle != null) {
			descriptors.add(new AssetDescriptor(atlasFileHandle, TextureAtlas.class));
		}

		return descriptors;
	}

	/** 从纹理图集中添加静态瓦片到瓦片集。
	 * 
	 * 与普通加载器的区别：瓦片的纹理区域不是从单独的图片文件加载，
	 * 而是从 TextureAtlas 中查找命名区域。
	 * 
	 * 查找规则：
	 * 1. 先按 tileset 名称查找所有区域，使用 region.index 匹配 tile ID
	 * 2. 再处理有独立图片源的瓦片（通过图片文件名去掉扩展名作为 region 名）
	 * 
	 * @param tileSet 目标瓦片集
	 * @param tiles 瓦片 JSON 数组（可能包含有独立图片源的瓦片）
	 * @param name tileset 名称，也是图集区域名的前缀 */
	@Override
	protected void addStaticTiles (FileHandle tmjFile, ImageResolver imageResolver, TiledMapTileSet tileSet, JsonValue element,
		JsonValue tiles, String name, int firstgid, int tilewidth, int tileheight, int spacing, int margin, int offsetX,
		int offsetY, String imageSource, int imageWidth, int imageHeight, FileHandle image) {

		TextureAtlas atlas = atlasResolver.getAtlas();
		String regionsName = name;

		// 追踪图集引用的所有纹理，用于后续设置过滤参数
		for (Texture texture : atlas.getTextures()) {
			trackedTextures.add(texture);
		}

		// 保存 tileset 属性
		MapProperties props = tileSet.getProperties();
		props.put("imagesource", imageSource);
		props.put("imagewidth", imageWidth);
		props.put("imageheight", imageHeight);
		props.put("tilewidth", tilewidth);
		props.put("tileheight", tileheight);
		props.put("margin", margin);
		props.put("spacing", spacing);

		if (imageSource != null && imageSource.length() > 0) {
			// 计算全局 GID 范围
			int lastgid = firstgid + ((imageWidth / tilewidth) * (imageHeight / tileheight)) - 1;
			// 从图集中查找所有以 tileset 命名的区域
			for (AtlasRegion region : atlas.findRegions(regionsName)) {
				if (region != null) {
					// region.index 是瓦片在 tileset 中的本地索引
					int tileId = firstgid + region.index;
					// 确保 GID 在有效范围内
					if (tileId >= firstgid && tileId <= lastgid) {
						addStaticTiledMapTile(tileSet, region, tileId, offsetX, offsetY);
					}
				}
			}
		}

		// 处理有独立图片源的瓦片（每个瓦片单独指定图片的情况）
		for (JsonValue tileElement : tiles) {
			int tileId = firstgid + tileElement.getInt("id", 0);
			TiledMapTile tile = tileSet.getTile(tileId);
			if (tile == null) {
				// 该瓦片还未被添加（说明它有独立的图片源）
				JsonValue imageElement = tileElement.get("image");
				if (imageElement != null) {
					// 使用图片文件名（去掉扩展名）作为图集区域名
					String regionName = imageElement.asString();
					regionName = regionName.substring(0, regionName.lastIndexOf('.'));
					AtlasRegion region = atlas.findRegion(regionName);
					if (region == null) throw new GdxRuntimeException("图集中未找到区域: " + regionName);
					addStaticTiledMapTile(tileSet, region, tileId, offsetX, offsetY);
				}
			}
		}
	}

	/** 从地图属性中获取 "atlas" 属性值，并解析为文件句柄。
	 * 
	 * 地图的 JSON 属性中必须包含名为 "atlas" 的属性，
	 * 其值指向 .atlas 纹理图集文件的路径（相对 .tmj 文件）。
	 * 
	 * @param tmjFile .tmj 文件句柄
	 * @return .atlas 文件的句柄
	 * @throws GdxRuntimeException 如果找不到 atlas 属性或文件不存在 */
	protected FileHandle getAtlasFileHandle (FileHandle tmjFile) {
		JsonValue properties = root.get("properties");

		String atlasFilePath = null;
		if (properties != null) {
			for (JsonValue property : properties) {
				String name = property.getString("name", "");
				if (name.startsWith("atlas")) {
					atlasFilePath = property.getString("value", "");
					break;
				}
			}
		}

		if (atlasFilePath == null || atlasFilePath.isEmpty()) {
			throw new GdxRuntimeException("地图缺少 'atlas' 属性");
		} else {
			final FileHandle fileHandle = getRelativeFileHandle(tmjFile, atlasFilePath);
			if (!fileHandle.exists()) {
				throw new GdxRuntimeException("找不到 'atlas' 文件: '" + atlasFilePath + "'");
			}
			return fileHandle;
		}
	}

	/** 设置所有追踪纹理的过滤参数（缩小/放大过滤器）。 */
	protected void setTextureFilters (Texture.TextureFilter min, Texture.TextureFilter mag) {
		for (Texture texture : trackedTextures) {
			texture.setFilter(min, mag);
		}
		trackedTextures.clear();
	}

	/** 解析区域名称，处理图片图层中的路径前缀。
	 * 
	 * 图片图层的图片被打包到图集中时，region 名称可能包含完整路径。
	 * 此方法检测 "atlas_imagelayer" 标记，如果存在则去除路径部分，
	 * 只保留文件名作为区域名。
	 * 
	 * @param name 原始名称（可能包含路径）
	 * @return 处理后的区域名称 */
	static String parseRegionName (String name) {
		if (name.contains("atlas_imagelayer")) {
			// 找到最后一个 '/'，去掉路径前缀
			int lastSlash = name.lastIndexOf('/');
			return (lastSlash >= 0) ? name.substring(lastSlash + 1) : name;
		} else {
			return name;
		}
	}
}
