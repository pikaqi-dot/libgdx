/*******************************************************************************
 * <b>瓦片图层，由二维网格的瓦片单元格组成。</b>
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

import com.badlogic.gdx.maps.MapLayer;

/** <b>Tiled 瓦片图层。</b>
 * TiledMap 中的瓦片图层，由二维网格的 Cell（单元格）组成。
 * 每个 Cell 可以包含一个瓦片(TiledMapTile)，并支持水平翻转、垂直翻转和旋转。
 * 继承自 MapLayer，因此也支持透明度、偏移、视差滚动等通用图层属性。 */
public class TiledMapTileLayer extends MapLayer {

	/** 图层在瓦片单位下的宽度（列数） */
	private int width;
	/** 图层在瓦片单位下的高度（行数） */
	private int height;

	/** 每个瓦片的像素宽度 */
	private int tileWidth;
	/** 每个瓦片的像素高度 */
	private int tileHeight;

	/** 瓦片单元格的二维数组。cells[x][y] 访问第x列第y行的单元格。
	 *  注意：y 轴向上增长，即 cells[0][0] 是左下角。 */
	private Cell[][] cells;

	/** @return 图层的宽度（瓦片列数） */
	public int getWidth () {
		return width;
	}

	/** @return 图层的高度（瓦片行数） */
	public int getHeight () {
		return height;
	}

	/** @return 瓦片的像素宽度 */
	public int getTileWidth () {
		return tileWidth;
	}

	/** @return 瓦片的像素高度 */
	public int getTileHeight () {
		return tileHeight;
	}

	/** 创建 TiledMap 瓦片图层。
	 * 
	 * @param width 图层宽度（瓦片列数）
	 * @param height 图层高度（瓦片行数）
	 * @param tileWidth 每个瓦片的像素宽度
	 * @param tileHeight 每个瓦片的像素高度 */
	public TiledMapTileLayer (int width, int height, int tileWidth, int tileHeight) {
		super();
		this.width = width;
		this.height = height;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		// 初始化瓦片网格，所有位置默认 null（空瓦片）
		this.cells = new Cell[width][height];
	}

	/** 获取指定坐标的瓦片单元格。
	 * @param x 列坐标（从0开始）
	 * @param y 行坐标（从0开始，y向上增长）
	 * @return (x,y) 处的 Cell，如果坐标越界或该位置为空则返回 null */
	public Cell getCell (int x, int y) {
		if (x < 0 || x >= width) return null;
		if (y < 0 || y >= height) return null;
		return cells[x][y];
	}

	/** 设置指定坐标的瓦片单元格。
	 * 如果坐标越界则静默忽略。
	 * @param x 列坐标
	 * @param y 行坐标
	 * @param cell 要设置的 Cell，设为 null 可清除该位置 */
	public void setCell (int x, int y, Cell cell) {
		if (x < 0 || x >= width) return;
		if (y < 0 || y >= height) return;
		cells[x][y] = cell;
	}

	/** <b>瓦片单元格。</b>
	 * 表示瓦片图层中的一个单元，包含瓦片引用和变换属性。
	 * 变换按以下顺序应用：翻转 → 旋转。
	 * 旋转角度为 0°、90°、180°、270°（顺时针）。 */
	public static class Cell {

		/** 该单元格使用的瓦片 */
		private TiledMapTile tile;

		/** 是否水平翻转（左右镜像） */
		private boolean flipHorizontally;

		/** 是否垂直翻转（上下镜像） */
		private boolean flipVertically;

		/** 旋转角度（顺时针），仅支持 0、90、180、270 */
		private int rotation;

		/** @return 当前分配给此单元格的瓦片。 */
		public TiledMapTile getTile () {
			return tile;
		}

		/** 设置此单元格的瓦片。
		 * @param tile 要设置的瓦片，设为 null 可清除 */
		public void setTile (TiledMapTile tile) {
			this.tile = tile;
		}

		/** @return 是否水平翻转 */
		public boolean getFlipHorizontally () {
			return flipHorizontally;
		}

		/** 设置是否水平翻转。
		 * @param value true=水平翻转（左右镜像） */
		public void setFlipHorizontally (boolean value) {
			this.flipHorizontally = value;
		}

		/** @return 是否垂直翻转 */
		public boolean getFlipVertically () {
			return flipVertically;
		}

		/** 设置是否垂直翻转。
		 * @param value true=垂直翻转（上下镜像） */
		public void setFlipVertically (boolean value) {
			this.flipVertically = value;
		}

		/** @return 旋转角度，0、90、180 或 270（顺时针） */
		public int getRotation () {
			return rotation;
		}

		/** 设置旋转角度。
		 * @param value 旋转角度，仅支持 0、90、180、270（顺时针）
		 * @throws IllegalArgumentException 如果角度不是90的倍数 */
		public void setRotation (int value) {
			if (value % 90 != 0) throw new IllegalArgumentException("旋转角度必须是90的倍数");
			this.rotation = value;
		}

		/** 获取翻转和旋转的编码值。
		 * 编码方式：bit0=水平翻转，bit1=垂直翻转，bit2-3=旋转(0/1/2/3对应0/90/180/270度)
		 * 这是 Tiled 编辑器中用于描述瓦片变换的标准编码方式。
		 * @return 编码后的翻转旋转值 */
		public int getFlipAndRotationValue () {
			int result = rotation;
			if (flipHorizontally) result += 0x10000000;
			if (flipVertically) result += 0x20000000;
			return result;
		}

		/** 设置翻转属性，使其与给定的编码值匹配。
		 * @param value 编码值（由 getFlipAndRotationValue 生成或从 Tiled 地图文件读取） */
		public void setFlipAndRotationValue (int value) {
			rotation = value & 0x0FFFFFFF;
			// 去除旋转部分后检查翻转标志位
			flipHorizontally = (value & 0x10000000) != 0;
			flipVertically = (value & 0x20000000) != 0;
		}
	}
}
