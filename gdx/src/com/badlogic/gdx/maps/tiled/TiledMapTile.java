/*******************************************************************************
 * <b>瓦片接口，定义单一瓦片的内容和属性。</b>
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

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;

/** <b>瓦片接口 — TiledMap 中的基础单元。</b>
 * 定义了单个瓦片的所有必要属性：
 * - 纹理区域（用于渲染的图片部分）
 * - ID（在瓦片集中的唯一标识）
 * - 混合模式（渲染时的透明度混合方式）
 * - 偏移量（渲染位置微调）
 * - 属性集（自定义键值对）
 * - 对象集（瓦片中嵌入的地图对象，如碰撞区域） */
public interface TiledMapTile {

	/** 渲染混合模式 */
	public enum BlendMode {
		/** 不使用混合（完全不透明） */
		NONE,
		/** 使用 alpha 混合（支持透明度） */
		ALPHA
	}

	/** @return 瓦片在瓦片集中的唯一 ID */
	public int getId ();

	/** @param id 瓦片的新 ID */
	public void setId (int id);

	/** @return 渲染该瓦片时使用的 {@link BlendMode} 混合模式 */
	public BlendMode getBlendMode ();

	/** 设置渲染该瓦片时使用的混合模式
	 * @param blendMode 混合模式 */
	public void setBlendMode (BlendMode blendMode);

	/** @return 用于渲染瓦片的纹理区域（图片的一部分） */
	public TextureRegion getTextureRegion ();

	/** 设置用于渲染瓦片的纹理区域
	 * @param textureRegion 纹理区域 */
	public void setTextureRegion (TextureRegion textureRegion);

	/** @return 渲染时 X 方向的偏移量（用于微调瓦片位置） */
	public float getOffsetX ();

	/** 设置渲染时 X 方向的偏移量
	 * @param offsetX X 偏移量（像素） */
	public void setOffsetX (float offsetX);

	/** @return 渲染时 Y 方向的偏移量 */
	public float getOffsetY ();

	/** 设置渲染时 Y 方向的偏移量
	 * @param offsetY Y 偏移量（像素） */
	public void setOffsetY (float offsetY);

	/** @return 瓦片的属性集合（用于存储自定义数据） */
	public MapProperties getProperties ();

	/** @return 瓦片中包含的地图对象集合（如碰撞区域、事件触发器） */
	public MapObjects getObjects ();

}
