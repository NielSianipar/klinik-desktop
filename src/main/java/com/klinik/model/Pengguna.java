package com.klinik.model;

import java.time.LocalDateTime;

/**
 * Model untuk tabel: pengguna
 * Mewakili akun login semua role (admin, dokter, perawat, pasien)
 */
public class Pengguna {

    private int           idPengguna;
    private String        namaLengkap;
    private String        email;
    private String        kataSandi;
    private String        peran;       // admin | dokter | perawat | pasien
    private boolean       aktif;
    private LocalDateTime terakhirLogin;
    private LocalDateTime dibuatPada;
    private LocalDateTime diperbarui;

    // ----------------------------------------------------------------
    // Constructor kosong
    // ----------------------------------------------------------------
    public Pengguna() {}

    // ----------------------------------------------------------------
    // Constructor lengkap (untuk INSERT)
    // ----------------------------------------------------------------
    public Pengguna(String namaLengkap, String email, String kataSandi, String peran) {
        this.namaLengkap = namaLengkap;
        this.email       = email;
        this.kataSandi   = kataSandi;
        this.peran       = peran;
        this.aktif       = true;
    }

    // ----------------------------------------------------------------
    // Getter & Setter
    // ----------------------------------------------------------------
    public int getIdPengguna()              { return idPengguna; }
    public void setIdPengguna(int id)       { this.idPengguna = id; }

    public String getNamaLengkap()          { return namaLengkap; }
    public void setNamaLengkap(String n)    { this.namaLengkap = n; }

    public String getEmail()                { return email; }
    public void setEmail(String e)          { this.email = e; }

    public String getKataSandi()            { return kataSandi; }
    public void setKataSandi(String k)      { this.kataSandi = k; }

    public String getPeran()                { return peran; }
    public void setPeran(String p)          { this.peran = p; }

    public boolean isAktif()                { return aktif; }
    public void setAktif(boolean a)         { this.aktif = a; }

    public LocalDateTime getTerakhirLogin()          { return terakhirLogin; }
    public void setTerakhirLogin(LocalDateTime t)    { this.terakhirLogin = t; }

    public LocalDateTime getDibuatPada()             { return dibuatPada; }
    public void setDibuatPada(LocalDateTime d)       { this.dibuatPada = d; }

    public LocalDateTime getDiperbarui()             { return diperbarui; }
    public void setDiperbarui(LocalDateTime d)       { this.diperbarui = d; }

    // ----------------------------------------------------------------
    // Helper
    // ----------------------------------------------------------------
    public boolean isAdmin()   { return "admin".equals(peran); }
    public boolean isDokter()  { return "dokter".equals(peran); }
    public boolean isPerawat() { return "perawat".equals(peran); }
    public boolean isPasien()  { return "pasien".equals(peran); }

    @Override
    public String toString() {
        return namaLengkap + " (" + peran + ")";
    }
}
