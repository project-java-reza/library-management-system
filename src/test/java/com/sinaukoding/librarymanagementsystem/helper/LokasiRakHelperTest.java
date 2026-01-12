package com.sinaukoding.librarymanagementsystem.helper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LokasiRakHelperTest {

    @Test
    void testCombineLokasiRak_Success() {
        String result = LokasiRakHelper.combineLokasiRak("2", "A", "Fiksi", "R01", "B03");

        assertEquals("2 - A - Fiksi - R01 - B03", result);
    }

    @Test
    void testParseLokasiRak_ValidFormat() {
        String lokasiRak = "2 - A - Fiksi - R01 - B03";

        var result = LokasiRakHelper.parseLokasiRak(lokasiRak);

        assertEquals("2", result.get("lantai"));
        assertEquals("A", result.get("ruang"));
        assertEquals("Fiksi", result.get("rak"));
        assertEquals("R01", result.get("nomorRak"));
        assertEquals("B03", result.get("nomorBaris"));
    }

    @Test
    void testParseLokasiRak_OldFormat() {
        String lokasiRak = "AB"; // Format lama

        var result = LokasiRakHelper.parseLokasiRak(lokasiRak);

        // Harus return string lengkap di lantai
        assertEquals("AB", result.get("lantai"));
        assertNull(result.get("ruang"));
        assertNull(result.get("rak"));
        assertNull(result.get("nomorRak"));
        assertNull(result.get("nomorBaris"));
    }

    @Test
    void testParseLokasiRak_NullInput() {
        var result = LokasiRakHelper.parseLokasiRak(null);

        assertNull(result.get("lantai"));
        assertNull(result.get("ruang"));
        assertNull(result.get("rak"));
        assertNull(result.get("nomorRak"));
        assertNull(result.get("nomorBaris"));
    }

    @Test
    void testParseLokasiRak_EmptyInput() {
        var result = LokasiRakHelper.parseLokasiRak("");

        assertNull(result.get("lantai"));
        assertNull(result.get("ruang"));
        assertNull(result.get("rak"));
        assertNull(result.get("nomorRak"));
        assertNull(result.get("nomorBaris"));
    }

    @Test
    void testParseLokasiRak_DashInput() {
        var result = LokasiRakHelper.parseLokasiRak("-");

        assertNull(result.get("lantai"));
        assertNull(result.get("ruang"));
        assertNull(result.get("rak"));
        assertNull(result.get("nomorRak"));
        assertNull(result.get("nomorBaris"));
    }

    @Test
    void testIsValidLokasiRakFormat_Valid() {
        assertTrue(LokasiRakHelper.isValidLokasiRakFormat("2 - A - Fiksi - R01 - B03"));
    }

    @Test
    void testIsValidLokasiRakFormat_Invalid() {
        assertFalse(LokasiRakHelper.isValidLokasiRakFormat("AB"));
        assertFalse(LokasiRakHelper.isValidLokasiRakFormat("2 - A - Fiksi"));
        assertFalse(LokasiRakHelper.isValidLokasiRakFormat(null));
        assertFalse(LokasiRakHelper.isValidLokasiRakFormat(""));
    }

    @Test
    void testIsAllLokasiFieldsFilled_AllFilled() {
        assertTrue(LokasiRakHelper.isAllLokasiFieldsFilled("2", "A", "Fiksi", "R01", "B03"));
    }

    @Test
    void testIsAllLokasiFieldsFilled_HasEmpty() {
        assertFalse(LokasiRakHelper.isAllLokasiFieldsFilled("2", "", "Fiksi", "R01", "B03"));
    }

    @Test
    void testIsAllLokasiFieldsFilled_HasNull() {
        assertFalse(LokasiRakHelper.isAllLokasiFieldsFilled("2", "A", null, "R01", "B03"));
    }

    @Test
    void testFormatLokasiRakReadable_ValidFormat() {
        String result = LokasiRakHelper.formatLokasiRakReadable("2 - A - Fiksi - R01 - B03");

        assertEquals("Lantai 2, Ruang A, Rak Fiksi, Nomor R01, Baris B03", result);
    }

    @Test
    void testFormatLokasiRakReadable_InvalidFormat() {
        String result = LokasiRakHelper.formatLokasiRakReadable("AB");

        assertEquals("AB", result);
    }

    @Test
    void testFormatLokasiRakReadable_NullInput() {
        String result = LokasiRakHelper.formatLokasiRakReadable(null);

        assertEquals("N/A", result);
    }

    @Test
    void testCombineAndParse_RoundTrip() {
        // Test combine lalu parse harus balik lagi
        String combined = LokasiRakHelper.combineLokasiRak("2", "A", "Fiksi", "R01", "B03");
        var parsed = LokasiRakHelper.parseLokasiRak(combined);

        assertEquals("2", parsed.get("lantai"));
        assertEquals("A", parsed.get("ruang"));
        assertEquals("Fiksi", parsed.get("rak"));
        assertEquals("R01", parsed.get("nomorRak"));
        assertEquals("B03", parsed.get("nomorBaris"));
    }
}
