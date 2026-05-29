/*******************************************************************************
 * <b>耳切法三角剖分器，用于简单多边形的三角化</b>
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

package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ShortArray;

/** A simple implementation of the ear cutting algorithm to triangulate simple polygons without holes. For more information:
 * <ul>
 * <li><a href="http://cgm.cs.mcgill.ca/~godfried/teaching/cg-projects/97/Ian/algorithm2.html">http://cgm.cs.mcgill.ca/~godfried/
 * teaching/cg-projects/97/Ian/algorithm2.html</a></li>
 * <li><a href=
 * "http://www.geometrictools.com/Documentation/TriangulationByEarClipping.pdf">http://www.geometrictools.com/Documentation
 * /TriangulationByEarClipping.pdf</a></li>
 * </ul>
 * If the input polygon is not simple (self-intersects), there will be output but it is of unspecified quality (garbage in,
 * garbage out).
 * <p>
 * If the polygon vertices are very large or very close together then {@link GeometryUtils#isClockwise(float[], int, int)} may not
 * be able to properly assess the winding (because it uses floats). In that case the vertices should be adjusted, eg by finding
 * the smallest X and Y values and subtracting that from each vertex.
 * @author badlogicgames@gmail.com
 * @author Nicolas Gramlich (optimizations, collinear edge support)
 * @author Eric Spitz
 * @author Thomas ten Cate (bugfixes, optimizations)
 * @author Nathan Sweet (rewrite, return indices, no allocation, optimizations) */
public class EarClippingTriangulator {
	static private final int CONCAVE = -1;
	static private final int CONVEX = 1;

	private final ShortArray indicesArray = new ShortArray();
	private short[] indices;
	private float[] vertices;
	private int vertexCount;
	private final IntArray vertexTypes = new IntArray();
	private final ShortArray triangles = new ShortArray();

	/** @see #computeTriangles(float[], int, int) */
	public ShortArray computeTriangles (FloatArray vertices) {
		return computeTriangles(vertices.items, 0, vertices.size);
	}

	/** @see #computeTriangles(float[], int, int) */
	public ShortArray computeTriangles (float[] vertices) {
		return computeTriangles(vertices, 0, vertices.length);
	}

	/** Triangulates the given (convex or concave) simple polygon to a list of triangle vertices.
	 * @param vertices pairs describing vertices of the polygon, in either clockwise or counterclockwise order.
	 * @return triples of triangle indices in clockwise order. Note the returned array is reused for later calls to the same
	 *         method. */
	/** 对简单多边形执行耳切法三角剖分。
	 * 算法原理：
	 * 1. 将输入顶点调整为顺时针顺序
	 * 2. 分类所有顶点为凸(convex)或凹(concave)
	 * 3. 重复查找"耳朵"（由相邻顶点组成的凸三角形，内部不含其他顶点）
	 * 4. 切掉耳朵（输出三角形），更新相邻顶点类型
	 * 5. 直到只剩3个顶点，形成最后一个三角形
	 * @param vertices x,y 成对出现的多边形顶点
	 * @param offset 偏移
	 
	*
	 
	@
	p
	a
	r
	a
	m
	 
	c
	o
	u
	n
	t
	 
	总
	数
	 * @return 三角形顶点索引（每3个一组） */
	public ShortArray computeTriangles (float[] vertices, int offset, int count) {
		this.vertices = vertices;
		int vertexCount = this.vertexCount = count / 2;
		int vertexOffset = offset / 2;

		ShortArray indicesArray = this.indicesArray;
		indicesArray.clear();
		indicesArray.ensureCapacity(vertexCount);
		indicesArray.size = vertexCount;
		short[] indices = this.indices = indicesArray.items;
		if (GeometryUtils.isClockwise(vertices, offset, count)) {
			for (short i = 0; i < vertexCount; i++)
				indices[i] = (short)(vertexOffset + i);
		} else {
			for (int i = 0, n = vertexCount - 1; i < vertexCount; i++)
				indices[i] = (short)(vertexOffset + n - i); // Reversed.
		}

		IntArray vertexTypes = this.vertexTypes;
		vertexTypes.clear();
		vertexTypes.ensureCapacity(vertexCount);
		for (int i = 0, n = vertexCount; i < n; ++i)
			vertexTypes.add(classifyVertex(i));

		// A polygon with n vertices has a triangulation of n-2 triangles.
		ShortArray triangles = this.triangles;
		triangles.clear();
		triangles.ensureCapacity(Math.max(0, vertexCount - 2) * 3);
		triangulate();
		return triangles;
	}

	/** 执行耳切法主循环。
	 * 每次迭代找到一个耳尖(ear tip)并切除，
	 * 更新相邻顶点的凸凹类型，直到剩余顶点数 <= 3 */
	private void triangulate () {
		int[] vertexTypes = this.vertexTypes.items;

		while (vertexCount > 3) {
			int earTipIndex = findEarTip();
			cutEarTip(earTipIndex);

			// The type of the two vertices adjacent to the clipped vertex may have changed.
			int previousIndex = previousIndex(earTipIndex);
			int nextIndex = earTipIndex == vertexCount ? 0 : earTipIndex;
			vertexTypes[previousIndex] = classifyVertex(previousIndex);
			vertexTypes[nextIndex] = classifyVertex(nextIndex);
		}

		if (vertexCount == 3) {
			ShortArray triangles = this.triangles;
			short[] indices = this.indices;
			triangles.add(indices[0]);
			triangles.add(indices[1]);
			triangles.add(indices[2]);
		}
	}

	/** @return {@link #CONCAVE} or {@link #CONVEX} */
	private int classifyVertex (int index) {
		short[] indices = this.indices;
		int previous = indices[previousIndex(index)] * 2;
		int current = indices[index] * 2;
		int next = indices[nextIndex(index)] * 2;
		float[] vertices = this.vertices;
		return computeSpannedAreaSign(vertices[previous], vertices[previous + 1], vertices[current], vertices[current + 1],
			vertices[next], vertices[next + 1]);
	}

	/** 查找耳尖顶点。耳尖需满足：
	 * 1. 是凸顶点（不是凹顶点）
	 * 2. 由其和前后邻点组成的三角形内部不包含其他任何顶点
	 * 如果找不到耳尖（退化多边形），使用 Held 的 FIST 方法：
	 * 优先返回凸顶点或切线顶点 */
	private int findEarTip () {
		int vertexCount = this.vertexCount;
		for (int i = 0; i < vertexCount; i++)
			if (isEarTip(i)) return i;

		// Desperate mode: if no vertex is an ear tip, we are dealing with a degenerate polygon (e.g. nearly collinear).
		// Note that the input was not necessarily degenerate, but we could have made it so by clipping some valid ears.

		// Idea taken from Martin Held, "FIST: Fast industrial-strength triangulation of polygons", Algorithmica (1998),
		// http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.115.291

		// Return a convex or tangential vertex if one exists.
		int[] vertexTypes = this.vertexTypes.items;
		for (int i = 0; i < vertexCount; i++)
			if (vertexTypes[i] != CONCAVE) return i;
		return 0; // If all vertices are concave, just return the first one.
	}

	/** 判断指定顶点是否为耳尖。
	 * 通过检查三角形(p1,p2,p3)内是否包含其他顶点来判断：
	 * 对于每个不在三角形边上的顶点v，如果v在三角形内部（三个叉积都 >= 0），
	 * 则p2不是耳尖 */
	private boolean isEarTip (int earTipIndex) {
		int[] vertexTypes = this.vertexTypes.items;
		if (vertexTypes[earTipIndex] == CONCAVE) return false;

		int previousIndex = previousIndex(earTipIndex);
		int nextIndex = nextIndex(earTipIndex);
		short[] indices = this.indices;
		int p1 = indices[previousIndex] * 2;
		int p2 = indices[earTipIndex] * 2;
		int p3 = indices[nextIndex] * 2;
		float[] vertices = this.vertices;
		float p1x = vertices[p1], p1y = vertices[p1 + 1];
		float p2x = vertices[p2], p2y = vertices[p2 + 1];
		float p3x = vertices[p3], p3y = vertices[p3 + 1];

		// Check if any point is inside the triangle formed by previous, current and next vertices.
		// Only consider vertices that are not part of this triangle, or else we'll always find one inside.
		for (int i = nextIndex(nextIndex); i != previousIndex; i = nextIndex(i)) {
			// Concave vertices can obviously be inside the candidate ear, but so can tangential vertices
			// if they coincide with one of the triangle's vertices.
			if (vertexTypes[i] != CONVEX) {
				int v = indices[i] * 2;
				float vx = vertices[v];
				float vy = vertices[v + 1];
				// Because the polygon has clockwise winding order, the area sign will be positive if the point is strictly inside.
				// It will be 0 on the edge, which we want to include as well.
				// note: check the edge defined by p1->p3 first since this fails _far_ more then the other 2 checks.
				if (computeSpannedAreaSign(p3x, p3y, p1x, p1y, vx, vy) >= 0) {
					if (computeSpannedAreaSign(p1x, p1y, p2x, p2y, vx, vy) >= 0) {
						if (computeSpannedAreaSign(p2x, p2y, p3x, p3y, vx, vy) >= 0) return false;
					}
				}
			}
		}
		return true;
	}

	/** 切除耳尖顶点：
	 * 1. 将三角形(prev, earTip, next)加入结果
	 * 2. 从索引数组中移除耳尖顶点
	 * 3. 更新顶点计数 */
	private void cutEarTip (int earTipIndex) {
		short[] indices = this.indices;
		ShortArray triangles = this.triangles;

		triangles.add(indices[previousIndex(earTipIndex)]);
		triangles.add(indices[earTipIndex]);
		triangles.add(indices[nextIndex(earTipIndex)]);

		indicesArray.removeIndex(earTipIndex);
		vertexTypes.removeIndex(earTipIndex);
		vertexCount--;
	}

	private int previousIndex (int index) {
		return (index == 0 ? vertexCount : index) - 1;
	}

	private int nextIndex (int index) {
		return (index + 1) % vertexCount;
	}

	/** 计算有向面积符号，用于判断点的位置关系。
	 * 使用鞋带公式(shoelace formula)计算三角形面积：
	 * area = (p1x*(p3y-p2y) + p2x*(p1y-p3y) + p3x*(p2y-p1y)) / 2
	 * 但此处只返回符号，省略除以2
	 * @return 1（逆时针）、-1（顺时针）或 0（共线） */
	static private int computeSpannedAreaSign (float p1x, float p1y, float p2x, float p2y, float p3x, float p3y) {
		float area = p1x * (p3y - p2y);
		area += p2x * (p1y - p3y);
		area += p3x * (p2y - p1y);
		return (int)Math.signum(area);
	}
}
