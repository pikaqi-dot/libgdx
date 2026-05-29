/*******************************************************************************
 * <b>静态瓦片，使用固定纹理的不可变瓦片。</b>
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

package com.badlogic.gdx.maps.tiled.tiles;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;

/** <b>静态瓦片 — 使用固定纹理的不可变瓦片。</b>
 * 这是最常见的瓦片类型，每个瓦片对应一个固定的纹理区域。
 * 由于纹理内容不会随时间变化，渲染器可以对其进行缓存优化。
 * 对应 Tiled 编辑器中放置的普通瓦片。
 * 
 * 支持属性：ID、纹理区域、混合模式、渲染偏移量、自定义属性、内置对象 */
public class StaticTiledMapTile implements TiledMapTile {

	/** 瓦片在瓦片集中的唯一 ID */
	private int id;

	/** 渲染混合模式，默认使用 ALPHA 透明度混合 */
	private BlendMode blendMode = BlendMode.ALPHA;

	/** 瓦片自定义属性集合（延迟初始化，首次访问时创建） */
	private MapProperties properties;

	/** 瓦片中包含的地图对象（如碰撞区域、事件触发器，延迟初始化） */
	private MapObjects objects;

	/** 瓦片使用的纹理区域 */
	private TextureRegion textureRegion;

	/** 渲染时的 X 偏移量（像素） */
	private float offsetX;

	/** 渲染时的 Y 偏移量（像素） */
	private float offsetY;

	@Override
	public int getId () {
		return id;
	}

	@Override
	public void setId (int id) {
		this.id = id;
	}

	@Override
	public BlendMode getBlendMode () {
		return blendMode;
	}

	@Override
	public void setBlendMode (BlendMode blendMode) {
		this.blendMode = blendMode;
	}

	/** @return 瓦片属性集合（首次调用时创建） */
	@Override
	public MapProperties getProperties () {
		if (properties == null) {
			properties = new MapProperties();
		}
		return properties;
	}

	/** @return 瓦片中的地图对象集合（首次调用时创建） */
	@Override
	public MapObjects getObjects () {
		if (objects == null) {
			objects = new MapObjects();
		}
		return objects;
	}

	@Override
	public TextureRegion getTextureRegion () {
		return textureRegion;
	}

	@Override
	public void setTextureRegion (TextureRegion textureRegion) {
		this.textureRegion = textureRegion;
	}

	@Override
	public float getOffsetX () {
		return offsetX;
	}

	@Override
	public void setOffsetX (float offsetX) {
		this.offsetX = offsetX;
	}

	@Override
	public float getOffsetY () {
		return offsetY;
	}

	@Override
	public void setOffsetY (float offsetY) {
		this.offsetY = offsetY;
	}

	/** 创建使用指定纹理区域的静态瓦片
	 * @param textureRegion 瓦片使用的纹理区域 */
	public StaticTiledMapTile (TextureRegion textureRegion) {
		this.textureRegion = textureRegion;
	}

	/** 复制构造函数
	 * @param copy 要复制的源瓦片 */
	public StaticTiledMapTile (StaticTiledMapTile copy) {
		if (copy.properties != null) {
			getProperties().putAll(copy.properties);
		}
		this.objects = copy.objects;
		this.textureRegion = copy.textureRegion;
		this.id = copy.id;
	}

}
