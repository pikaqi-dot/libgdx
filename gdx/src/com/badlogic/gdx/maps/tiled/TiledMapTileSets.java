/*******************************************************************************
 * <b>瓦片集合列表，管理地图的所有瓦片集。</b>
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

import java.util.Iterator;

import com.badlogic.gdx.utils.Array;

/** <b>瓦片集合列表 — 管理地图的所有瓦片集。</b>
 * 一个 TiledMap 可能有多个瓦片集（如 terrain.png、objects.png），
 * 每个瓦片集都是一个 {@link TiledMapTileSet}。
 * 本类负责管理这些瓦片集的增删改查，
 * 以及在所有瓦片集中根据瓦片 ID 查找瓦片。
 * 内部使用 {@link Array} 存储，保持瓦片集的有序性。 */
public class TiledMapTileSets implements Iterable<TiledMapTileSet> {

	/** 瓦片集列表，按添加顺序存储 */
	private Array<TiledMapTileSet> tilesets;

	/** 创建一个空的瓦片集集合 */
	public TiledMapTileSets () {
		tilesets = new Array<TiledMapTileSet>();
	}

	/** 根据索引获取瓦片集
	 * @param index 索引（从0开始）
	 * @return 指定索引处的瓦片集 */
	public TiledMapTileSet getTileSet (int index) {
		return tilesets.get(index);
	}

	/** 根据名称获取瓦片集
	 * @param name 瓦片集名称（对应 Tiled 中的 name 属性）
	 * @return 匹配的瓦片集，不存在则返回 null */
	public TiledMapTileSet getTileSet (String name) {
		for (TiledMapTileSet tileset : tilesets) {
			if (name.equals(tileset.getName())) {
				return tileset;
			}
		}
		return null;
	}

	/** 添加瓦片集到列表中
	 * @param tileset 要添加的瓦片集 */
	public void addTileSet (TiledMapTileSet tileset) {
		tilesets.add(tileset);
	}

	/** 移除指定索引处的瓦片集
	 * @param index 要移除的瓦片集索引 */
	public void removeTileSet (int index) {
		tilesets.removeIndex(index);
	}

	/** 移除指定的瓦片集
	 * @param tileset 要移除的瓦片集 */
	public void removeTileSet (TiledMapTileSet tileset) {
		tilesets.removeValue(tileset, true);
	}

	/** 在所有瓦片集中查找指定 ID 的瓦片。
	 * 
	 * 采用反向遍历的原因是为了向后兼容早期的共享瓦片集。
	 * 假设瓦片集按 firstgid 升序排列，反向遍历时，
	 * 后添加的瓦片集优先匹配，这通常是早期的瓦片集版本。
	 * 
	 * @param id 瓦片的全局 ID（GID）
	 * @return 匹配的瓦片，不存在则返回 null */
	public TiledMapTile getTile (int id) {
		// 反向遍历：后添加的瓦片集优先（保持向后兼容）
		for (int i = tilesets.size - 1; i >= 0; i--) {
			TiledMapTileSet tileset = tilesets.get(i);
			TiledMapTile tile = tileset.getTile(id);
			if (tile != null) {
				return tile;
			}
		}
		return null;
	}

	/** @return 遍历所有瓦片集的迭代器 */
	@Override
	public Iterator<TiledMapTileSet> iterator () {
		return tilesets.iterator();
	}

}
