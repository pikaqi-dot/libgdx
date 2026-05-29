/*******************************************************************************
 * <b>Tiled 编辑器瓦片地图，包含图层集、瓦片集和属性。</b>
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

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/** <b>Tiled 瓦片地图。</b>
 * 继承自 {@link Map}，添加了瓦片集(TiledMapTileSets)的概念。
 * 一个 TiledMap 包含：
 * - {@link TiledMapTileSets}：瓦片集合列表（定义所有可用的瓦片）
 * - {@link TiledMapTileLayer}：瓦片图层（二维网格中的瓦片排列）
 * - {@link MapProperties}：地图级属性（如地图大小、背景色等）
 * 
 * @see Map */
public class TiledMap extends Map {
	/** 瓦片集合列表，管理所有瓦片集（每个瓦片集对应一个图片文件） */
	private TiledMapTileSets tilesets;

	/** 地图拥有的资源列表（由加载器设置），在 dispose() 时释放。
	 * 这些资源是不通过 AssetManager 直接加载的纹理等。 */
	private Array<? extends Disposable> ownedResources;

	/** @return 地图的瓦片集合列表 */
	public TiledMapTileSets getTileSets () {
		return tilesets;
	}

	/** 创建一个空的 TiledMap。之后需要通过加载器填充图层和瓦片集。 */
	public TiledMap () {
		tilesets = new TiledMapTileSets();
	}

	/** 由加载器调用，设置在直接加载地图时（不使用 AssetManager）拥有的资源。
	 * 这些资源将在 dispose() 时统一释放。
	 * @param resources 要拥有的资源列表（如纹理等） */
	public void setOwnedResources (Array<? extends Disposable> resources) {
		this.ownedResources = resources;
	}

	@Override
	public void dispose () {
		// 释放直接加载的资源（非 AssetManager 管理的）
		if (ownedResources != null) {
			for (Disposable resource : ownedResources) {
				resource.dispose();
			}
		}
	}
}
