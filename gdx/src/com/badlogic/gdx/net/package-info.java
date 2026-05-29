/**
 * <b>网络通信包 — HTTP 请求与 TCP Socket</b>
 * 
 * <h2>架构</h2>
 * 
 * <pre>
 * net/
 * ├── HttpRequest         HTTP请求（方法/URL/头/内容/超时）
 * ├── HttpResponse        HTTP响应（状态码/头/数据）
 * ├── HttpMethods         HTTP方法常量(GET/POST/PUT/DELETE...)
 * ├── HttpRequestHeader   HTTP请求头常量
 * ├── HttpResponseHeader  HTTP响应头常量
 * ├── HttpStatus          HTTP状态码
 * ├── HttpParametersUtils HTTP参数工具
 * ├── HttpRequestBuilder  HTTP请求构建器
 * ├── Socket               客户端 TCP Socket
 * ├── ServerSocket         服务端 TCP Socket
 * ├── SocketHints          客户端 Socket 配置
 * └── ServerSocketHints    服务端 Socket 配置
 * </pre>
 * 
 * <h2>HTTP 请求示例</h2>
 * 
 * <pre>
 * HttpRequest request = new HttpRequest(HttpMethods.GET);
 * request.setUrl("https://api.example.com/data");
 * 
 * Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
 *     public void handleHttpResponse(HttpResponse response) {
 *         String result = response.getResultAsString();
 *     }
 *     public void failed(Throwable t) { }
 * });
 * </pre>
 * 
 * @see com.badlogic.gdx.Net
 * @see com.badlogic.gdx.net.HttpRequestBuilder
 */
package com.badlogic.gdx.net;
