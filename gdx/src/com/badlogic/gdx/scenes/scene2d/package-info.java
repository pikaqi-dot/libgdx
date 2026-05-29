/**
 * <b>Scene2D — 2D 场景图 UI 系统</b>
 * 
 * <h2>架构</h2>
 * 
 * <pre>
 * scene2d/
 * ├── Stage           舞台（场景图根节点，管理输入和渲染）
 * ├── Actor           节点基类（位置/大小/缩放/旋转/颜色/事件）
 * ├── Group           Actor容器（可嵌套，支持变换）
 * ├── Event           事件基类
 * ├── EventListener   事件监听器接口
 * ├── InputEvent / InputListener  输入事件/监听器
 * ├── Touchable       触摸响应模式枚举
 * ├── actions/        动作系统
 * │   ├── TemporalAction   随时间变化的动作(基类)
 * │   ├── MoveTo/By、RotateTo/By、ScaleTo/By、SizeTo/By
 * │   ├── AlphaAction、ColorAction、VisibleAction
 * │   ├── Sequence/Parallel/Repeat/Delay 组合控制动作
 * │   └── Actions           动作工厂工具类
 * ├── ui/             UI控件
 * │   ├── 按钮类      Button / TextButton / ImageButton / CheckBox
 * │   ├── 文本类      Label / TextField / TextArea
 * │   ├── 容器类      Table(表格布局) / Container / Stack
 * │   ├── 选择类      List / SelectBox / Tree
 * │   ├── 数值类      Slider / ProgressBar / Touchpad
 * │   ├── 面板类      ScrollPane / SplitPane / Window / Dialog
 * │   └── 工具类      Image / Tooltip / Skin(皮肤)
 * └── utils/          UI工具
 *     ├── ClickListener / DragListener / DragAndDrop
 *     ├── FocusListener / ChangeListener
 *     ├── Drawable / BaseDrawable   可绘制对象
 *     ├── Selection / ArraySelection 选择管理
 *     └── ScissorStack             裁剪栈
 * </pre>
 * 
 * <h2>使用流程</h2>
 * 
 * <pre>
 * // 1. 创建舞台
 * Stage stage = new Stage(viewport);
 * Gdx.input.setInputProcessor(stage);
 * 
 * // 2. 添加 Actor
 * TextButton button = new TextButton("Click", skin);
 * stage.addActor(button);
 * 
 * // 3. 每帧更新和渲染
 * stage.act(delta);   // 更新逻辑和动作
 * stage.draw();       // 渲染场景
 * </pre>
 * 
 * <h2>事件系统</h2>
 * 
 * 事件从 Stage 分发到 Actor 的层次结构：
 * - 触摸事件从父到子传递（捕获阶段）
 * - 然后在目标 Actor 上触发
 * - 最后从子到父冒泡
 * - 通过 {@link com.badlogic.gdx.scenes.scene2d.Event#stop()} 停止传播
 * 
 * <h2>UI 控件体系</h2>
 * 
 * 所有 UI 控件继承自 Actor，使用 Skin 管理样式资源。
 * Table 使用 Cell 进行网格布局，支持跨行跨列。
 * 
 * @see com.badlogic.gdx.scenes.scene2d.Stage
 * @see com.badlogic.gdx.scenes.scene2d.Actor
 * @see com.badlogic.gdx.scenes.scene2d.ui.Table
 * @see com.badlogic.gdx.scenes.scene2d.ui.Skin
 */
package com.badlogic.gdx.scenes.scene2d;
