package com.hexa.telraw;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RawStringParser {

    public static byte[] toRaw(String str) {
        if (str == null)
            return null;

        byte[] input = str.getBytes(StandardCharsets.UTF_8);
        System.err.println("toRaw input: " + input);
        ByteBuffer buf = ByteBuffer.allocate(input.length);

        int len = parse(input, 0, buf);

        if (len > 0) {
            byte[] output = new byte[buf.position()];
            buf.rewind();
            buf.get(output);
            System.err.println("toRaw output [" + output.length + "]: " + output);
            return output;
        }
        return null;
    }

    /**
     * Convert byte array to text presentation. Identical to toReadable(byte[]
     * data, int block) with block size is 2.
     * 
     * @param data
     * @return
     */
    public static String toReadable(byte[] data) {
        return toReadable(data, 2);
    }

    /**
     * Convert byte array to text presentation.
     * 
     * @param data
     * @param group
     *            Number of bytes which will be grouped. If group is 0, all
     *            bytes will be stringed together.
     * @return
     */
    public static String toReadable(byte[] data, int group) {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < data.length; i++) {
            buf.append(String.format("%02x", data[i]));
            if (group > 0 && (i + 1) % group == 0)
                buf.append(' ');
        }

        return buf.toString();
    }
    
    /**
     * Convert byte array to text presentation of one byte characters.
     * Characters from 0x21 to 0x7e will be kept as it is. Other characters will
     * be represented by 0x2e.
     * 
     * @param data
     * @return
     */
    public static String toReadableChar(byte[] data) {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < data.length; i++) {
            if (data[i] >= 0x21 && data[i] <= 0x7e)
                buf.append((char) data[i]);
            else
                buf.append((char) 0x2e);
        }

        return buf.toString();
    }

    /**
     * Parse a normal byte sequence: "{input}"
     * 
     * @param input
     * @param iIdx
     * @param output
     * @return
     */
    static int parse(byte[] input, int iIdx, ByteBuffer output) {
        int i = iIdx;
        byte c;
        int len;

        while (i < input.length) {
            c = input[i];

            if (c != '\\') {
                // Normal sequence
                output.put(c);
            } else {
                // Parse escape sequence (skip '/')
                len = parseEsc(input, i + 1, output);
                if (len > 0) {
                    // Valid escape sequence
                    i += len;
                } else {
                    // Invalid escape sequence, just consume '/'
                    output.put(c);
                    i++;
                }
            }

            i++;
        }

        return i - iIdx;
    }

    /**
     * Parse an escaped sequence: "\{input}"
     * 
     * @param input
     * @param iIdx
     * @param output
     * @return Number of bytes consumed in input buffer
     */
    static int parseEsc(byte[] input, int iIdx, ByteBuffer output) {
        if (input == null || output == null || iIdx >= input.length)
            return 0;

        byte c = input[iIdx];
        int len;

        if (c == '\\') {
            output.put(c);
            return 1;
        } else if (c == 'x' || c == 'X') {
            len = parseEscHex(input, iIdx + 1, output);
            if (len > 0) {
                // Valid escaped hexadecimal sequence, consume 'x' and the
                // hexadecimal sequence
                return 1 + len;
            } else {
                // Invalid escaped hexadecimal sequence, DON'T consume anything
                return 0;
            }
        } else if (c == 'b' || c == 'B') {
            len = parseEscBin(input, iIdx + 1, output);
            if (len > 0) {
                // Valid escaped binary sequence, consume 'b' and the binary
                // sequence
                return 1 + len;
            } else {
                // Invalid escaped binary sequence, DON'T consume anything
                return 0;
            }
        }

        return 0;
    }

    /**
     * Parse an escaped hexadecimal sequence: "\x{input}"
     * 
     * @param input
     * @param iIdx
     * @param output
     * @return Number of bytes consumed in input buffer
     */
    static int parseEscHex(byte[] input, int iIdx, ByteBuffer output) {
        if (input == null || output == null || iIdx >= input.length)
            return 0;

        byte val = 0;
        byte c;
        int i = iIdx;

        while (i < (iIdx + 2) && i < input.length) {
            c = input[i];
            if (c >= '0' && c <= '9') {
                val = (byte) (val * 16 + (c - '0'));
            } else if (c >= 'A' && c <= 'F') {
                val = (byte) (val * 16 + (c - 'A' + 10));
            } else if (c >= 'a' && c <= 'f') {
                val = (byte) (val * 16 + (c - 'a' + 10));
            } else {
                // Stop parsing when not see [0-9A-Fa-f]
                break;
            }
            i++;
        }

        if (i > iIdx) {
            output.put(val);
            return i - iIdx;
        } else {
            return 0;
        }
    }

    /**
     * Parse an escaped binary sequence: "\b{input}"
     * 
     * @param input
     * @param iIdx
     * @param output
     * @return
     */
    static int parseEscBin(byte[] input, int iIdx, ByteBuffer output) {
        if (input == null || output == null || iIdx >= input.length)
            return 0;

        byte val = 0;
        byte c;
        int i = iIdx;

        while (i < (iIdx + 8) && i < input.length) {
            c = input[i];
            if (c >= '0' && c <= '1') {
                val = (byte) (val * 2 + (c - '0'));
            } else {
                // Stop parsing when not see [0-1]
                break;
            }
            i++;
        }

        if (i > iIdx) {
            output.put(val);
            return i - iIdx;
        } else {
            return 0;
        }
    }

}
