package com.navercorp.pinpoint.bootstrap.plugin.arms;

public class HexCodec
{
    static final char[] HEX_DIGITS;
    
    static Long lowerHexToUnsignedLong(final String lowerHex) {
        final int length = lowerHex.length();
        if (length < 1 || length > 32) {
            return null;
        }
        final int beginIndex = (length > 16) ? (length - 16) : 0;
        return lowerHexToUnsignedLong(lowerHex, beginIndex);
    }
    
    static Long lowerHexToUnsignedLong(final String lowerHex, int index) {
        long result = 0L;
        for (int endIndex = Math.min(index + 16, lowerHex.length()); index < endIndex; ++index) {
            final char c = lowerHex.charAt(index);
            result <<= 4;
            if (c >= '0' && c <= '9') {
                result |= c - '0';
            }
            else {
                if (c < 'a' || c > 'f') {
                    return null;
                }
                result |= c - 'a' + '\n';
            }
        }
        return result;
    }
    
    static String toLowerHex(final long high, final long low) {
        final char[] result = new char[(high != 0L) ? 32 : 16];
        int pos = 0;
        if (high != 0L) {
            writeHexLong(result, pos, high);
            pos += 16;
        }
        writeHexLong(result, pos, low);
        return new String(result);
    }
    
    static String toLowerHex(final long v) {
        final char[] data = new char[16];
        writeHexLong(data, 0, v);
        return new String(data);
    }
    
    static void writeHexLong(final char[] data, final int pos, final long v) {
        writeHexByte(data, pos + 0, (byte)(v >>> 56 & 0xFFL));
        writeHexByte(data, pos + 2, (byte)(v >>> 48 & 0xFFL));
        writeHexByte(data, pos + 4, (byte)(v >>> 40 & 0xFFL));
        writeHexByte(data, pos + 6, (byte)(v >>> 32 & 0xFFL));
        writeHexByte(data, pos + 8, (byte)(v >>> 24 & 0xFFL));
        writeHexByte(data, pos + 10, (byte)(v >>> 16 & 0xFFL));
        writeHexByte(data, pos + 12, (byte)(v >>> 8 & 0xFFL));
        writeHexByte(data, pos + 14, (byte)(v & 0xFFL));
    }
    
    static void writeHexByte(final char[] data, final int pos, final byte b) {
        data[pos + 0] = HexCodec.HEX_DIGITS[b >> 4 & 0xF];
        data[pos + 1] = HexCodec.HEX_DIGITS[b & 0xF];
    }
    
    static {
        HEX_DIGITS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
}
