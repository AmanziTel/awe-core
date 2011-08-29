/***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2007 Ola Bini <ola@ologix.com>
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/
package org.jruby.ext.socket;

import static com.kenai.constantine.platform.IPProto.IPPROTO_TCP;
import static com.kenai.constantine.platform.IPProto.IPPROTO_IP;
import static com.kenai.constantine.platform.Sock.SOCK_DGRAM;
import static com.kenai.constantine.platform.Sock.SOCK_STREAM;
import static com.kenai.constantine.platform.TCP.TCP_NODELAY;

import java.io.EOFException;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.jruby.CompatVersion;
import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyBoolean;
import org.jruby.RubyClass;
import org.jruby.RubyFixnum;
import org.jruby.RubyIO;
import org.jruby.RubyNumeric;
import org.jruby.RubyString;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.util.ByteList;
import org.jruby.util.Pack;
import org.jruby.util.io.BadDescriptorException;
import org.jruby.util.io.ChannelDescriptor;
import org.jruby.util.io.ChannelStream;
import org.jruby.util.io.ModeFlags;
import org.jruby.util.io.OpenFile;

import com.kenai.constantine.platform.SocketLevel;
import com.kenai.constantine.platform.SocketOption;


/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 */
@JRubyClass(name="BasicSocket", parent="IO")
public class RubyBasicSocket extends RubyIO {
    private static final ByteList FORMAT_SMALL_I = new ByteList(ByteList.plain("i"));
    protected MulticastStateManager multicastStateManager = null;

    private static ObjectAllocator BASICSOCKET_ALLOCATOR = new ObjectAllocator() {
        public IRubyObject allocate(Ruby runtime, RubyClass klass) {
            return new RubyBasicSocket(runtime, klass);
        }
    };

    static void createBasicSocket(Ruby runtime) {
        RubyClass rb_cBasicSocket = runtime.defineClass("BasicSocket", runtime.getIO(), BASICSOCKET_ALLOCATOR);

        rb_cBasicSocket.defineAnnotatedMethods(RubyBasicSocket.class);
    }

    // By default we always reverse lookup unless do_not_reverse_lookup set.
    private boolean doNotReverseLookup = false;

    public RubyBasicSocket(Ruby runtime, RubyClass type) {
        super(runtime, type);
        doNotReverseLookup = runtime.is1_9();
    }
    
    protected void initSocket(Ruby runtime, ChannelDescriptor descriptor) {
        // continue with normal initialization
        openFile = new OpenFile();
        
        try {
            openFile.setMainStream(ChannelStream.fdopen(runtime, descriptor, new ModeFlags(ModeFlags.RDONLY)));
            openFile.setPipeStream(ChannelStream.fdopen(runtime, descriptor, new ModeFlags(ModeFlags.WRONLY)));
            openFile.getPipeStream().setSync(true);
        } catch (org.jruby.util.io.InvalidValueException ex) {
            throw runtime.newErrnoEINVALError();
        }
        openFile.setMode(OpenFile.READWRITE | OpenFile.SYNC);
    }

    @Override
    public IRubyObject close_write(ThreadContext context) {
        if (context.getRuntime().getSafeLevel() >= 4 && isTaint()) {
            throw context.getRuntime().newSecurityError("Insecure: can't close");
        }

        if (!openFile.isWritable()) {
            return context.getRuntime().getNil();
        }

        if (openFile.getPipeStream() == null && openFile.isReadable()) {
            throw context.getRuntime().newIOError("closing non-duplex IO for writing");
        }

        if (!openFile.isReadable()) {
            close();
        } else {
            // shutdown write
            try {
                shutdownInternal(context, 1);
            } catch (BadDescriptorException e) {
                throw context.runtime.newErrnoEBADFError();
            }
        }
        return context.getRuntime().getNil();
    }

    @Override
    public IRubyObject close_read(ThreadContext context) {
        Ruby runtime = context.getRuntime();
        if (runtime.getSafeLevel() >= 4 && isTaint()) {
            throw runtime.newSecurityError("Insecure: can't close");
        }

        if (!openFile.isOpen()) {
            throw context.getRuntime().newIOError("not opened for reading");
        }

        if (!openFile.isWritable()) {
            close();
        } else {
            // shutdown read
            try {
                shutdownInternal(context, 0);
            } catch (BadDescriptorException e) {
                throw context.runtime.newErrnoEBADFError();
            }
        }
        return runtime.getNil();
    }

    @JRubyMethod(name = "send", rest = true)
    public IRubyObject write_send(ThreadContext context, IRubyObject[] args) {
        return syswrite(context, args[0]);
    }

    @Deprecated
    public IRubyObject recv(IRubyObject[] args) {
        return recv(getRuntime().getCurrentContext(), args);
    }
    @JRubyMethod(rest = true)
    public IRubyObject recv(ThreadContext context, IRubyObject[] args) {
        OpenFile openFile = getOpenFileChecked();
        try {
            context.getThread().beforeBlockingCall();
            return RubyString.newString(context.getRuntime(), openFile.getMainStreamSafe().read(RubyNumeric.fix2int(args[0])));
        } catch (BadDescriptorException e) {
            throw context.getRuntime().newErrnoEBADFError();
        } catch (EOFException e) {
            // recv returns nil on EOF
            return context.getRuntime().getNil();
        } catch (IOException e) {
            // All errors to sysread should be SystemCallErrors, but on a closed stream
            // Ruby returns an IOError.  Java throws same exception for all errors so
            // we resort to this hack...
            if ("Socket not open".equals(e.getMessage())) {
	            throw context.getRuntime().newIOError(e.getMessage());
            }
            throw context.getRuntime().newSystemCallError(e.getMessage());
        } finally {
            context.getThread().afterBlockingCall();
        }
    }

    protected InetSocketAddress getLocalSocket(String caller) throws BadDescriptorException {
        Channel socketChannel = getOpenChannel();
        if (socketChannel instanceof SocketChannel) {
            return (InetSocketAddress)((SocketChannel)socketChannel).socket().getLocalSocketAddress();
        } else if (socketChannel instanceof ServerSocketChannel) {
            return (InetSocketAddress)((ServerSocketChannel) socketChannel).socket().getLocalSocketAddress();
        } else if (socketChannel instanceof DatagramChannel) {
            return (InetSocketAddress)((DatagramChannel) socketChannel).socket().getLocalSocketAddress();
        } else {
            return null;
        }
    }
    
    protected InetSocketAddress getRemoteSocket() throws BadDescriptorException {
        Channel socketChannel = getOpenChannel();
        if(socketChannel instanceof SocketChannel) {
            return (InetSocketAddress)((SocketChannel)socketChannel).socket().getRemoteSocketAddress();
        } else {
            return null;
        }
    }

    private Socket asSocket() throws BadDescriptorException {
        Channel socketChannel = getOpenChannel();
        if(!(socketChannel instanceof SocketChannel)) {
            throw getRuntime().newErrnoENOPROTOOPTError();
        }

        return ((SocketChannel)socketChannel).socket();
    }

    private ServerSocket asServerSocket() throws BadDescriptorException {
        Channel socketChannel = getOpenChannel();
        if(!(socketChannel instanceof ServerSocketChannel)) {
            throw getRuntime().newErrnoENOPROTOOPTError();
        }

        return ((ServerSocketChannel)socketChannel).socket();
    }

    private DatagramSocket asDatagramSocket() throws BadDescriptorException {
        Channel socketChannel = getOpenChannel();
        if(!(socketChannel instanceof DatagramChannel)) {
            throw getRuntime().newErrnoENOPROTOOPTError();
        }

        return ((DatagramChannel)socketChannel).socket();
    }

    private IRubyObject getBroadcast(Ruby runtime) throws IOException, BadDescriptorException {
        Channel socketChannel = getOpenChannel();
        return trueFalse(runtime, (socketChannel instanceof DatagramChannel) ? asDatagramSocket().getBroadcast() : false);
    }

    private void setBroadcast(IRubyObject val) throws IOException, BadDescriptorException {
        Channel socketChannel = getOpenChannel();
        if(socketChannel instanceof DatagramChannel) {
            asDatagramSocket().setBroadcast(asBoolean(val));
        }
    }

    private void setKeepAlive(IRubyObject val) throws IOException, BadDescriptorException {
        Channel socketChannel = getOpenChannel();
        if(socketChannel instanceof SocketChannel) {
            asSocket().setKeepAlive(asBoolean(val));
        }
    }

    private void setTcpNoDelay(IRubyObject val) throws IOException, BadDescriptorException {
        Channel socketChannel = getOpenChannel();
        if(socketChannel instanceof SocketChannel) {
            asSocket().setTcpNoDelay(asBoolean(val));
        }
    }

    private void joinMulticastGroup(IRubyObject val) throws IOException, BadDescriptorException {
        Channel socketChannel = getOpenChannel();

        if(socketChannel instanceof DatagramChannel) {
            if (multicastStateManager == null) {
                multicastStateManager = new MulticastStateManager();
            }

            if (val instanceof RubyString) {
                byte [] ipaddr_buf = val.convertToString().getBytes();
                multicastStateManager.addMembership(ipaddr_buf);
            }
        }
    }

    private void setReuseAddr(IRubyObject val) throws IOException, BadDescriptorException {
        Channel socketChannel = getOpenChannel();
        if (socketChannel instanceof ServerSocketChannel) {
            asServerSocket().setReuseAddress(asBoolean(val));
        } else if (socketChannel instanceof SocketChannel) {
            asSocket().setReuseAddress(asBoolean(val));
        } else if (socketChannel instanceof DatagramChannel) {
            asDatagramSocket().setReuseAddress(asBoolean(val));
        }
    }

    private void setRcvBuf(IRubyObject val) throws IOException, BadDescriptorException {
        Channel socketChannel = getOpenChannel();
        if(socketChannel instanceof SocketChannel) {
            asSocket().setReceiveBufferSize(asNumber(val));
        } else if(socketChannel instanceof ServerSocketChannel) {
            asServerSocket().setReceiveBufferSize(asNumber(val));
        } else if(socketChannel instanceof DatagramChannel) {
            asDatagramSocket().setReceiveBufferSize(asNumber(val));
        }
    }

    private void setTimeout(IRubyObject val) throws IOException, BadDescriptorException {
        Channel socketChannel = getOpenChannel();
        if(socketChannel instanceof SocketChannel) {
            asSocket().setSoTimeout(asNumber(val));
        } else if(socketChannel instanceof ServerSocketChannel) {
            asServerSocket().setSoTimeout(asNumber(val));
        } else if(socketChannel instanceof DatagramChannel) {
            asDatagramSocket().setSoTimeout(asNumber(val));
        }
    }

    private void setSndBuf(IRubyObject val) throws IOException, BadDescriptorException {
        try {
            Channel socketChannel = getOpenChannel();
            if(socketChannel instanceof SocketChannel) {
                asSocket().setSendBufferSize(asNumber(val));
            } else if(socketChannel instanceof DatagramChannel) {
                asDatagramSocket().setSendBufferSize(asNumber(val));
            }
        } catch (IllegalArgumentException iae) {
            throw getRuntime().newErrnoEINVALError(iae.getMessage());
        }
    }

    private void setLinger(IRubyObject val) throws IOException, BadDescriptorException {
        Channel socketChannel = getOpenChannel();
        if(socketChannel instanceof SocketChannel) {
            if(val instanceof RubyBoolean && !val.isTrue()) {
                asSocket().setSoLinger(false, 0);
            } else {
                int num = asNumber(val);
                if(num == -1) {
                    asSocket().setSoLinger(false, 0);
                } else {
                    asSocket().setSoLinger(true, num);
                }
            }
        }
    }

    private void setOOBInline(IRubyObject val) throws IOException, BadDescriptorException {
        Channel socketChannel = getOpenChannel();
        if(socketChannel instanceof SocketChannel) {
            asSocket().setOOBInline(asBoolean(val));
        }
    }

    private int asNumber(IRubyObject val) {
        if (val instanceof RubyNumeric) {
            return RubyNumeric.fix2int(val);
        } else if (val instanceof RubyBoolean) {
            return val.isTrue() ? 1 : 0;
        }
        else {
            return stringAsNumber(val);
        }
    }

    private int stringAsNumber(IRubyObject val) {
        ByteList str = val.convertToString().getByteList();
        IRubyObject res = Pack.unpack(getRuntime(), str, FORMAT_SMALL_I).entry(0);
        
        if (res.isNil()) {
            throw getRuntime().newErrnoEINVALError();
        }

        return RubyNumeric.fix2int(res);
    }

    protected boolean asBoolean(IRubyObject val) {
        if (val instanceof RubyString) {
            return stringAsNumber(val) != 0;
        } else if(val instanceof RubyNumeric) {
            return RubyNumeric.fix2int(val) != 0;
        } else {
            return val.isTrue();
        }
    }

    private IRubyObject getKeepAlive(Ruby runtime) throws IOException, BadDescriptorException {
        Channel socketChannel = getOpenChannel();
        return trueFalse(runtime,
                         (socketChannel instanceof SocketChannel) ? asSocket().getKeepAlive() : false
                         );
    }

    private IRubyObject getLinger(Ruby runtime) throws IOException, BadDescriptorException {
        Channel socketChannel = getOpenChannel();

        int linger = 0;
        if (socketChannel instanceof SocketChannel) {
            linger = asSocket().getSoLinger();
            if (linger < 0) {
                linger = 0;
            }
        }

        return number(runtime, linger);
    }

    private IRubyObject getOOBInline(Ruby runtime) throws IOException, BadDescriptorException {
        Channel socketChannel = getOpenChannel();
        return trueFalse(runtime,
                         (socketChannel instanceof SocketChannel) ? asSocket().getOOBInline() : false
                         );
    }

    private IRubyObject getRcvBuf(Ruby runtime) throws IOException, BadDescriptorException {
        Channel socketChannel = getOpenChannel();
        return number(runtime,
                      (socketChannel instanceof SocketChannel) ? asSocket().getReceiveBufferSize() : 
                      ((socketChannel instanceof ServerSocketChannel) ? asServerSocket().getReceiveBufferSize() : 
                       asDatagramSocket().getReceiveBufferSize())
                      );
    }

    private IRubyObject getSndBuf(Ruby runtime) throws IOException, BadDescriptorException {
        Channel socketChannel = getOpenChannel();
        return number(runtime,
                      (socketChannel instanceof SocketChannel) ? asSocket().getSendBufferSize() : 
                      ((socketChannel instanceof DatagramChannel) ? asDatagramSocket().getSendBufferSize() : 0)
                      );
    }

    private IRubyObject getReuseAddr(Ruby runtime) throws IOException, BadDescriptorException {
        Channel socketChannel = getOpenChannel();

        boolean reuse = false;
        if (socketChannel instanceof ServerSocketChannel) {
            reuse = asServerSocket().getReuseAddress();
        } else if (socketChannel instanceof SocketChannel) {
            reuse = asSocket().getReuseAddress();
        } else if (socketChannel instanceof DatagramChannel) {
            reuse = asDatagramSocket().getReuseAddress();
        }

        return trueFalse(runtime, reuse);
    }

    private IRubyObject getTimeout(Ruby runtime) throws IOException, BadDescriptorException {
        Channel socketChannel = getOpenChannel();
        return number(runtime,
                      (socketChannel instanceof SocketChannel) ? asSocket().getSoTimeout() : 
                      ((socketChannel instanceof ServerSocketChannel) ? asServerSocket().getSoTimeout() : 
                       ((socketChannel instanceof DatagramChannel) ? asDatagramSocket().getSoTimeout() : 0))
                      );
    }

    protected int getSoTypeDefault() {
        return 0;
    }
    private int getChannelSoType(Channel channel) {
        if (channel instanceof SocketChannel || channel instanceof ServerSocketChannel) {
            return SOCK_STREAM.value();
        } else if (channel instanceof DatagramChannel) {
            return SOCK_DGRAM.value();
        } else {
            return getSoTypeDefault();
        }
    }
    private IRubyObject getSoType(Ruby runtime) throws IOException, BadDescriptorException {
        Channel socketChannel = getOpenChannel();
        return number(runtime, getChannelSoType(socketChannel));
    }

    private IRubyObject trueFalse(Ruby runtime, boolean val) {
        return number(runtime, val ? 1 : 0);
    }

    private static IRubyObject number(Ruby runtime, int s) {
        RubyArray array = runtime.newArray(runtime.newFixnum(s));
        return Pack.pack(runtime, array, FORMAT_SMALL_I);
    }

    @Deprecated
    public IRubyObject getsockopt(IRubyObject lev, IRubyObject optname) {
        return getsockopt(getRuntime().getCurrentContext(), lev, optname);
    }
    @JRubyMethod
    public IRubyObject getsockopt(ThreadContext context, IRubyObject lev, IRubyObject optname) {
        int level = RubyNumeric.fix2int(lev);
        int opt = RubyNumeric.fix2int(optname);
        Ruby runtime = context.getRuntime();

        try {
            switch(SocketLevel.valueOf(level)) {
            case SOL_IP:
            case SOL_SOCKET:
            case SOL_TCP:
            case SOL_UDP:
                switch(SocketOption.valueOf(opt)) {
                case SO_BROADCAST:
                    return getBroadcast(runtime);
                case SO_KEEPALIVE:
                    return getKeepAlive(runtime);
                case SO_LINGER:
                    return getLinger(runtime);
                case SO_OOBINLINE:
                    return getOOBInline(runtime);
                case SO_RCVBUF:
                    return getRcvBuf(runtime);
                case SO_REUSEADDR:
                    return getReuseAddr(runtime);
                case SO_SNDBUF:
                    return getSndBuf(runtime);
                case SO_RCVTIMEO:
                case SO_SNDTIMEO:
                    return getTimeout(runtime);
                case SO_TYPE:
                    return getSoType(runtime);

                    // Can't support the rest with Java
                case SO_RCVLOWAT:
                    return number(runtime, 1);
                case SO_SNDLOWAT:
                    return number(runtime, 2048);
                case SO_DEBUG:
                case SO_ERROR:
                case SO_DONTROUTE:
                case SO_TIMESTAMP:
                    return trueFalse(runtime, false);
                default:
                    throw context.getRuntime().newErrnoENOPROTOOPTError();
                }
            default:
                throw context.getRuntime().newErrnoENOPROTOOPTError();
            }
        } catch (BadDescriptorException e) {
            throw context.getRuntime().newErrnoEBADFError();
        } catch(IOException e) {
            throw context.getRuntime().newErrnoENOPROTOOPTError();
        }
    }
    @Deprecated
    public IRubyObject setsockopt(IRubyObject lev, IRubyObject optname, IRubyObject val) {
        return setsockopt(getRuntime().getCurrentContext(), lev, optname, val);
    }
    @JRubyMethod
    public IRubyObject setsockopt(ThreadContext context, IRubyObject lev, IRubyObject optname, IRubyObject val) {
        int level = RubyNumeric.fix2int(lev);
        int opt = RubyNumeric.fix2int(optname);

        try {
            switch(SocketLevel.valueOf(level)) {
            case SOL_IP:
            case SOL_SOCKET:
            case SOL_TCP:
            case SOL_UDP:
                switch(SocketOption.valueOf(opt)) {
                case SO_BROADCAST:
                    setBroadcast(val);
                    break;
                case SO_KEEPALIVE:
                    setKeepAlive(val);
                    break;
                case SO_LINGER:
                    setLinger(val);
                    break;
                case SO_OOBINLINE:
                    setOOBInline(val);
                    break;
                case SO_RCVBUF:
                    setRcvBuf(val);
                    break;
                case SO_REUSEADDR:
                    setReuseAddr(val);
                    break;
                case SO_SNDBUF:
                    setSndBuf(val);
                    break;
                case SO_RCVTIMEO:
                case SO_SNDTIMEO:
                    setTimeout(val);
                    break;
                    // Can't support the rest with Java
                case SO_TYPE:
                case SO_RCVLOWAT:
                case SO_SNDLOWAT:
                case SO_DEBUG:
                case SO_ERROR:
                case SO_DONTROUTE:
                case SO_TIMESTAMP:
                    break;
                default:
                    if (IPPROTO_TCP.value() == level && TCP_NODELAY.value() == opt) {
                        setTcpNoDelay(val);
                    }
                    else if (IPPROTO_IP.value() == level) {
                        if (MulticastStateManager.IP_ADD_MEMBERSHIP == opt) {
                            joinMulticastGroup(val);
                        }
                    } else {
                        throw context.getRuntime().newErrnoENOPROTOOPTError();
                    }
                }
                break;
            default:
                if (IPPROTO_TCP.value() == level && TCP_NODELAY.value() == opt) {
                    setTcpNoDelay(val);
                }
                else if (IPPROTO_IP.value() == level) {
                    if (MulticastStateManager.IP_ADD_MEMBERSHIP == opt) {
                        joinMulticastGroup(val);
                    }
                } else {
                    throw context.getRuntime().newErrnoENOPROTOOPTError();
                }
            }
        } catch (BadDescriptorException e) {
            throw context.getRuntime().newErrnoEBADFError();
        } catch(IOException e) {
            throw context.getRuntime().newErrnoENOPROTOOPTError();
        }
        return context.getRuntime().newFixnum(0);
    }

    @Deprecated
    public IRubyObject getsockname() {
        return getsockname(getRuntime().getCurrentContext());
    }

    @JRubyMethod(name = "getsockname")
    public IRubyObject getsockname(ThreadContext context) {
        return getSocknameCommon(context, "getsockname");
    }

    @JRubyMethod(name = "__getsockname")
    public IRubyObject getsockname_u(ThreadContext context) {
        return getSocknameCommon(context, "__getsockname");
    }

    protected IRubyObject getSocknameCommon(ThreadContext context, String caller) {
        try {
            InetSocketAddress sock = getLocalSocket(caller);
            if(null == sock) {
                return RubySocket.pack_sockaddr_in(context, null, 0, "0.0.0.0");
            } else {
               return RubySocket.pack_sockaddr_in(context, sock);
            }
        } catch (BadDescriptorException e) {
            throw context.runtime.newErrnoEBADFError();
        }
    }

    @Deprecated
    public IRubyObject getpeername() {
        return getpeername(getRuntime().getCurrentContext());
    }
    @JRubyMethod(name = {"getpeername", "__getpeername"})
    public IRubyObject getpeername(ThreadContext context) {
        try {
            SocketAddress sock = getRemoteSocket();
            if(null == sock) {
                throw context.getRuntime().newIOError("Not Supported");
            }
            return context.getRuntime().newString(sock.toString());
        } catch (BadDescriptorException e) {
            throw context.runtime.newErrnoEBADFError();
        }
    }

    @JRubyMethod(optional = 1)
    public IRubyObject shutdown(ThreadContext context, IRubyObject[] args) {
        if (context.getRuntime().getSafeLevel() >= 4 && tainted_p(context).isFalse()) {
            throw context.getRuntime().newSecurityError("Insecure: can't shutdown socket");
        }

        int how = 2;
        if (args.length > 0) {
            how = RubyNumeric.fix2int(args[0]);
        }
        try {
            return shutdownInternal(context, how);
        } catch (BadDescriptorException e) {
            throw context.runtime.newErrnoEBADFError();
        }
    }

    private IRubyObject shutdownInternal(ThreadContext context, int how) throws BadDescriptorException {
        Channel socketChannel;
        switch (how) {
        case 0:
            socketChannel = getOpenChannel();
            try {
                if (socketChannel instanceof SocketChannel ||
                        socketChannel instanceof DatagramChannel) {
                    asSocket().shutdownInput();
                } else if (socketChannel instanceof Shutdownable) {
                    ((Shutdownable)socketChannel).shutdownInput();
                }
            } catch (IOException e) {
                throw context.getRuntime().newIOError(e.getMessage());
            }
            if(openFile.getPipeStream() != null) {
                openFile.setMainStream(openFile.getPipeStream());
                openFile.setPipeStream(null);
            }
            openFile.setMode(openFile.getMode() & ~OpenFile.READABLE);
            return RubyFixnum.zero(context.getRuntime());
        case 1:
            socketChannel = getOpenChannel();
            try {
                if (socketChannel instanceof SocketChannel ||
                        socketChannel instanceof DatagramChannel) {
                    asSocket().shutdownOutput();
                } else if (socketChannel instanceof Shutdownable) {
                    ((Shutdownable)socketChannel).shutdownOutput();
                }
            } catch (IOException e) {
                throw context.getRuntime().newIOError(e.getMessage());
            }
            openFile.setPipeStream(null);
            openFile.setMode(openFile.getMode() & ~OpenFile.WRITABLE);
            return RubyFixnum.zero(context.getRuntime());
        case 2:
            shutdownInternal(context, 0);
            shutdownInternal(context, 1);
            return RubyFixnum.zero(context.getRuntime());
        default:
            throw context.getRuntime().newArgumentError("`how' should be either 0, 1, 2");
        }
    }

    protected boolean doNotReverseLookup(ThreadContext context) {
        return context.getRuntime().isDoNotReverseLookupEnabled() || doNotReverseLookup;
    }

    @JRubyMethod(compat = CompatVersion.RUBY1_9)
    public IRubyObject do_not_reverse_lookup19(ThreadContext context) {
        return context.getRuntime().newBoolean(doNotReverseLookup);
    }

    @JRubyMethod(name = "do_not_reverse_lookup=", compat = CompatVersion.RUBY1_9)
    public IRubyObject set_do_not_reverse_lookup19(ThreadContext context, IRubyObject flag) {
        doNotReverseLookup = flag.isTrue();
        return do_not_reverse_lookup19(context);
    }

    @JRubyMethod(meta = true)
    public static IRubyObject do_not_reverse_lookup(IRubyObject recv) {
        return recv.getRuntime().isDoNotReverseLookupEnabled() ? recv.getRuntime().getTrue() : recv.getRuntime().getFalse();
    }
    
    @JRubyMethod(name = "do_not_reverse_lookup=", meta = true)
    public static IRubyObject set_do_not_reverse_lookup(IRubyObject recv, IRubyObject flag) {
        recv.getRuntime().setDoNotReverseLookupEnabled(flag.isTrue());
        return recv.getRuntime().isDoNotReverseLookupEnabled() ? recv.getRuntime().getTrue() : recv.getRuntime().getFalse();
    }
    
    private Channel getOpenChannel() throws BadDescriptorException {
        return getOpenFileChecked().getMainStreamSafe().getDescriptor().getChannel();
    }
}// RubyBasicSocket
