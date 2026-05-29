/*******************************************************************************
 * <b>地图图层，包含一组地图对象和属性，支持透明度、偏移、视差滚动等。</b>
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

package com.badlogic.gdx.maps;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.GdxRuntimeException;

/** <b>地图图层。</b>
 * 地图中的一个图层，包含一组地图对象(MapObjects)和属性(MapProperties)。
 * 支持图层透明度、颜色色调、偏移量、视差滚动和可见性控制。
 * 图层可以嵌套（通过 parent 属性），子图层的偏移量会累加到父图层上。 */
public class MapLayer {
	/** 图层名称 */
	private String name = "";
	/** 图层透明度，范围 [0,1]，0=完全透明，1=完全不透明 */
	private float opacity = 1.0f;
	/** 图层色调颜色（叠加到所有对象上） */
	private Color tintColor = new Color(Color.WHITE);
	/** 临时颜色，用于计算组合色调（避免重复分配对象） */
	private Color tempColor = new Color(Color.WHITE);
	/** 图层是否可见 */
	private boolean visible = true;
	/** 图层 X 偏移量（相对父图层） */
	private float offsetX;
	/** 图层 Y 偏移量（相对父图层） */
	private float offsetY;
	/** 计算后的最终 X 渲染偏移量（包含父图层的偏移） */
	private float renderOffsetX;
	/** 计算后的最终 Y 渲染偏移量（包含父图层的偏移） */
	private float renderOffsetY;
	/** X 轴视差滚动因子：1=跟随摄像机，0=不跟随，>1=更快，<1=更慢 */
	private float parallaxX = 1;
	/** Y 轴视差滚动因子 */
	private float parallaxY = 1;
	/** 渲染偏移是否脏（需要重新计算） */
	private boolean renderOffsetDirty = true;
	/** 父图层，用于图层嵌套 */
	private MapLayer parent;
	/** 图层中包含的地图对象集合 */
	private MapObjects objects = new MapObjects();
	/** 图层的属性集合（键值对） */
	private MapProperties properties = new MapProperties();

	/** @return 图层名称 */
	public String getName () {
		return name;
	}

	/** @param name 图层的新名称 */
	public void setName (String name) {
		this.name = name;
	}

	/** 获取图层的最终透明度。
	 * 如果图层有父图层，则透明度为当前图层透明度乘以父图层的透明度，
	 * 实现父子图层的透明度叠加效果。
	 * @return 图层透明度 [0,1] */
	public float getOpacity () {
		if (parent != null)
			return opacity * parent.getOpacity();
		else
			return opacity;
	}

	/** @param opacity 图层的新透明度 [0,1] */
	public void setOpacity (float opacity) {
		this.opacity = opacity;
	}

	/** 获取组合后的色调颜色（当前图层色调 x 父图层色调）。
	 * 通过颜色乘法实现父子图层色调的叠加效果。
	 * 返回的临时颜色对象是复用的，不应持有引用或修改。
	 * @return 组合后的色调颜色 */
	public Color getCombinedTintColor () {
		if (parent != null) {
			return tempColor.set(tintColor).mul(parent.getCombinedTintColor());
		} else {
			return tempColor.set(tintColor);
		}
	}

	/** @return 图层的色调颜色 */
	public Color getTintColor () {
		return tintColor;
	}

	/** @param tintColor 图层的新色调颜色 */
	public void setTintColor (Color tintColor) {
		this.tintColor.set(tintColor);
	}

	/** @return 图层的 X 偏移量 */
	public float getOffsetX () {
		return offsetX;
	}

	/** 设置图层的 X 偏移量，并标记渲染偏移为脏，以便下次获取时重新计算。 */
	public void setOffsetX (float offsetX) {
		this.offsetX = offsetX;
		invalidateRenderOffset();
	}

	/** @return 图层的 Y 偏移量 */
	public float getOffsetY () {
		return offsetY;
	}

	/** 设置图层的 Y 偏移量，并标记渲染偏移为脏。 */
	public void setOffsetY (float offsetY) {
		this.offsetY = offsetY;
		invalidateRenderOffset();
	}

	/** @return 图层 X 轴视差滚动因子 */
	public float getParallaxX () {
		return parallaxX;
	}

	/** @param parallaxX X 轴视差滚动因子
	 *  1.0=正常跟随摄像机，0.5=半速（远景效果），2.0=双倍速度（近景效果） */
	public void setParallaxX (float parallaxX) {
		this.parallaxX = parallaxX;
	}

	/** @return 图层 Y 轴视差滚动因子 */
	public float getParallaxY () {
		return parallaxY;
	}

	public void setParallaxY (float parallaxY) {
		this.parallaxY = parallaxY;
	}

	/** 获取最终 X 渲染偏移量（包含所有父图层的偏移累加）。
	 * 如果渲染偏移标记为脏，则先通过 calculateRenderOffsets() 重新计算。
	 * @return 最终 X 渲染偏移 */
	public float getRenderOffsetX () {
		if (renderOffsetDirty) calculateRenderOffsets();
		return renderOffsetX;
	}

	/** 获取最终 Y 渲染偏移量（包含所有父图层的偏移累加）。 */
	public float getRenderOffsetY () {
		if (renderOffsetDirty) calculateRenderOffsets();
		return renderOffsetY;
	}

	/** 标记渲染偏移为脏状态，当下次获取渲染偏移时会重新计算。
	 * 当本图层或任意父图层的偏移量发生变化时需要调用此方法。 **/
	public void invalidateRenderOffset () {
		renderOffsetDirty = true;
	}

	/** @return 图层的父图层，如果图层没有父图层则返回 null **/
	public MapLayer getParent () {
		return parent;
	}

	/** @param parent 图层的新父图层，仅内部使用 **/
	public void setParent (MapLayer parent) {
		if (parent == this) throw new GdxRuntimeException("不能将自身设为父图层");
		this.parent = parent;
	}

	/** @return 图层中包含的地图对象集合 */
	public MapObjects getObjects () {
		return objects;
	}

	/** @return 图层是否可见 */
	public boolean isVisible () {
		return visible;
	}

	/** @param visible 设置图层的可见性 */
	public void setVisible (boolean visible) {
		this.visible = visible;
	}

	/** @return 图层的属性集合 */
	public MapProperties getProperties () {
		return properties;
	}

	/** 递归计算渲染偏移量。
	 * 从当前图层开始向上遍历父图层链，
	 * 将每个图层的偏移量累加到 renderOffsetX/Y 上，
	 * 从而实现子图层在父图层偏移基础上的再次偏移。 */
	protected void calculateRenderOffsets () {
		if (parent != null) {
			// 先确保父图层的偏移已计算
			parent.calculateRenderOffsets();
			// 子图层偏移 = 父图层最终偏移 + 子图层自身偏移
			renderOffsetX = parent.getRenderOffsetX() + offsetX;
			renderOffsetY = parent.getRenderOffsetY() + offsetY;
		} else {
			// 根图层，直接使用自身偏移
			renderOffsetX = offsetX;
			renderOffsetY = offsetY;
		}
		renderOffsetDirty = false;
	}
}
