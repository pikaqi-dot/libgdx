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

package com.badlogic.gdx;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

/** <b>文件系统访问接口。</b>
 * 提供对文件系统、classpath、Android 应用存储（内部和外部）以及 Android assets 目录的标准访问。
 * @author mzechner
 * @author Nathan Sweet */
public interface Files {
	/** 枚举：如何解析文件路径。
	 * @author mzechner
	 * @author Nathan Sweet */
	public enum FileType {
		/** 相对于 classpath 根目录的路径。Classpath 文件始终为只读。
		 * 注意：classpath 文件与 Android 上的一些功能不兼容，例如
		 * {@link Audio#newSound(FileHandle)} 和 {@link Audio#newMusic(FileHandle)}。 */
		Classpath,

		/** 相对于 Android 上 asset 目录的路径，或桌面上应用根目录的路径。
		 * 在桌面上，如果文件未找到，会检查 classpath。这使得在使用 JWS 或 applet 时也能找到文件。
		 * 内部文件始终为只读。 */
		Internal,

		/** 相对于 Android 上应用外部存储根目录的路径，或桌面上当前用户主目录的路径。 */
		External,

		/** 完全限定的绝对文件系统路径。为保持跨平台可移植性，仅在绝对必要时使用绝对文件。 */
		Absolute,

		/** 相对于 Android 上应用私有文件目录的路径，或桌面上应用根目录的路径。 */
		Local;
	}

	/** 返回代表文件或目录的句柄。
	 * @param type 决定路径的解析方式。
	 * @throws GdxRuntimeException 如果是 classpath 或 internal 类型且文件不存在。
	 * @see FileType */
	public FileHandle getFileHandle (String path, FileType type);

	/** 便捷方法：返回 {@link FileType#Classpath} 类型的文件句柄。 */
	public FileHandle classpath (String path);

	/** 便捷方法：返回 {@link FileType#Internal} 类型的文件句柄。 */
	public FileHandle internal (String path);

	/** 便捷方法：返回 {@link FileType#External} 类型的文件句柄。 */
	public FileHandle external (String path);

	/** 便捷方法：返回 {@link FileType#Absolute} 类型的文件句柄。 */
	public FileHandle absolute (String path);

	/** 便捷方法：返回 {@link FileType#Local} 类型的文件句柄。 */
	public FileHandle local (String path);

	/** 返回外部存储路径。Android 上为应用外部存储，桌面上为当前用户主目录。 */
	public String getExternalStoragePath ();

	/** 返回外部存储是否已准备好进行文件 I/O 操作。 */
	public boolean isExternalStorageAvailable ();

	/** 返回本地存储路径。Android 上为私有文件目录，桌面上为 jar 所在目录。 */
	public String getLocalStoragePath ();

	/** 返回本地存储是否已准备好进行文件 I/O 操作。 */
	public boolean isLocalStorageAvailable ();
}
