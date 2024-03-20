package main;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class RWLE {
    private final Polynomial f;
    private final long q;
    private final int n;
    SecureRandom random;

    public RWLE(int n, long q) {
        this.n = n;
        this.q = q;
        long[] coef_f = new long[n + 1];
        coef_f[0] = 1;
        Arrays.fill(coef_f, 1, n, 0);
        coef_f[n] = 1;
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

    public long getQ() {
        return this.q;
    }

    public Polynomial generateUnfPol() {
        long[] coef = new long[this.n];
        for (int i = 0; i < this.n; i++) {
            coef[i] = random.nextLong(q);
        }
        return new Polynomial(coef);
    }

    public Polynomial generateGauPol(Double stddev) {
        long[] coef = new long[this.n];
        for (int i = 0; i < this.n; i++) {
            coef[i] = (int) random.nextGaussian(0, stddev);
        }
        return new Polynomial(coef);
    }

    public Polynomial hint(Polynomial a) {
        int b = random.nextInt(2);
        long[] coef_a = a.GetCoef();
        long[] coef_r = new long[this.n - 1];
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
                        Polynomial.PolySum(a,
                                Polynomial.PolyEscalar((q - 1) / 2, hint)),
                        q),
                2);
    }

    public byte[] toByteArray(Polynomial p) {
        byte[] f = new byte[(p.GetGrado() + 1) * 8];
        for (int i = 0; i <= p.GetGrado(); i++) {
            f[8 * i] = (byte) p.GetCoef()[i];
            f[8 * i + 1] = (byte) (p.GetCoef()[i] >> 8);
            f[8 * i + 2] = (byte) (p.GetCoef()[i] >> 16);
            f[8 * i + 3] = (byte) (p.GetCoef()[i] >> 24);
            f[8 * i + 4] = (byte) (p.GetCoef()[i] >> 32);
            f[8 * i + 5] = (byte) (p.GetCoef()[i] >> 40);
            f[8 * i + 6] = (byte) (p.GetCoef()[i] >> 48);
            f[8 * i + 7] = (byte) (p.GetCoef()[i] >> 56);
        }
        return f;
    }

    public Polynomial fromByteArray(byte[] bytes) {
        long[] coef = new long[this.n];
        for (int i = 0; 8 * i + 7 < bytes.length; i++) {
            coef[i] = ((bytes[8 * i] & 0xFF)) |
                    ((bytes[8 * i + 1] & 0xFF) << 8) |
                    ((bytes[8 * i + 2] & 0xFF) << 16) |
                    ((bytes[8 * i + 3] & 0xFF) << 24) |
                    ((bytes[8 * i + 4] & 0xFF) << 32) |
                    ((bytes[8 * i + 5] & 0xFF) << 40) |
                    ((bytes[8 * i + 6] & 0xFF) << 48) |
                    ((bytes[8 * i + 7] & 0xFF) << 56);
        }
        return new Polynomial(coef);
    }

    public byte[] toByte(Polynomial p) {
        byte[] f = new byte[this.n / 8];
        long[] temp = new long[this.n];
        for (int i = 0; i < p.GetCoef().length; i++) {
            temp[i] = p.GetCoef()[i];
        }
        for (int i = 0; i * 8 + 7 < temp.length; i += 1) {
            f[i] = (byte) (temp[8 * i]
                    | temp[8 * i + 1] << 1
                    | temp[8 * i + 2] << 2
                    | temp[8 * i + 3] << 3
                    | temp[8 * i + 4] << 4
                    | temp[8 * i + 5] << 5
                    | temp[8 * i + 6] << 6
                    | temp[8 * i + 7] << 7);
        }
        return f;
    }
}
