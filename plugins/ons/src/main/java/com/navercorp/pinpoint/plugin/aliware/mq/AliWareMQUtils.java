package com.navercorp.pinpoint.plugin.aliware.mq;

import com.navercorp.pinpoint.common.plugin.util.*;
import java.net.*;

public class AliWareMQUtils
{
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
