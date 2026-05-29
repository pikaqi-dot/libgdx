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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.net.HttpRequestHeader;
import com.badlogic.gdx.net.HttpResponseHeader;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.Pool.Poolable;

/** <b>网络操作接口。</b>
 * 提供执行网络操作的方法，包括简单的 HTTP GET/POST 请求、TCP 服务器/客户端 Socket 通信。
 * </p>
 * 
 * <b>HTTP 请求用法：</b><br>
 * 创建包含 HTTP 方法（参见 {@link HttpMethods}）的 {@link HttpRequest}，
 * 然后调用 {@link #sendHttpRequest(HttpRequest, HttpResponseListener)} 并传入 {@link HttpResponseListener}。
 * HTTP 请求处理完成后，会调用 HttpResponseListener 返回 {@link HttpResponse} 和状态码，以判断请求是否成功。
 * </p>
 * 
 * <b>TCP 客户端用法：</b><br>
 * 调用 {@link #newClientSocket(Protocol, String, int, SocketHints)} 创建 TCP 客户端 Socket 与远程服务器通信。
 * 返回的 {@link Socket} 提供 {@link InputStream} 和 {@link OutputStream} 用于通信。
 * </p>
 * 
 * <b>TCP 服务器用法：</b><br>
 * 调用 {@link #newServerSocket(Protocol, int, ServerSocketHints)} 创建 TCP 服务器 Socket 等待连接。
 * 返回的 {@link ServerSocket} 通过 {@link ServerSocket#accept(SocketHints)} 等待入站连接。
 * 
 * @author mzechner
 * @author noblemaster
 * @author arielsan */
public interface Net {

	/** HTTP 响应接口，提供获取响应数据的方法（byte[]、String 或 InputStream）。 */
	public static interface HttpResponse {
		/** 返回 HTTP 响应的数据（byte[]）。
		 * <p>
		 * <b>注意</b>：每个响应仅能调用此方法一次。
		 * </p>
		 * @return byte[] 格式的结果，超时或异常取消时返回 null */
		byte[] getResult ();

		/** 返回 HTTP 响应的数据（{@link String}）。
		 * <p>
		 * <b>注意</b>：每个响应仅能调用此方法一次。
		 * </p>
		 * @return String 格式的结果，超时或异常取消时返回 null */
		String getResultAsString ();

		/** 返回 HTTP 响应的数据（{@link InputStream}）。
		 * <br><b>警告：</b>不要在 {@link HttpResponseListener#handleHttpResponse(HttpResponse)} 之外持有此 InputStream 引用。
		 * 底层 HTTP 连接会在回调执行完毕后关闭。在连接关闭后读取 InputStream 将导致异常。
		 * @return 包含 HTTP 响应数据的 InputStream */
		InputStream getResultAsStream ();

		/** 返回包含 HTTP 响应状态码的 {@link HttpStatus}。 */
		HttpStatus getStatus ();

		/** 返回指定名称的响应头值（{@link String}），如果未设置则返回 null。参见 {@link HttpResponseHeader}。 */
		String getHeader (String name);

		/** 返回响应头的 Map。键为头名称（String），值为对应的头值列表（List<String>）。参见 {@link HttpResponseHeader}。 */
		Map<String, List<String>> getHeaders ();
	}

	/** 提供创建 {@link HttpRequest} 时使用的常见 HTTP 方法。
	 * <ul>
	 * <li><b>HEAD</b> 请求与 GET 相同的响应，但不包含响应体。</li>
	 * <li><b>GET</b> 请求指定资源的表示。使用 GET 的请求应只检索数据。</li>
	 * <li><b>POST</b> 用于向指定资源提交实体，通常会导致服务器状态变化或副作用。</li>
	 * <li><b>PUT</b> 用请求负载替换目标资源的所有当前表示。</li>
	 * <li><b>PATCH</b> 用于对资源进行部分修改。</li>
	 * <li><b>DELETE</b> 删除指定资源。</li>
	 * </ul>
	 */
	public static interface HttpMethods {
		/** HEAD 方法请求与 GET 相同的响应，但不带响应体。 **/
		public static final String HEAD = "HEAD";

		/** GET 方法请求指定资源的表示。使用 GET 的请求应只检索数据。 **/
		public static final String GET = "GET";

		/** POST 方法用于向指定资源提交实体，通常会导致服务器状态变化或副作用。 **/
		public static final String POST = "POST";

		/** PUT 方法用请求负载替换目标资源的所有当前表示。 **/
		public static final String PUT = "PUT";

		/** PATCH 方法用于对资源进行部分修改。 **/
		public static final String PATCH = "PATCH";

		/** DELETE 方法删除指定资源。 **/
		public static final String DELETE = "DELETE";
	}

	/** HTTP 请求类，封装了以下参数：
	 * <ul>
	 * <li><strong>httpMethod:</strong> HTTP 方法（GET 或 POST 最常见），可使用 {@link Net.HttpMethods HttpMethods} 常量</li>
	 * <li><strong>url:</strong> 请求 URL</li>
	 * <li><strong>headers:</strong> 请求头 Map</li>
	 * <li><strong>timeout:</strong> 连接超时时间</li>
	 * <li><strong>content:</strong> 请求内容的字符串数据</li>
	 * </ul>
	 * 
	 * <pre>
	 * HttpRequest httpGet = new HttpRequest(HttpMethods.GET);
	 * httpGet.setUrl("http://somewhere.net");
	 * httpGet.setContent(HttpParametersUtils.convertHttpParameters(parameters));
	 * Gdx.net.sendHttpRequest(httpGet, new HttpResponseListener() {
	 *     public void handleHttpResponse(HttpResponse httpResponse) {
	 *         String result = httpResponse.getResultAsString();
	 *     }
	 *     public void failed(Throwable t) {
	 *         // 处理失败
	 *     }
	 * });
	 * </pre>
	 */
	public static class HttpRequest implements Poolable {

		private String httpMethod;
		private String url;
		private Map<String, String> headers;
		private int timeOut = 0;

		private String content;
		private InputStream contentStream;
		private long contentLength;

		private boolean followRedirects = true;

		private boolean includeCredentials = false;

		public HttpRequest () {
			this.headers = new HashMap<String, String>();
		}

		/** 使用指定的 HTTP 方法创建新的 HTTP 请求。
		 * @param httpMethod HTTP 方法，参见 {@link HttpMethods} */
		public HttpRequest (String httpMethod) {
			this();
			this.httpMethod = httpMethod;
		}

		/** 设置 HTTP 请求的 URL。
		 * @param url URL */
		public void setUrl (String url) {
			this.url = url;
		}

		/** 设置 HTTP 请求头，参见 {@link HttpRequestHeader}。
		 * @param name 头名称
		 * @param value 头值 */
		public void setHeader (String name, String value) {
			headers.put(name, value);
		}

		/** 设置 HTTP 请求的内容（body）。
		 * @param content 编码后的字符串内容，HTTP GET 时用作查询字符串，HTTP POST 时用作 POST 数据 */
		public void setContent (String content) {
			this.content = content;
		}

		/** 设置内容为输入流，用于传输自定义数据（如 POST 上传）。
		 * @param contentStream 内容数据流 */
		public void setContent (InputStream contentStream, long contentLength) {
			this.contentStream = contentStream;
			this.contentLength = contentLength;
		}

		/** 设置 HTTP 请求的超时时间，0 表示阻塞直到完成。
		 * 超时同时用于 TCP 连接建立和接收第一个字节数据。
		 * @param timeOut 超时毫秒数，0 或负数表示一直等待 */
		public void setTimeOut (int timeOut) {
			this.timeOut = timeOut;
		}

		/** 设置是否跟随 301 和 302 重定向。默认为 true。
		 * GWT 后端无法更改，因为 XmlHttpRequest 总是重定向。
		 * @param followRedirects 是否跟随重定向
		 * @exception IllegalArgumentException 如果在 GWT 后端禁用了重定向 */
		public void setFollowRedirects (boolean followRedirects) throws IllegalArgumentException {
			if (followRedirects || Gdx.app.getType() != ApplicationType.WebGL) {
				this.followRedirects = followRedirects;
			} else {
				throw new IllegalArgumentException("Following redirects can't be disabled using the GWT/WebGL backend!");
			}
		}

		/** 设置跨域请求是否包含凭据（cookie、认证头等）。仅在 GWT 后端使用。 */
		public void setIncludeCredentials (boolean includeCredentials) {
			this.includeCredentials = includeCredentials;
		}

		/** 设置 HTTP 请求的方法。 */
		public void setMethod (String httpMethod) {
			this.httpMethod = httpMethod;
		}

		/** @return HTTP 请求的超时时间 */
		public int getTimeOut () {
			return timeOut;
		}

		/** @return HTTP 请求的方法 */
		public String getMethod () {
			return httpMethod;
		}

		/** @return HTTP 请求的 URL */
		public String getUrl () {
			return url;
		}

		/** @return HTTP 请求的内容字符串 */
		public String getContent () {
			return content;
		}

		/** @return 内容输入流 */
		public InputStream getContentStream () {
			return contentStream;
		}

		/** @return 内容长度（当内容为流时） */
		public long getContentLength () {
			return contentLength;
		}

		/** @return HTTP 请求头的 Map */
		public Map<String, String> getHeaders () {
			return headers;
		}

		/** @return 是否跟随 301/302 重定向 */
		public boolean getFollowRedirects () {
			return followRedirects;
		}

		/** @return 跨域请求是否包含凭据 */
		public boolean getIncludeCredentials () {
			return includeCredentials;
		}

		@Override
		public void reset () {
			httpMethod = null;
			url = null;
			headers.clear();
			timeOut = 0;

			content = null;
			contentStream = null;
			contentLength = 0;

			followRedirects = true;
		}

	}

	/** HTTP 响应监听器，注册后用于在 {@link HttpResponse} 准备好时执行自定义逻辑。
	 * 通过 {@link Net#sendHttpRequest(HttpRequest, HttpResponseListener)} 注册。 */
	public static interface HttpResponseListener {

		/** 当 {@link HttpRequest} 已处理完毕且 {@link HttpResponse} 已就绪时调用。
		 * 向渲染线程传递数据应使用 {@link Application#postRunnable(Runnable)}。
		 * 
		 * @param httpResponse 包含 HTTP 响应值的 {@link HttpResponse} */
		void handleHttpResponse (HttpResponse httpResponse);

		/** 当 {@link HttpRequest} 因异常而失败时调用（可能是超时或其他原因，非 HTTP 错误）。
		 * @param t 封装失败异常的 Throwable */
		void failed (Throwable t);

		void cancelled ();
	}

	/** 发送指定的 {@link HttpRequest} 并将 {@link HttpResponse} 报告给指定的 {@link HttpResponseListener}。
	 * @param httpRequest 要发送的 HTTP 请求
	 * @param httpResponseListener HTTP 响应监听器，可为 null */
	public void sendHttpRequest (HttpRequest httpRequest, @Null HttpResponseListener httpResponseListener);

	public void cancelHttpRequest (HttpRequest httpRequest);

	public boolean isHttpRequestPending (HttpRequest httpRequest);

	/** 协议枚举，用于 {@link Net#newServerSocket(Protocol, int, ServerSocketHints)} 和
	 * {@link Net#newClientSocket(Protocol, String, int, SocketHints)}。
	 * @author mzechner */
	public enum Protocol {
		TCP
	}

	/** 在指定地址和端口上创建新的服务器 Socket，等待入站连接。
	 * 
	 * @param hostname 绑定的主机名或 IP 地址
	 * @param port 监听的端口
	 * @param hints 额外的 {@link ServerSocketHints}，可为 null 使用系统默认设置
	 * @return {@link ServerSocket}
	 * @throws GdxRuntimeException 如果无法打开 Socket */
	public ServerSocket newServerSocket (Protocol protocol, String hostname, int port, ServerSocketHints hints);

	/** 在指定端口上创建新的服务器 Socket，等待入站连接。
	 * 
	 * @param port 监听的端口
	 * @param hints 额外的 {@link ServerSocketHints}，可为 null 使用系统默认设置
	 * @return {@link ServerSocket}
	 * @throws GdxRuntimeException 如果无法打开 Socket */
	public ServerSocket newServerSocket (Protocol protocol, int port, ServerSocketHints hints);

	/** 创建新的 TCP 客户端 Socket，连接到指定的主机和端口。
	 * 
	 * @param host 主机地址
	 * @param port 端口
	 * @param hints 额外的 {@link SocketHints}，可为 null 使用系统默认设置
	 * @throws GdxRuntimeException 如果无法打开 Socket */
	public Socket newClientSocket (Protocol protocol, String host, int port, SocketHints hints);

	/** 启动默认浏览器打开 URI。如果默认浏览器无法处理指定的 URI，则会调用注册处理该 URI 类型的应用程序。
	 * 尽量尝试打开 URI，但由于涉及外部应用程序，无法保证 URI 是否真的被打开。
	 * 
	 * @param URI 要打开的 URI
	 * @return 如果已知 URI 未被打开则返回 false，否则返回 true */
	public boolean openURI (String URI);
}
