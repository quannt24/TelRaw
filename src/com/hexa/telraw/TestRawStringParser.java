package com.hexa.telraw;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestRawStringParser {

    private String inptGetPersInfo = "\\x00\\x00\\x00\\x01\\x00\\x00\\x00\\x68\\x00\\x00\\x00\\x2d";
    private byte[] binGetPersInfo;

    @Before
    public void setUp() throws Exception {
        binGetPersInfo = new byte[12];
        binGetPersInfo[0] = 0;
        binGetPersInfo[1] = 0;
        binGetPersInfo[2] = 0;
        binGetPersInfo[3] = 1;

        binGetPersInfo[4] = 0;
        binGetPersInfo[5] = 0;
        binGetPersInfo[6] = 0;
        binGetPersInfo[7] = 0x68;

        binGetPersInfo[8] = 0;
        binGetPersInfo[9] = 0;
        binGetPersInfo[10] = 0;
        binGetPersInfo[11] = 0x2d;
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testToRaw() {
        byte[] output = RawStringParser.toRaw(inptGetPersInfo);
        assertArrayEquals(binGetPersInfo, output);
    }

    @Test
    public void testToReadable() {
        String str = RawStringParser.toReadable(binGetPersInfo);
        assertEquals(inptGetPersInfo, str);
    }
    
    @Test
    public void testParse() {
        String input0 = "\\x0001";
        String input15 = "\\x0f01";
        String input255 = "\\xff01";
        byte[] expected0 = { 0, 48, 49 };
        byte[] expected15 = { 15, 48, 49 };
        byte[] expected255 = { -1, 48, 49 };
        ByteBuffer buf = ByteBuffer.allocate(3);
        int len;

        buf.clear();
        len = RawStringParser.parse(input0.getBytes(), 0, buf);
        assertEquals(6, len);
        assertArrayEquals(expected0, buf.array());

        buf.clear();
        len = RawStringParser.parse(input15.getBytes(), 0, buf);
        assertEquals(6, len);
        assertArrayEquals(expected15, buf.array());

        buf.clear();
        len = RawStringParser.parse(input255.getBytes(), 0, buf);
        assertEquals(6, len);
        assertArrayEquals(expected255, buf.array());
    }

    @Test
    public void testParseEscSeq() {
        String input0 = "x0001";
        String input15 = "x0f01";
        String input255 = "xff01";
        byte[] expected0 = { 0, 0 };
        byte[] expected15 = { 15, 0 };
        byte[] expected255 = { -1, 0 };
        ByteBuffer buf = ByteBuffer.allocate(2);
        int len;

        buf.clear();
        len = RawStringParser.parseEsc(input0.getBytes(), 0, buf);
        assertEquals(3, len);
        assertArrayEquals(expected0, buf.array());

        buf.clear();
        len = RawStringParser.parseEsc(input15.getBytes(), 0, buf);
        assertEquals(3, len);
        assertArrayEquals(expected15, buf.array());

        buf.clear();
        len = RawStringParser.parseEsc(input255.getBytes(), 0, buf);
        assertEquals(3, len);
        assertArrayEquals(expected255, buf.array());
    }

    @Test
    public void testParseEscHexSeq() {
        String input0 = "0001";
        String input15 = "0f01";
        String input255 = "ff01";
        byte[] expected0 = { 0, 0 };
        byte[] expected15 = { 15, 0 };
        byte[] expected255 = { -1, 0 };
        ByteBuffer buf = ByteBuffer.allocate(2);
        int len;

        buf.clear();
        len = RawStringParser.parseEscHex(input0.getBytes(), 0, buf);
        assertEquals(2, len);
        assertArrayEquals(expected0, buf.array());

        buf.clear();
        len = RawStringParser.parseEscHex(input15.getBytes(), 0, buf);
        assertEquals(2, len);
        assertArrayEquals(expected15, buf.array());

        buf.clear();
        len = RawStringParser.parseEscHex(input255.getBytes(), 0, buf);
        assertEquals(2, len);
        assertArrayEquals(expected255, buf.array());
    }

}
