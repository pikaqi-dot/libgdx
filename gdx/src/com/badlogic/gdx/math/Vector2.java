/*******************************************************************************
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

import java.io.Serializable;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.NumberUtils;

/** <b>2D 向量类。</b>
 * 封装了一个二维向量 (x, y)。所有修改方法都返回对自身的引用，支持链式调用。
 * 
 * <p>常用常量：{@link #X}（X 轴单位向量）、{@link #Y}（Y 轴单位向量）、
 * {@link #Zero}（零向量）、{@link #One}（所有分量为1的向量）</p>
 * 
 * <p>主要功能：<br>
 * - 加减法：{@link #add(Vector2)}、{@link #sub(Vector2)}<br>
 * - 缩放：{@link #scl(float)}<br>
 * - 向量长度：{@link #len()}、{@link #len2()}（平方长度）、{@link #nor()}（归一化）<br>
 * - 点积/叉积：{@link #dot(Vector2)}、{@link #crs(Vector2)}<br>
 * - 角度操作：{@link #angleDeg()}、{@link #rotateDeg(float)}、{@link #setAngleDeg(float)}<br>
 * - 距离计算：{@link #dst(Vector2)}、{@link #dst2(Vector2)}<br>
 * - 限制：{@link #limit(float)}、{@link #clamp(float, float)}、{@link #setLength(float)}<br>
 * - 插值：{@link #lerp(Vector2, float)}</p>
 *
 * @author badlogicgames@gmail.com */
public class Vector2 implements Serializable, Vector<Vector2> {
	private static final long serialVersionUID = 913902788239530931L;

	/** X 轴单位向量 (1, 0) */
	public final static Vector2 X = new Vector2(1, 0);
	/** Y 轴单位向量 (0, 1) */
	public final static Vector2 Y = new Vector2(0, 1);
	/** 零向量 (0, 0) */
	public final static Vector2 Zero = new Vector2(0, 0);
	/** 所有分量为1的向量 (1, 1) */
	public final static Vector2 One = new Vector2(1, 1);

	/** 向量的 x 分量 **/
	public float x;
	/** 向量的 y 分量 **/
	public float y;

	/** 构造一个位于 (0,0) 的向量 */
	public Vector2 () {
	}

	/** 使用指定分量构造向量
	 * @param x x 分量
	 * @param y y 分量 */
	public Vector2 (float x, float y) {
		this.x = x;
		this.y = y;
	}

	/** 从给定向量复制构造
	 * @param v 源向量 */
	public Vector2 (Vector2 v) {
		set(v);
	}

	@Override
	public Vector2 cpy () {
		return new Vector2(this);
	}

	public static float len (float x, float y) {
		return (float)Math.sqrt(x * x + y * y);
	}

	@Override
	public float len () {
		return (float)Math.sqrt(x * x + y * y);
	}

	public static float len2 (float x, float y) {
		return x * x + y * y;
	}

	@Override
	public float len2 () {
		return x * x + y * y;
	}

	@Override
	public Vector2 set (Vector2 v) {
		x = v.x;
		y = v.y;
		return this;
	}

	/** 设置此向量的分量
	 * @param x x 分量
	 * @param y y 分量
	 * @return 当前向量（链式调用） */
	public Vector2 set (float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	@Override
	public Vector2 sub (Vector2 v) {
		x -= v.x;
		y -= v.y;
		return this;
	}

	/** 从此向量中减去另一个向量。
	 * @param x 另一个向量的 x 分量
	 * @param y 另一个向量的 y 分量
	 * @return 当前向量（链式调用） */
	public Vector2 sub (float x, float y) {
		this.x -= x;
		this.y -= y;
		return this;
	}

	@Override
	public Vector2 nor () {
		float len = len();
		if (len != 0) {
			x /= len;
			y /= len;
		}
		return this;
	}

	@Override
	public Vector2 add (Vector2 v) {
		x += v.x;
		y += v.y;
		return this;
	}

	/** 将给定分量添加到此向量
	 * @param x x 分量
	 * @param y y 分量
	 * @return 当前向量（链式调用） */
	public Vector2 add (float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public static float dot (float x1, float y1, float x2, float y2) {
		return x1 * x2 + y1 * y2;
	}

	@Override
	public float dot (Vector2 v) {
		return x * v.x + y * v.y;
	}

	public float dot (float ox, float oy) {
		return x * ox + y * oy;
	}

	@Override
	public Vector2 scl (float scalar) {
		x *= scalar;
		y *= scalar;
		return this;
	}

	/** 分别缩放向量的每个分量
	 * @return 当前向量（链式调用） */
	public Vector2 scl (float x, float y) {
		this.x *= x;
		this.y *= y;
		return this;
	}

	@Override
	public Vector2 scl (Vector2 v) {
		this.x *= v.x;
		this.y *= v.y;
		return this;
	}

	@Override
	public Vector2 mulAdd (Vector2 vec, float scalar) {
		this.x += vec.x * scalar;
		this.y += vec.y * scalar;
		return this;
	}

	@Override
	public Vector2 mulAdd (Vector2 vec, Vector2 mulVec) {
		this.x += vec.x * mulVec.x;
		this.y += vec.y * mulVec.y;
		return this;
	}

	/** 如果此向量与参数向量具有完全相同的分量值，返回 true。
	 * @param vector 另一个向量
	 * @return 是否精确相等 */
	public boolean idt (final Vector2 vector) {
		return x == vector.x && y == vector.y;
	}

	public static float dst (float x1, float y1, float x2, float y2) {
		final float x_d = x2 - x1;
		final float y_d = y2 - y1;
		return (float)Math.sqrt(x_d * x_d + y_d * y_d);
	}

	@Override
	public float dst (Vector2 v) {
		final float x_d = v.x - x;
		final float y_d = v.y - y;
		return (float)Math.sqrt(x_d * x_d + y_d * y_d);
	}

	/** @param x 另一个向量的 x 分量
	 * @param y 另一个向量的 y 分量
	 * @return 此向量与另一个向量的距离 */
	public float dst (float x, float y) {
		final float x_d = x - this.x;
		final float y_d = y - this.y;
		return (float)Math.sqrt(x_d * x_d + y_d * y_d);
	}

	public static float dst2 (float x1, float y1, float x2, float y2) {
		final float x_d = x2 - x1;
		final float y_d = y2 - y1;
		return x_d * x_d + y_d * y_d;
	}

	@Override
	public float dst2 (Vector2 v) {
		final float x_d = v.x - x;
		final float y_d = v.y - y;
		return x_d * x_d + y_d * y_d;
	}

	/** @param x 另一个向量的 x 分量
	 * @param y 另一个向量的 y 分量
	 * @return 此向量与另一个向量的平方距离 */
	public float dst2 (float x, float y) {
		final float x_d = x - this.x;
		final float y_d = y - this.y;
		return x_d * x_d + y_d * y_d;
	}

	@Override
	public Vector2 limit (float limit) {
		return limit2(limit * limit);
	}

	@Override
	public Vector2 limit2 (float limit2) {
		float len2 = len2();
		if (len2 > limit2) {
			return scl((float)Math.sqrt(limit2 / len2));
		}
		return this;
	}

	@Override
	public Vector2 clamp (float min, float max) {
		final float len2 = len2();
		if (len2 == 0f) return this;
		float max2 = max * max;
		if (len2 > max2) return scl((float)Math.sqrt(max2 / len2));
		float min2 = min * min;
		if (len2 < min2) return scl((float)Math.sqrt(min2 / len2));
		return this;
	}

	@Override
	public Vector2 setLength (float len) {
		return setLength2(len * len);
	}

	@Override
	public Vector2 setLength2 (float len2) {
		float oldLen2 = len2();
		return (oldLen2 == 0 || oldLen2 == len2) ? this : scl((float)Math.sqrt(len2 / oldLen2));
	}

	/** 将此 {@code Vector2} 转换为字符串，格式为 {@code (x,y)}。
	 * @return 字符串表示 */
	@Override
	public String toString () {
		return "(" + x + "," + y + ")";
	}

	/** 将字符串（格式为 {@link #toString()}）解析为此向量的值。
	 * @param v 字符串
	 * @return 当前向量（链式调用） */
	public Vector2 fromString (String v) {
		int s = v.indexOf(',', 1);
		if (s != -1 && v.charAt(0) == '(' && v.charAt(v.length() - 1) == ')') {
			try {
				float x = Float.parseFloat(v.substring(1, s));
				float y = Float.parseFloat(v.substring(s + 1, v.length() - 1));
				return this.set(x, y);
			} catch (NumberFormatException ex) {
				// Throw a GdxRuntimeException
			}
		}
		throw new GdxRuntimeException("Malformed Vector2: " + v);
	}

	/** 左乘给定矩阵（3x3 变换矩阵）
	 * @param mat 矩阵
	 * @return 当前向量 */
	public Vector2 mul (Matrix3 mat) {
		float x = this.x * mat.val[0] + this.y * mat.val[3] + mat.val[6];
		float y = this.x * mat.val[1] + this.y * mat.val[4] + mat.val[7];
		this.x = x;
		this.y = y;
		return this;
	}

	/** 计算此向量与给定向量的 2D 叉积（标量）。
	 * @param v 另一个向量
	 * @return 叉积：this.x * v.y - this.y * v.x */
	public float crs (Vector2 v) {
		return this.x * v.y - this.y * v.x;
	}

	/** 计算此向量与给定向量的 2D 叉积（标量）。
	 * @param x 另一个向量的 x 坐标
	 * @param y 另一个向量的 y 坐标
	 * @return 叉积 */
	public float crs (float x, float y) {
		return this.x * y - this.y * x;
	}

	/** @return 此向量相对于 X 轴的角度（度），朝正 Y 轴方向（逆时针），范围 [0, 360)。
	 * @deprecated 使用 {@link #angleDeg()} 替代 */
	@Deprecated
	public float angle () {
		float angle = (float)Math.atan2(y, x) * MathUtils.radiansToDegrees;
		if (angle < 0) angle += 360;
		return angle;
	}

	/** @return 此向量相对于给定向量的角度（度），范围为 -180 到 +180
	 * @deprecated 使用 {@link #angleDeg(Vector2)} 替代。注意返回值范围的变化。 */
	@Deprecated
	public float angle (Vector2 reference) {
		return (float)Math.atan2(crs(reference), dot(reference)) * MathUtils.radiansToDegrees;
	}

	/** @return 此向量相对于 X 轴的角度（度），朝正 Y 轴方向（逆时针），范围 [0, 360) */
	public float angleDeg () {
		float angle = (float)Math.atan2(y, x) * MathUtils.radiansToDegrees;
		if (angle < 0) angle += 360;
		return angle;
	}

	/** @return 此向量相对于给定向量的角度（度），朝正 Y 轴方向（逆时针），范围 [0, 360) */
	public float angleDeg (Vector2 reference) {
		float angle = (float)Math.atan2(reference.crs(this), reference.dot(this)) * MathUtils.radiansToDegrees;
		if (angle < 0) angle += 360;
		return angle;
	}

	/** @return 给定坐标相对于 X 轴的角度（度），朝正 Y 轴方向（逆时针），范围 [0, 360) */
	public static float angleDeg (float x, float y) {
		float angle = (float)Math.atan2(y, x) * MathUtils.radiansToDegrees;
		if (angle < 0) angle += 360;
		return angle;
	}

	/** @return 此向量相对于 X 轴的角度（弧度），朝正 Y 轴方向（逆时针） */
	public float angleRad () {
		return (float)Math.atan2(y, x);
	}

	/** @return 此向量相对于给定向量的角度（弧度），朝正 Y 轴方向（逆时针） */
	public float angleRad (Vector2 reference) {
		return (float)Math.atan2(reference.crs(this), reference.dot(this));
	}

	/** @return 给定坐标相对于 X 轴的角度（弧度），朝正 Y 轴方向（逆时针） */
	public static float angleRad (float x, float y) {
		return (float)Math.atan2(y, x);
	}

	/** 设置向量的方向角度（度），朝正 Y 轴方向（逆时针）。
	 * @param degrees 要设置的角度（度）
	 * @deprecated 使用 {@link #setAngleDeg(float)} 替代 */
	@Deprecated
	public Vector2 setAngle (float degrees) {
		return setAngleRad(degrees * MathUtils.degreesToRadians);
	}

	/** 设置向量的方向角度（度），朝正 Y 轴方向（逆时针）。
	 * @param degrees 要设置的角度（度） */
	public Vector2 setAngleDeg (float degrees) {
		return setAngleRad(degrees * MathUtils.degreesToRadians);
	}

	/** 设置向量的方向角度（弧度），朝正 Y 轴方向（逆时针）。
	 * @param radians 要设置的角度（弧度） */
	public Vector2 setAngleRad (float radians) {
		this.set(len(), 0f);
		this.rotateRad(radians);

		return this;
	}

	/** 旋转 Vector2，朝正 Y 轴方向（逆时针）。
	 * @param degrees 角度（度）
	 * @deprecated 使用 {@link #rotateDeg(float)} 替代 */
	@Deprecated
	public Vector2 rotate (float degrees) {
		return rotateRad(degrees * MathUtils.degreesToRadians);
	}

	/** 绕参考点旋转 Vector2，朝正 Y 轴方向（逆时针）。
	 * @param degrees 角度（度）
	 * @param reference 参考点
	 * @deprecated 使用 {@link #rotateAroundDeg(Vector2, float)} 替代 */
	@Deprecated
	public Vector2 rotateAround (Vector2 reference, float degrees) {
		return this.sub(reference).rotateDeg(degrees).add(reference);
	}

	/** 旋转 Vector2，朝正 Y 轴方向（逆时针）。
	 * @param degrees 角度（度） */
	public Vector2 rotateDeg (float degrees) {
		return rotateRad(degrees * MathUtils.degreesToRadians);
	}

	/** 旋转 Vector2，朝正 Y 轴方向（逆时针）。
	 * @param radians 角度（弧度） */
	public Vector2 rotateRad (float radians) {
		float cos = (float)Math.cos(radians);
		float sin = (float)Math.sin(radians);

		float newX = this.x * cos - this.y * sin;
		float newY = this.x * sin + this.y * cos;

		this.x = newX;
		this.y = newY;

		return this;
	}

	/** 绕参考点旋转 Vector2，朝正 Y 轴方向（逆时针）。
	 * @param degrees 角度（度）
	 * @param reference 参考点 */
	public Vector2 rotateAroundDeg (Vector2 reference, float degrees) {
		return this.sub(reference).rotateDeg(degrees).add(reference);
	}

	/** 绕参考点旋转 Vector2，朝正 Y 轴方向（逆时针）。
	 * @param radians 角度（弧度）
	 * @param reference 参考点 */
	public Vector2 rotateAroundRad (Vector2 reference, float radians) {
		return this.sub(reference).rotateRad(radians).add(reference);
	}

	/** 按指定方向将 Vector2 旋转 90 度，dir >= 0 为逆时针，dir < 0 为顺时针 */
	public Vector2 rotate90 (int dir) {
		float x = this.x;
		if (dir >= 0) {
			this.x = -y;
			y = x;
		} else {
			this.x = y;
			y = -x;
		}
		return this;
	}

	@Override
	public Vector2 lerp (Vector2 target, float alpha) {
		final float invAlpha = 1.0f - alpha;
		this.x = (x * invAlpha) + (target.x * alpha);
		this.y = (y * invAlpha) + (target.y * alpha);
		return this;
	}

	@Override
	public Vector2 interpolate (Vector2 target, float alpha, Interpolation interpolation) {
		return lerp(target, interpolation.apply(alpha));
	}

	@Override
	public Vector2 setToRandomDirection () {
		float theta = MathUtils.random(0f, MathUtils.PI2);
		return this.set(MathUtils.cos(theta), MathUtils.sin(theta));
	}

	@Override
	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + NumberUtils.floatToIntBits(x);
		result = prime * result + NumberUtils.floatToIntBits(y);
		return result;
	}

	@Override
	public boolean equals (Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Vector2 other = (Vector2)obj;
		if (NumberUtils.floatToIntBits(x) != NumberUtils.floatToIntBits(other.x)) return false;
		if (NumberUtils.floatToIntBits(y) != NumberUtils.floatToIntBits(other.y)) return false;
		return true;
	}

	@Override
	public boolean epsilonEquals (Vector2 other, float epsilon) {
		if (other == null) return false;
		if (Math.abs(other.x - x) > epsilon) return false;
		if (Math.abs(other.y - y) > epsilon) return false;
		return true;
	}

	/** 使用指定 epsilon 进行模糊相等性比较。
	 * @return 向量是否相同 */
	public boolean epsilonEquals (float x, float y, float epsilon) {
		if (Math.abs(x - this.x) > epsilon) return false;
		if (Math.abs(y - this.y) > epsilon) return false;
		return true;
	}

	/** 使用 MathUtils.FLOAT_ROUNDING_ERROR 进行模糊相等性比较
	 * @param other 另一个向量
	 * @return 是否相等 */
	public boolean epsilonEquals (final Vector2 other) {
		return epsilonEquals(other, MathUtils.FLOAT_ROUNDING_ERROR);
	}

	/** 使用 MathUtils.FLOAT_ROUNDING_ERROR 进行模糊相等性比较
	 * @param x 另一个向量的 x 分量
	 * @param y 另一个向量的 y 分量
	 * @return 是否相等 */
	public boolean epsilonEquals (float x, float y) {
		return epsilonEquals(x, y, MathUtils.FLOAT_ROUNDING_ERROR);
	}

	@Override
	public boolean isUnit () {
		return isUnit(0.000000001f);
	}

	@Override
	public boolean isUnit (final float margin) {
		return Math.abs(len2() - 1f) < margin;
	}

	@Override
	public boolean isZero () {
		return x == 0 && y == 0;
	}

	@Override
	public boolean isZero (final float margin) {
		return len2() < margin;
	}

	@Override
	public boolean isOnLine (Vector2 other) {
		return MathUtils.isZero(x * other.y - y * other.x);
	}

	@Override
	public boolean isOnLine (Vector2 other, float epsilon) {
		return MathUtils.isZero(x * other.y - y * other.x, epsilon);
	}

	@Override
	public boolean isCollinear (Vector2 other, float epsilon) {
		return isOnLine(other, epsilon) && dot(other) > 0f;
	}

	@Override
	public boolean isCollinear (Vector2 other) {
		return isOnLine(other) && dot(other) > 0f;
	}

	@Override
	public boolean isCollinearOpposite (Vector2 other, float epsilon) {
		return isOnLine(other, epsilon) && dot(other) < 0f;
	}

	@Override
	public boolean isCollinearOpposite (Vector2 other) {
		return isOnLine(other) && dot(other) < 0f;
	}

	@Override
	public boolean isPerpendicular (Vector2 vector) {
		return MathUtils.isZero(dot(vector));
	}

	@Override
	public boolean isPerpendicular (Vector2 vector, float epsilon) {
		return MathUtils.isZero(dot(vector), epsilon);
	}

	@Override
	public boolean hasSameDirection (Vector2 vector) {
		return dot(vector) > 0;
	}

	@Override
	public boolean hasOppositeDirection (Vector2 vector) {
		return dot(vector) < 0;
	}

	@Override
	public Vector2 setZero () {
		this.x = 0;
		this.y = 0;
		return this;
	}
}
