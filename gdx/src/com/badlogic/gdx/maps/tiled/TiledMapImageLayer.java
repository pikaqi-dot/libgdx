/*******************************************************************************
 * <b>图片图层，显示单张图片作为图层。</b>
 * 
 * Copyright 2015 See AUTHORS file.
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

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;

/** <b>Tiled 图片图层。</b>
 * 在 Tiled 地图中显示单张图片作为图层（如背景图、Logo 等）。
 * 与瓦片图层不同，图片图层不是由瓦片网格组成的，
 * 而是一张完整的图片，可以设置位置和是否重复平铺。
 * 继承自 {@link MapLayer}，支持透明度、色调等通用图层属性。 */
public class TiledMapImageLayer extends MapLayer {

	/** 图片图层的纹理区域 */
	private TextureRegion region;

	/** 图片的 X 位置（像素） */
	private float x;
	/** 图片的 Y 位置（像素） */
	private float y;
	/** 是否在 X 方向重复平铺 */
	private boolean repeatX;
	/** 是否在 Y 方向重复平铺 */
	private boolean repeatY;
	/** 图片是否支持透明度（由像素格式决定） */
	private boolean supportsTransparency;

	/** 创建图片图层
	 * @param region 纹理区域
	 * @param x X 位置
	 * @param y Y 位置
	 * @param repeatX X 方向重复
	 * @param repeatY Y 方向重复 */
	public TiledMapImageLayer (TextureRegion region, float x, float y, boolean repeatX, boolean repeatY) {
		this.region = region;
		this.x = x;
		this.y = y;
		this.repeatX = repeatX;
		this.repeatY = repeatY;
		// 检查纹理格式是否支持透明度
		this.supportsTransparency = checkTransparencySupport(region);
	}

	/** TiledMap 图片图层可以通过色调颜色支持透明度，
	 * 前提是图片使用了正确的像素格式。
	 * 此方法通过检查 TextureData 的格式来判断是否支持透明度。
	 *
	 * @param region 图片图层的纹理区域
	 * @return 是否支持透明度 */
	private boolean checkTransparencySupport (TextureRegion region) {
		Pixmap.Format format = region.getTexture().getTextureData().getFormat();
		return format != null && formatHasAlpha(format);
	}

	/** 检查像素格式是否包含 alpha（透明度）通道
	 * @param format 像素格式
	 * @return 是否包含 alpha 通道 */
	private boolean formatHasAlpha (Pixmap.Format format) {
		switch (format) {
		case Alpha:             // 仅 alpha 通道
		case LuminanceAlpha:    // 亮度 + alpha
		case RGBA4444:          // RGBA 每通道4位
		case RGBA8888:          // RGBA 每通道8位（标准真彩色+透明度）
			return true;
		default:
			return false;
		}
	}

	/** @return 图片图层的纹理区域 */
	public TextureRegion getTextureRegion () {
		return region;
	}

	/** @return 图片的 X 位置 */
	public float getX () {
		return x;
	}

	/** @param x 图片的新 X 位置 */
	public void setX (float x) {
		this.x = x;
	}

	/** @return 图片的 Y 位置 */
	public float getY () {
		return y;
	}

	/** @param y 图片的新 Y 位置 */
	public void setY (float y) {
		this.y = y;
	}

	/** @return 是否在 X 方向重复平铺 */
	public boolean isRepeatX () {
		return repeatX;
	}

	/** @param repeatX 设置 X 方向重复平铺 */
	public void setRepeatX (boolean repeatX) {
		this.repeatX = repeatX;
	}

	/** @return 是否在 Y 方向重复平铺 */
	public boolean isRepeatY () {
		return repeatY;
	}

	/** @param repeatY 设置 Y 方向重复平铺 */
	public void setRepeatY (boolean repeatY) {
		this.repeatY = repeatY;
	}

	/** @return 图片是否支持透明度 */
	public boolean supportsTransparency () {
		return supportsTransparency;
	}
}
