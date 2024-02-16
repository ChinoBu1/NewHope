package main;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class RWLE {
    private final Polynomial f;
    private final int q;
    private final int n;
    SecureRandom random;

    public RWLE(int n, int q) {
        this.n = n;
        this.q = q;
        int[] coef_f = new int[n];
        coef_f[0] = 1;
        Arrays.fill(coef_f, 1, n, 0);
        coef_f[n - 1] = 1;
        this.f = new Polynomial(coef_f);
        try {
            random = SecureRandom.getInstance("Windows-PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public Polynomial getF() {
        return new Polynomial(this.f.GetCoef());
    }

    public int getQ() {
        return this.q;
    }

    public Polynomial generateUnfPol() {
        int[] coef = new int[this.n];
        for (int i = 0; i < this.n; i++) {
            coef[i] = random.nextInt(q);
        }
        return new Polynomial(coef);
    }

    public Polynomial generateGauPol(Double stddev) {
        int[] coef = new int[this.n];
        for (int i = 0; i < this.n; i++) {
            coef[i] = (int) random.nextGaussian(0, stddev);
        }
        return new Polynomial(coef);
    }

    public Polynomial hint(Polynomial a) {
        int b = random.nextInt(2);
        int[] coef_a = a.GetCoef();
        int[] coef_r = new int[this.n - 1];
        Arrays.fill(coef_r, 0);
        if (b == 0) {
            for (int i = 0; i < a.GetGrado() - 1; i++) {
                if (coef_a[i] >= q - q / 4 || coef_a[i] <= q / 4) {
                    coef_r[i] = 0;
                } else {
                    coef_r[i] = 1;
                }
            }
        } else {
            for (int i = 0; i < a.GetGrado() - 1; i++) {
                if (coef_a[i] >= q - q / 4 + 1 || coef_a[i] <= q / 4 + 1) {
                    coef_r[i] = 0;
                } else {
                    coef_r[i] = 1;
                }
            }
        }
        Polynomial result = new Polynomial(coef_r);
        return result;
    }

    public Polynomial extractor(Polynomial a, Polynomial hint) {
        return Polynomial.PolyModInt(
                Polynomial.PolyModInt(
                        Polynomial.SumPoly(a,
                                Polynomial.EscalarPoly((q - 1) / 2, hint)),
                        q),
                2);
    }

    public byte[] toByteArray(Polynomial p) {
        byte[] f = new byte[(p.GetGrado() + 1) * 4];
        for (int i = 0; i <= p.GetGrado(); i += 1) {
            f[4 * i] = (byte) p.GetCoef()[i];
            f[4 * i + 1] = (byte) (p.GetCoef()[i] >> 8);
            f[4 * i + 2] = (byte) (p.GetCoef()[i] >> 16);
            f[4 * i + 3] = (byte) (p.GetCoef()[i] >> 24);
        }
        return f;
    }

    public Polynomial fromByteArray(byte[] bytes) {
        int[] coef = new int[this.n];
        for (int i = 0; i + 3 < bytes.length; i += 4) {
            coef[i / 4] = ((bytes[i] & 0xFF)) |
                    ((bytes[i + 1] & 0xFF) << 8) |
                    ((bytes[i + 2] & 0xFF) << 16) |
                    ((bytes[i + 3] & 0xFF) << 24);
        }
        return new Polynomial(coef);
    }

    public byte[] toByte(Polynomial p) {
        byte[] f = new byte[this.n / 8];
        int[] temp = new int[this.n];
        for (int i = 0; i < p.GetCoef().length; i++) {
            temp[i] = p.GetCoef()[i];
        }
        for (int i = 0; i < this.n / 8; i += 8) {
            f[i] = (byte) (temp[i]
                    | temp[i + 1] << 1
                    | temp[i + 2] << 2
                    | temp[i + 3] << 3
                    | temp[i + 4] << 4
                    | temp[i + 5] << 5
                    | temp[i + 6] << 6
                    | temp[i + 7] << 7);
        }
        return f;
    }
}
