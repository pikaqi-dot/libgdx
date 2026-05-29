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

/** <b>3D 向量类。</b>
 * 封装了一个三维向量 (x, y, z)。所有修改方法都返回对自身的引用，支持链式调用。
 * 
 * <p>常用常量：{@link #X}（X 轴单位向量）、{@link #Y}、{@link #Z}、
 * {@link #Zero}（零向量）、{@link #One}（所有分量为1的向量）</p>
 * 
 * <p>主要功能：<br>
 * - 加减乘除：{@link #add(Vector3)}、{@link #sub(Vector3)}、{@link #scl(float)}<br>
 * - 向量长度：{@link #len()}、{@link #nor()}（归一化）<br>
 * - 点积/叉积：{@link #dot(Vector3)}、{@link #crs(Vector3)}<br>
 * - 矩阵变换：{@link #mul(Matrix4)}（仿射变换）、{@link #rot(Matrix4)}（仅旋转缩放）<br>
 * - 投影：{@link #prj(Matrix4)}（透视投影）<br>
 * - 四元数旋转：{@link #mul(Quaternion)}<br>
 * - 球面线性插值：{@link #slerp(Vector3, float)}</p>
 *
 * @author badlogicgames@gmail.com */
public class Vector3 implements Serializable, Vector<Vector3> {
	private static final long serialVersionUID = 3840054589595372522L;

	/** 向量的 x 分量 **/
	public float x;
	/** 向量的 y 分量 **/
	public float y;
	/** 向量的 z 分量 **/
	public float z;

	/** X 轴单位向量 (1, 0, 0) */
	public final static Vector3 X = new Vector3(1, 0, 0);
	/** Y 轴单位向量 (0, 1, 0) */
	public final static Vector3 Y = new Vector3(0, 1, 0);
	/** Z 轴单位向量 (0, 0, 1) */
	public final static Vector3 Z = new Vector3(0, 0, 1);
	/** 零向量 (0, 0, 0) */
	public final static Vector3 Zero = new Vector3(0, 0, 0);
	/** 所有分量为1的向量 (1, 1, 1) */
	public final static Vector3 One = new Vector3(1, 1, 1);

	private final static Matrix4 tmpMat = new Matrix4();

	/** 构造一个位于 (0,0,0) 的向量 */
	public Vector3 () {
	}

	/** 使用指定分量构造向量
	 * @param x x 分量
	 * @param y y 分量
	 * @param z z 分量 */
	public Vector3 (float x, float y, float z) {
		this.set(x, y, z);
	}

	/** 从给定向量复制构造
	 * @param vector 源向量 */
	public Vector3 (final Vector3 vector) {
		this.set(vector);
	}

	/** 从数组中构造向量，数组必须至少有3个元素。
	 *
	 * @param values 数组 */
	public Vector3 (final float[] values) {
		this.set(values[0], values[1], values[2]);
	}

	/** 从给定的 Vector2 和 z 分量构造向量
	 *
	 * @param vector Vector2
	 * @param z z 分量 */
	public Vector3 (final Vector2 vector, float z) {
		this.set(vector.x, vector.y, z);
	}

	/** 设置向量的分量
	 *
	 * @param x x 分量
	 * @param y y 分量
	 * @param z z 分量
	 * @return 当前向量（链式调用） */
	public Vector3 set (float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	@Override
	public Vector3 set (final Vector3 vector) {
		return this.set(vector.x, vector.y, vector.z);
	}

	/** 从数组设置分量，数组必须至少有3个元素
	 *
	 * @param values 数组
	 * @return 当前向量（链式调用） */
	public Vector3 set (final float[] values) {
		return this.set(values[0], values[1], values[2]);
	}

	/** 从 Vector2 和 z 分量设置
	 *
	 * @param vector Vector2
	 * @param z z 分量
	 * @return 当前向量（链式调用） */
	public Vector3 set (final Vector2 vector, float z) {
		return this.set(vector.x, vector.y, z);
	}

	/** 从球面坐标设置向量分量
	 * @param azimuthalAngle 方位角（x轴夹角，弧度），范围 [0, 2pi]
	 * @param polarAngle 极角（z轴夹角，弧度），范围 [0, pi]
	 * @return 当前向量（链式调用） */
	public Vector3 setFromSpherical (float azimuthalAngle, float polarAngle) {
		float cosPolar = MathUtils.cos(polarAngle);
		float sinPolar = MathUtils.sin(polarAngle);

		float cosAzim = MathUtils.cos(azimuthalAngle);
		float sinAzim = MathUtils.sin(azimuthalAngle);

		return this.set(cosAzim * sinPolar, sinAzim * sinPolar, cosPolar);
	}

	@Override
	public Vector3 setToRandomDirection () {
		float u = MathUtils.random();
		float v = MathUtils.random();

		float theta = MathUtils.PI2 * u; // azimuthal angle
		float phi = (float)Math.acos(2f * v - 1f); // polar angle

		return this.setFromSpherical(theta, phi);
	}

	@Override
	public Vector3 cpy () {
		return new Vector3(this);
	}

	@Override
	public Vector3 add (final Vector3 vector) {
		return this.add(vector.x, vector.y, vector.z);
	}

	/** 将给定的分量添加到此向量
	 * @param x 另一个向量的 x 分量
	 * @param y 另一个向量的 y 分量
	 * @param z 另一个向量的 z 分量
	 * @return 当前向量（链式调用） */
	public Vector3 add (float x, float y, float z) {
		return this.set(this.x + x, this.y + y, this.z + z);
	}

	/** 将给定的标量值添加到所有三个分量
	 *
	 * @param values 值
	 * @return 当前向量（链式调用） */
	public Vector3 add (float values) {
		return this.set(this.x + values, this.y + values, this.z + values);
	}

	@Override
	public Vector3 sub (final Vector3 a_vec) {
		return this.sub(a_vec.x, a_vec.y, a_vec.z);
	}

	/** 从此向量中减去另一个向量。
	 *
	 * @param x 另一个向量的 x 分量
	 * @param y 另一个向量的 y 分量
	 * @param z 另一个向量的 z 分量
	 * @return 当前向量（链式调用） */
	public Vector3 sub (float x, float y, float z) {
		return this.set(this.x - x, this.y - y, this.z - z);
	}

	/** 从所有分量中减去给定值
	 *
	 * @param value 值
	 * @return 当前向量（链式调用） */
	public Vector3 sub (float value) {
		return this.set(this.x - value, this.y - value, this.z - value);
	}

	@Override
	public Vector3 scl (float scalar) {
		return this.set(this.x * scalar, this.y * scalar, this.z * scalar);
	}

	@Override
	public Vector3 scl (final Vector3 other) {
		return this.set(x * other.x, y * other.y, z * other.z);
	}

	/** 分别缩放向量的每个分量
	 * @param vx X 值
	 * @param vy Y 值
	 * @param vz Z 值
	 * @return 当前向量（链式调用） */
	public Vector3 scl (float vx, float vy, float vz) {
		return this.set(this.x * vx, this.y * vy, this.z * vz);
	}

	@Override
	public Vector3 mulAdd (Vector3 vec, float scalar) {
		this.x += vec.x * scalar;
		this.y += vec.y * scalar;
		this.z += vec.z * scalar;
		return this;
	}

	@Override
	public Vector3 mulAdd (Vector3 vec, Vector3 mulVec) {
		this.x += vec.x * mulVec.x;
		this.y += vec.y * mulVec.y;
		this.z += vec.z * mulVec.z;
		return this;
	}

	/** @return 欧几里得长度 */
	public static float len (final float x, final float y, final float z) {
		return (float)Math.sqrt(x * x + y * y + z * z);
	}

	@Override
	public float len () {
		return (float)Math.sqrt(x * x + y * y + z * z);
	}

	/** @return 欧几里得长度的平方 */
	public static float len2 (final float x, final float y, final float z) {
		return x * x + y * y + z * z;
	}

	@Override
	public float len2 () {
		return x * x + y * y + z * z;
	}

	/** 如与此向量和参数向量的分量完全相同，返回 true。
	 * @param vector 另一个向量
	 * @return 是否精确相等 */
	public boolean idt (final Vector3 vector) {
		return x == vector.x && y == vector.y && z == vector.z;
	}

	/** @return 两个指定向量之间的欧几里得距离 */
	public static float dst (final float x1, final float y1, final float z1, final float x2, final float y2, final float z2) {
		final float a = x2 - x1;
		final float b = y2 - y1;
		final float c = z2 - z1;
		return (float)Math.sqrt(a * a + b * b + c * c);
	}

	@Override
	public float dst (final Vector3 vector) {
		final float a = vector.x - x;
		final float b = vector.y - y;
		final float c = vector.z - z;
		return (float)Math.sqrt(a * a + b * b + c * c);
	}

	/** @return 此点与给定点之间的距离 */
	public float dst (float x, float y, float z) {
		final float a = x - this.x;
		final float b = y - this.y;
		final float c = z - this.z;
		return (float)Math.sqrt(a * a + b * b + c * c);
	}

	/** @return 给定点之间的平方距离 */
	public static float dst2 (final float x1, final float y1, final float z1, final float x2, final float y2, final float z2) {
		final float a = x2 - x1;
		final float b = y2 - y1;
		final float c = z2 - z1;
		return a * a + b * b + c * c;
	}

	@Override
	public float dst2 (Vector3 point) {
		final float a = point.x - x;
		final float b = point.y - y;
		final float c = point.z - z;
		return a * a + b * b + c * c;
	}

	/** 返回此点与给定点之间的平方距离
	 * @param x 另一个点的 x 分量
	 * @param y 另一个点的 y 分量
	 * @param z 另一个点的 z 分量
	 * @return 平方距离 */
	public float dst2 (float x, float y, float z) {
		final float a = x - this.x;
		final float b = y - this.y;
		final float c = z - this.z;
		return a * a + b * b + c * c;
	}

	@Override
	public Vector3 nor () {
		final float len2 = this.len2();
		if (len2 == 0f || len2 == 1f) return this;
		return this.scl(1f / (float)Math.sqrt(len2));
	}

	/** @return 两个向量的点积 */
	public static float dot (float x1, float y1, float z1, float x2, float y2, float z2) {
		return x1 * x2 + y1 * y2 + z1 * z2;
	}

	@Override
	public float dot (final Vector3 vector) {
		return x * vector.x + y * vector.y + z * vector.z;
	}

	/** 返回此向量与给定向量的点积。
	 * @param x 另一个向量的 x 分量
	 * @param y 另一个向量的 y 分量
	 * @param z 另一个向量的 z 分量
	 * @return 点积 */
	public float dot (float x, float y, float z) {
		return this.x * x + this.y * y + this.z * z;
	}

	/** 将此向量设为此向量与另一个向量的叉积。
	 * @param vector 另一个向量
	 * @return 当前向量（链式调用） */
	public Vector3 crs (final Vector3 vector) {
		return this.set(y * vector.z - z * vector.y, z * vector.x - x * vector.z, x * vector.y - y * vector.x);
	}

	/** 将此向量设为此向量与给定向量的叉积。
	 * @param x 另一个向量的 x 分量
	 * @param y 另一个向量的 y 分量
	 * @param z 另一个向量的 z 分量
	 * @return 当前向量（链式调用） */
	public Vector3 crs (float x, float y, float z) {
		return this.set(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
	}

	/** 左乘给定的 4x3 列主序矩阵。矩阵应由代表旋转和缩放的 3x3 矩阵以及代表平移的 1x3 矩阵组成。
	 * @param matrix 矩阵
	 * @return 当前向量（链式调用） */
	public Vector3 mul4x3 (float[] matrix) {
		return set(x * matrix[0] + y * matrix[3] + z * matrix[6] + matrix[9],
			x * matrix[1] + y * matrix[4] + z * matrix[7] + matrix[10], x * matrix[2] + y * matrix[5] + z * matrix[8] + matrix[11]);
	}

	/** 左乘给定的 4x4 矩阵，假设向量的第4个分量（w）为1。
	 * @param matrix 矩阵
	 * @return 当前向量（链式调用） */
	public Vector3 mul (final Matrix4 matrix) {
		final float[] l_mat = matrix.val;
		return this.set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M01] + z * l_mat[Matrix4.M02] + l_mat[Matrix4.M03],
			x * l_mat[Matrix4.M10] + y * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M12] + l_mat[Matrix4.M13],
			x * l_mat[Matrix4.M20] + y * l_mat[Matrix4.M21] + z * l_mat[Matrix4.M22] + l_mat[Matrix4.M23]);
	}

	/** 左乘给定矩阵的转置，假设向量的第4个分量（w）为1。
	 * @param matrix 矩阵
	 * @return 当前向量（链式调用） */
	public Vector3 traMul (final Matrix4 matrix) {
		final float[] l_mat = matrix.val;
		return this.set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M10] + z * l_mat[Matrix4.M20] + l_mat[Matrix4.M30],
			x * l_mat[Matrix4.M01] + y * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M21] + l_mat[Matrix4.M31],
			x * l_mat[Matrix4.M02] + y * l_mat[Matrix4.M12] + z * l_mat[Matrix4.M22] + l_mat[Matrix4.M32]);
	}

	/** 左乘给定的 3x3 矩阵。
	 * @param matrix 矩阵
	 * @return 当前向量（链式调用） */
	public Vector3 mul (Matrix3 matrix) {
		final float[] l_mat = matrix.val;
		return set(x * l_mat[Matrix3.M00] + y * l_mat[Matrix3.M01] + z * l_mat[Matrix3.M02],
			x * l_mat[Matrix3.M10] + y * l_mat[Matrix3.M11] + z * l_mat[Matrix3.M12],
			x * l_mat[Matrix3.M20] + y * l_mat[Matrix3.M21] + z * l_mat[Matrix3.M22]);
	}

	/** 左乘给定矩阵的转置。
	 * @param matrix 矩阵
	 * @return 当前向量（链式调用） */
	public Vector3 traMul (Matrix3 matrix) {
		final float[] l_mat = matrix.val;
		return set(x * l_mat[Matrix3.M00] + y * l_mat[Matrix3.M10] + z * l_mat[Matrix3.M20],
			x * l_mat[Matrix3.M01] + y * l_mat[Matrix3.M11] + z * l_mat[Matrix3.M21],
			x * l_mat[Matrix3.M02] + y * l_mat[Matrix3.M12] + z * l_mat[Matrix3.M22]);
	}

	/** 左乘给定的 {@link Quaternion}（四元数）。
	 * @return 当前向量（链式调用） */
	public Vector3 mul (final Quaternion quat) {
		return quat.transform(this);
	}

	/** 左乘给定矩阵并除以 w，假设向量的第4个分量（w）为1。
	 * 主要用于通过透视投影矩阵进行投影/反投影。
	 *
	 * @param matrix 矩阵
	 * @return 当前向量（链式调用） */
	public Vector3 prj (final Matrix4 matrix) {
		final float[] l_mat = matrix.val;
		final float l_w = 1f / (x * l_mat[Matrix4.M30] + y * l_mat[Matrix4.M31] + z * l_mat[Matrix4.M32] + l_mat[Matrix4.M33]);
		return this.set((x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M01] + z * l_mat[Matrix4.M02] + l_mat[Matrix4.M03]) * l_w,
			(x * l_mat[Matrix4.M10] + y * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M12] + l_mat[Matrix4.M13]) * l_w,
			(x * l_mat[Matrix4.M20] + y * l_mat[Matrix4.M21] + z * l_mat[Matrix4.M22] + l_mat[Matrix4.M23]) * l_w);
	}

	/** 仅应用矩阵的前三列（旋转和缩放），不包含平移。
	 *
	 * @param matrix 矩阵
	 * @return 当前向量（链式调用） */
	public Vector3 rot (final Matrix4 matrix) {
		final float[] l_mat = matrix.val;
		return this.set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M01] + z * l_mat[Matrix4.M02],
			x * l_mat[Matrix4.M10] + y * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M12],
			x * l_mat[Matrix4.M20] + y * l_mat[Matrix4.M21] + z * l_mat[Matrix4.M22]);
	}

	/** 左乘矩阵前三列的转置（反向旋转）。仅适用于平移和旋转，不适用于缩放。
	 * @param matrix 变换矩阵
	 * @return 当前向量（链式调用） */
	public Vector3 unrotate (final Matrix4 matrix) {
		final float[] l_mat = matrix.val;
		return this.set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M10] + z * l_mat[Matrix4.M20],
			x * l_mat[Matrix4.M01] + y * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M21],
			x * l_mat[Matrix4.M02] + y * l_mat[Matrix4.M12] + z * l_mat[Matrix4.M22]);
	}

	/** 先减去矩阵的平移，再乘以矩阵前三列的转置（反向变换）。仅适用于平移和旋转。
	 * @param matrix 变换矩阵
	 * @return 当前向量（链式调用） */
	public Vector3 untransform (final Matrix4 matrix) {
		final float[] l_mat = matrix.val;
		x -= l_mat[Matrix4.M03];
		y -= l_mat[Matrix4.M03];
		z -= l_mat[Matrix4.M03];
		return this.set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M10] + z * l_mat[Matrix4.M20],
			x * l_mat[Matrix4.M01] + y * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M21],
			x * l_mat[Matrix4.M02] + y * l_mat[Matrix4.M12] + z * l_mat[Matrix4.M22]);
	}

	/** 绕指定轴旋转此向量（角度制）。
	 *
	 * @param degrees 角度（度）
	 * @param axisX 轴 x 分量
	 * @param axisY 轴 y 分量
	 * @param axisZ 轴 z 分量
	 * @return 当前向量（链式调用） */
	public Vector3 rotate (float degrees, float axisX, float axisY, float axisZ) {
		return this.mul(tmpMat.setToRotation(axisX, axisY, axisZ, degrees));
	}

	/** 绕指定轴旋转此向量（弧度制）。
	 *
	 * @param radians 角度（弧度）
	 * @param axisX 轴 x 分量
	 * @param axisY 轴 y 分量
	 * @param axisZ 轴 z 分量
	 * @return 当前向量（链式调用） */
	public Vector3 rotateRad (float radians, float axisX, float axisY, float axisZ) {
		return this.mul(tmpMat.setToRotationRad(axisX, axisY, axisZ, radians));
	}

	/** 绕指定轴旋转此向量（角度制）。
	 *
	 * @param axis 旋转轴
	 * @param degrees 角度（度）
	 * @return 当前向量（链式调用） */
	public Vector3 rotate (final Vector3 axis, float degrees) {
		tmpMat.setToRotation(axis, degrees);
		return this.mul(tmpMat);
	}

	/** 绕指定轴旋转此向量（弧度制）。
	 *
	 * @param axis 旋转轴
	 * @param radians 角度（弧度）
	 * @return 当前向量（链式调用） */
	public Vector3 rotateRad (final Vector3 axis, float radians) {
		tmpMat.setToRotationRad(axis, radians);
		return this.mul(tmpMat);
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
		return x == 0 && y == 0 && z == 0;
	}

	@Override
	public boolean isZero (final float margin) {
		return len2() < margin;
	}

	@Override
	public boolean isOnLine (Vector3 other, float epsilon) {
		return len2(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x) <= epsilon;
	}

	@Override
	public boolean isOnLine (Vector3 other) {
		return len2(y * other.z - z * other.y, z * other.x - x * other.z,
			x * other.y - y * other.x) <= MathUtils.FLOAT_ROUNDING_ERROR;
	}

	@Override
	public boolean isCollinear (Vector3 other, float epsilon) {
		return isOnLine(other, epsilon) && hasSameDirection(other);
	}

	@Override
	public boolean isCollinear (Vector3 other) {
		return isOnLine(other) && hasSameDirection(other);
	}

	@Override
	public boolean isCollinearOpposite (Vector3 other, float epsilon) {
		return isOnLine(other, epsilon) && hasOppositeDirection(other);
	}

	@Override
	public boolean isCollinearOpposite (Vector3 other) {
		return isOnLine(other) && hasOppositeDirection(other);
	}

	@Override
	public boolean isPerpendicular (Vector3 vector) {
		return MathUtils.isZero(dot(vector));
	}

	@Override
	public boolean isPerpendicular (Vector3 vector, float epsilon) {
		return MathUtils.isZero(dot(vector), epsilon);
	}

	@Override
	public boolean hasSameDirection (Vector3 vector) {
		return dot(vector) > 0;
	}

	@Override
	public boolean hasOppositeDirection (Vector3 vector) {
		return dot(vector) < 0;
	}

	@Override
	public Vector3 lerp (final Vector3 target, float alpha) {
		x += alpha * (target.x - x);
		y += alpha * (target.y - y);
		z += alpha * (target.z - z);
		return this;
	}

	@Override
	public Vector3 interpolate (Vector3 target, float alpha, Interpolation interpolator) {
		return lerp(target, interpolator.apply(0f, 1f, alpha));
	}

	/** 球面线性插值。在 alpha [0,1] 范围内，在此向量和目标向量之间进行插值。
	 * 结果存储在此向量中。
	 *
	 * @param target 目标向量
	 * @param alpha 插值系数 [0,1]
	 * @return 当前向量（链式调用） */
	public Vector3 slerp (final Vector3 target, float alpha) {
		final float dot = dot(target);
		// If the inputs are too close for comfort, simply linearly interpolate.
		if (dot > 0.9995 || dot < -0.9995) return lerp(target, alpha);

		// theta0 = angle between input vectors
		final float theta0 = (float)Math.acos(dot);
		// theta = angle between this vector and result
		final float theta = theta0 * alpha;

		final float st = (float)Math.sin(theta);
		final float tx = target.x - x * dot;
		final float ty = target.y - y * dot;
		final float tz = target.z - z * dot;
		final float l2 = tx * tx + ty * ty + tz * tz;
		final float dl = st * ((l2 < 0.0001f) ? 1f : 1f / (float)Math.sqrt(l2));

		return scl((float)Math.cos(theta)).add(tx * dl, ty * dl, tz * dl).nor();
	}

	/** 将此 {@code Vector3} 转换为字符串，格式为 {@code (x,y,z)}。
	 * @return 字符串表示 */
	@Override
	public String toString () {
		return "(" + x + "," + y + "," + z + ")";
	}

	/** 将字符串（格式为 {@link #toString()}）解析为此向量的值。
	 * @param v 字符串
	 * @return 当前向量（链式调用） */
	public Vector3 fromString (String v) {
		int s0 = v.indexOf(',', 1);
		int s1 = v.indexOf(',', s0 + 1);
		if (s0 != -1 && s1 != -1 && v.charAt(0) == '(' && v.charAt(v.length() - 1) == ')') {
			try {
				float x = Float.parseFloat(v.substring(1, s0));
				float y = Float.parseFloat(v.substring(s0 + 1, s1));
				float z = Float.parseFloat(v.substring(s1 + 1, v.length() - 1));
				return this.set(x, y, z);
			} catch (NumberFormatException ex) {
				// Throw a GdxRuntimeException
			}
		}
		throw new GdxRuntimeException("Malformed Vector3: " + v);
	}

	@Override
	public Vector3 limit (float limit) {
		return limit2(limit * limit);
	}

	@Override
	public Vector3 limit2 (float limit2) {
		float len2 = len2();
		if (len2 > limit2) {
			scl((float)Math.sqrt(limit2 / len2));
		}
		return this;
	}

	@Override
	public Vector3 setLength (float len) {
		return setLength2(len * len);
	}

	@Override
	public Vector3 setLength2 (float len2) {
		float oldLen2 = len2();
		return (oldLen2 == 0 || oldLen2 == len2) ? this : scl((float)Math.sqrt(len2 / oldLen2));
	}

	@Override
	public Vector3 clamp (float min, float max) {
		final float len2 = len2();
		if (len2 == 0f) return this;
		float max2 = max * max;
		if (len2 > max2) return scl((float)Math.sqrt(max2 / len2));
		float min2 = min * min;
		if (len2 < min2) return scl((float)Math.sqrt(min2 / len2));
		return this;
	}

	@Override
	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + NumberUtils.floatToIntBits(x);
		result = prime * result + NumberUtils.floatToIntBits(y);
		result = prime * result + NumberUtils.floatToIntBits(z);
		return result;
	}

	@Override
	public boolean equals (Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Vector3 other = (Vector3)obj;
		if (NumberUtils.floatToIntBits(x) != NumberUtils.floatToIntBits(other.x)) return false;
		if (NumberUtils.floatToIntBits(y) != NumberUtils.floatToIntBits(other.y)) return false;
		if (NumberUtils.floatToIntBits(z) != NumberUtils.floatToIntBits(other.z)) return false;
		return true;
	}

	@Override
	public boolean epsilonEquals (final Vector3 other, float epsilon) {
		if (other == null) return false;
		if (Math.abs(other.x - x) > epsilon) return false;
		if (Math.abs(other.y - y) > epsilon) return false;
		if (Math.abs(other.z - z) > epsilon) return false;
		return true;
	}

	/** 使用指定 epsilon 进行模糊相等性比较。
	 * @return 是否相同 */
	public boolean epsilonEquals (float x, float y, float z, float epsilon) {
		if (Math.abs(x - this.x) > epsilon) return false;
		if (Math.abs(y - this.y) > epsilon) return false;
		if (Math.abs(z - this.z) > epsilon) return false;
		return true;
	}

	/** 使用 MathUtils.FLOAT_ROUNDING_ERROR 进行模糊相等性比较
	 *
	 * @param other 另一个向量
	 * @return 是否相等 */
	public boolean epsilonEquals (final Vector3 other) {
		return epsilonEquals(other, MathUtils.FLOAT_ROUNDING_ERROR);
	}

	/** 使用 MathUtils.FLOAT_ROUNDING_ERROR 进行模糊相等性比较
	 *
	 * @param x 另一个向量的 x 分量
	 * @param y 另一个向量的 y 分量
	 * @param z 另一个向量的 z 分量
	 * @return 是否相等 */
	public boolean epsilonEquals (float x, float y, float z) {
		return epsilonEquals(x, y, z, MathUtils.FLOAT_ROUNDING_ERROR);
	}

	@Override
	public Vector3 setZero () {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		return this;
	}
}
