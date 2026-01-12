package com.sinaukoding.librarymanagementsystem.helper;

import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;

/**
 * Helper class untuk operasi terkait Lokasi Rak
 * Menangani parsing dan formatting lokasi rak
 */
public class LokasiRakHelper {

    /**
     * Delimiter untuk memisahkan komponen lokasi rak
     */
    private static final String DELIMITER = " - ";

    /**
     * Jumlah komponen yang valid untuk lokasi rak
     */
    private static final int EXPECTED_PARTS = 5;

    /**
     * Menggabungkan 5 komponen lokasi rak menjadi format string
     *
     * @param lantai     Lantai
     * @param ruang      Ruang/Zona
     * @param rak        Rak
     * @param nomorRak   Nomor Rak
     * @param nomorBaris Nomor Baris
     * @return String format: "Lantai - Ruang - Rak - NomorRak - NomorBaris"
     */
    public static String combineLokasiRak(String lantai, String ruang, String rak,
                                          String nomorRak, String nomorBaris) {
        return String.format("%s%s%s%s%s%s%s%s%s",
                lantai, DELIMITER, ruang, DELIMITER, rak, DELIMITER, nomorRak, DELIMITER, nomorBaris);
    }

    /**
     * Memecah string lokasiRak menjadi 5 komponen terpisah
     *
     * Format baru: "Lantai - Ruang - Rak - NomorRak - NomorBaris"
     * Format lama: "AB" atau format lain
     *
     * @param lokasiRak String lokasi rak dari database
     * @return SimpleMap berisi 5 field: lantai, ruang, rak, nomorRak, nomorBaris
     */
    public static SimpleMap parseLokasiRak(String lokasiRak) {
        SimpleMap lokasi = new SimpleMap();

        // Default values
        lokasi.put("lantai", null);
        lokasi.put("ruang", null);
        lokasi.put("rak", null);
        lokasi.put("nomorRak", null);
        lokasi.put("nomorBaris", null);

        if (lokasiRak == null || lokasiRak.isEmpty() || lokasiRak.equals("-")) {
            return lokasi;
        }

        // Coba parse format baru: "Lantai - Ruang - Rak - NomorRak - NomorBaris"
        String[] parts = lokasiRak.split(DELIMITER);
        if (parts.length == EXPECTED_PARTS) {
            lokasi.put("lantai", parts[0]);
            lokasi.put("ruang", parts[1]);
            lokasi.put("rak", parts[2]);
            lokasi.put("nomorRak", parts[3]);
            lokasi.put("nomorBaris", parts[4]);
        } else {
            // Format lama atau tidak dikenali - return sebagai string lengkap di lantai
            lokasi.put("lantai", lokasiRak);
        }

        return lokasi;
    }

    /**
     * Mengecek apakah string lokasiRak valid dan sesuai format
     *
     * @param lokasiRak String lokasi rak
     * @return true jika format valid (5 komponen), false jika tidak
     */
    public static boolean isValidLokasiRakFormat(String lokasiRak) {
        if (lokasiRak == null || lokasiRak.isEmpty() || lokasiRak.equals("-")) {
            return false;
        }

        String[] parts = lokasiRak.split(DELIMITER);
        return parts.length == EXPECTED_PARTS;
    }

    /**
     * Mengecek apakah semua field lokasi sudah terisi di entity
     *
     * @param lantai     Field lantai
     * @param ruang      Field ruang
     * @param rak        Field rak
     * @param nomorRak   Field nomor rak
     * @param nomorBaris Field nomor baris
     * @return true jika semua field terisi, false jika ada yang null/empty
     */
    public static boolean isAllLokasiFieldsFilled(String lantai, String ruang, String rak,
                                                    String nomorRak, String nomorBaris) {
        return lantai != null && !lantai.isEmpty() &&
                ruang != null && !ruang.isEmpty() &&
                rak != null && !rak.isEmpty() &&
                nomorRak != null && !nomorRak.isEmpty() &&
                nomorBaris != null && !nomorBaris.isEmpty();
    }

    /**
     * Format lokasi rak menjadi format yang human-readable
     * Contoh: "2 - A - Fiksi - R01 - B03" → "Lantai 2, Rak A, Fiksi, Nomor R-01, Baris B-03"
     *
     * @param lokasiRak String lokasi rak
     * @return String format human-readable
     */
    public static String formatLokasiRakReadable(String lokasiRak) {
        if (!isValidLokasiRakFormat(lokasiRak)) {
            return lokasiRak != null ? lokasiRak : "N/A";
        }

        String[] parts = lokasiRak.split(DELIMITER);
        return String.format("Lantai %s, Ruang %s, Rak %s, Nomor %s, Baris %s",
                parts[0], parts[1], parts[2], parts[3], parts[4]);
    }
}
