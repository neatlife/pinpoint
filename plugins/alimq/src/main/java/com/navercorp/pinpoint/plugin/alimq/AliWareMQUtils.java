package com.navercorp.pinpoint.plugin.alimq;

import com.navercorp.pinpoint.common.plugin.util.HostAndPort;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class AliWareMQUtils
{
    private AliWareMQUtils() {
    }
    
    public static String getEndPoint(final SocketAddress socketAddress) {
        if (socketAddress instanceof InetSocketAddress) {
            final InetSocketAddress inetSocketAddress = (InetSocketAddress)socketAddress;
            final InetAddress remoteAddress = inetSocketAddress.getAddress();
            if (remoteAddress != null) {
                return HostAndPort.toHostAndPortString(remoteAddress.getHostAddress(), inetSocketAddress.getPort());
            }
        }
        return "";
    }
}
