/**
 * <b>数学工具包 — 向量/矩阵/几何/碰撞/曲线</b>
 * 
 * <h2>架构</h2>
 * 
 * <pre>
 * math/
 * ├── 向量            Vector(接口) ← Vector2 / Vector3 / Vector4
 * ├── 矩阵            Matrix3(3x3, 2D变换) / Matrix4(4x4, 3D变换)
 * ├── 旋转             Quaternion(四元数, 避免万向锁)
 * ├── 几何体           Rectangle / Circle / Ellipse / Polygon / Polyline / Plane
 * ├── 数学工具         MathUtils(快速三角函数/随机数/插值)
 * ├── 碰撞检测         Intersector(相交测试) / Frustum(视锥体裁剪)
 * ├── 曲线算法         Bezier / BSpline / CatmullRomSpline / Path(接口)
 * ├── 三角剖分         EarClippingTriangulator(耳切法) / DelaunayTriangulator
 * ├── 凸包             ConvexHull(Andrew's Monotone Chain 算法)
 * ├── 空间索引         Octree(八叉树)
 * └── collision/       BoundingBox(AABB) / OrientedBoundingBox(OBB)
 *                      Ray(射线) / Sphere(球体) / Segment(线段)
 * </pre>
 * 
 * <h2>关键算法</h2>
 * 
 * <ul>
 *   <li><b>凸包</b> — {@link com.badlogic.gdx.math.ConvexHull} 使用 Andrew's Monotone Chain
 *       (O(n log n))：先按 x 排序，分别构建上下凸包</li>
 *   <li><b>耳切法三角剖分</b> — {@link com.badlogic.gdx.math.EarClippingTriangulator}：
 *       重复查找"耳朵"（凸顶点且三角形内不含其他点）并切除</li>
 *   <li><b>贝塞尔曲线</b> — {@link com.badlogic.gdx.math.Bezier} 使用 De Casteljau 递归算法</li>
 *   <li><b>B样条</b> — {@link com.badlogic.gdx.math.BSpline} 使用 Cox-de Boor 递归公式</li>
 *   <li><b>快速三角函数</b> — {@link com.badlogic.gdx.math.MathUtils} 使用 14位查表法</li>
 *   <li><b>浮点比较</b> — {@link com.badlogic.gdx.math.MathUtils#isEqual} 避免 == 精度问题</li>
 * </ul>
 * 
 * <h2>坐标系</h2>
 * 
 * 2D：屏幕坐标系，原点在左上角，Y轴向下。
 * 3D：右手坐标系，X向右，Y向上，Z向屏幕外。
 * 
 * @see com.badlogic.gdx.math.Vector2
 * @see com.badlogic.gdx.math.Vector3
 * @see com.badlogic.gdx.math.Matrix4
 * @see com.badlogic.gdx.math.MathUtils
 */
package com.badlogic.gdx.math;
