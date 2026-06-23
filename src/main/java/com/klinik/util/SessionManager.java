package com.klinik.util;

import com.klinik.model.Pengguna;

/**
 * Utility untuk mengelola sesi pengguna yang sedang login.
 * 
 * @author Daniel sianipar
 */
public class SessionManager {
    
    private static Pengguna currentUser;

    /**
     * Mendapatkan data pengguna yang sedang aktif login.
     * 
     * @return Pengguna yang sedang login
     */
    public static Pengguna getCurrentUser() {
        return currentUser;
    }

    /**
     * Menyimpan data pengguna setelah berhasil login.
     * 
     * @param user Objek Pengguna
     */
    public static void setCurrentUser(Pengguna user) {
        currentUser = user;
    }

    /**
     * Memeriksa apakah ada pengguna yang sedang login.
     * 
     * @return true jika sudah login, false jika belum
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Menghapus sesi login saat ini (logout).
     */
    public static void logout() {
        currentUser = null;
    }
}
