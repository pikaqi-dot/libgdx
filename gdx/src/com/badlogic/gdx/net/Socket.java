/** <b>客户端 Socket 接口。</b>
* TCP 客户端 Socket，提供输入输出流进行网络通信。
* @author mzechner */

package com.badlogic.gdx.net;

import java.io.InputStream;
import java.io.OutputStream;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.utils.Disposable;

/** A client socket that talks to a server socket via some {@link Protocol}. See
 * {@link Net#newClientSocket(Protocol, String, int, SocketHints)} and
 * {@link Net#newServerSocket(Protocol, int, ServerSocketHints)}.
 * </p>
 * 
 * A socket has an {@link InputStream} used to send data to the other end of the connection, and an {@link OutputStream} to
 * receive data from the other end of the connection.
 * </p>
 * 
 * A socket needs to be disposed if it is no longer used. Disposing also closes the connection.
 * 
 * @author mzechner */
public interface Socket extends Disposable {
	/** @return whether the socket is connected */
	public boolean isConnected ();

	/** @return the {@link InputStream} used to read data from the other end of the connection. */
	public InputStream getInputStream ();

	/** @return the {@link OutputStream} used to write data to the other end of the connection. */
	public OutputStream getOutputStream ();

	/** @return the RemoteAddress of the Socket as String */
	public String getRemoteAddress ();
}
