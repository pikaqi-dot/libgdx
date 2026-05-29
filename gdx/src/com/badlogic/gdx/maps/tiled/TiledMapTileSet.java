/*******************************************************************************
 * <b>瓦片集合，包含一组瓦片和共享属性。</b>
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
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.IntMap;

/** <b>瓦片集合 — 一组瓦片的容器。</b>
 * 对应 Tiled 编辑器中的一个图块集（tileset），
 * 包含多个瓦片（TiledMapTile），每个瓦片通过 int ID 唯一标识。
 * 瓦片集合也有自己的属性（如图块来源文件、间距、边距等）。
 * 内部使用 {@link IntMap} 存储，以 ID 为键进行快速查找。 */
public class TiledMapTileSet implements Iterable<TiledMapTile> {

	/** 瓦片集合的名称（对应 Tiled 中的 tileset name） */
	private String name;

	/** 瓦片存储映射：ID → TiledMapTile
	 * 使用 IntMap 而非 HashMap 以避免 Integer 装箱开销 */
	private IntMap<TiledMapTile> tiles;

	/** 瓦片集合级的属性（如 tilewidth、tileheight、spacing、margin、image 路径等） */
	private MapProperties properties;

	/** @return 瓦片集合的名称 */
	public String getName () {
		return name;
	}

	/** @param name 瓦片集合的新名称 */
	public void setName (String name) {
		this.name = name;
	}

	/** @return 瓦片集合的属性集合 */
	public MapProperties getProperties () {
		return properties;
	}

	/** 创建一个空的瓦片集合 */
	public TiledMapTileSet () {
		tiles = new IntMap<TiledMapTile>();
		properties = new MapProperties();
	}

	/** 根据 ID 获取瓦片。
	 * ID 在瓦片集中的全局唯一标识，由 Tiled 编辑器分配。
	 * @param id 瓦片的 ID
	 * @return 匹配的瓦片，不存在则返回 null */
	public TiledMapTile getTile (int id) {
		return tiles.get(id);
	}

	/** @return 遍历瓦片集合中所有瓦片的迭代器 */
	@Override
	public Iterator<TiledMapTile> iterator () {
		return tiles.values().iterator();
	}

	/** 添加或替换指定 ID 的瓦片。
	 * 如果该 ID 已存在，则新瓦片会替换旧瓦片。
	 * @param id 瓦片的 ID
	 * @param tile 要添加的瓦片 */
	public void putTile (int id, TiledMapTile tile) {
		tiles.put(id, tile);
	}

	/** 移除指定 ID 的瓦片。
	 * @param id 要移除的瓦片 ID */
	public void removeTile (int id) {
		tiles.remove(id);
	}

	/** @return 瓦片集合的大小（瓦片数量） */
	public int size () {
		return tiles.size;
	}
}
