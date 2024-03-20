
package main;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Logger;

import org.bouncycastle.crypto.digests.SHAKEDigest;

public class NewHope {
    Logger logger = Logger.getLogger("mainNH");
    private final Polynomial F;
    private final int Q = 12289;
    private final int N = 1024;
    private final float R = 4f;
    private final float[] G = { 0.5f, 0.5f, 0.5f, 0.5f };
    int q;
    int n;
    Random random;

    public NewHope() {
        this.q = Q;
        this.n = N;
        long[] coef_f = new long[n + 1];
        coef_f[0] = 1;
        Arrays.fill(coef_f, 1, n, 0);
        coef_f[n] = 1;
        this.F = new Polynomial(coef_f);
        // random = new Random(2024);
        random = new SecureRandom();
    }

    public NewHope(int n, int q) {
        this.q = q;
        this.n = n;
        long[] coef_f = new long[n + 1];
        coef_f[0] = 1;
        Arrays.fill(coef_f, 1, n, 0);
        coef_f[n] = 1;
        this.F = new Polynomial(coef_f);
        // random = new Random(2024);
        random = new SecureRandom();
    }

    public Polynomial getF() {
        return new Polynomial(this.F.GetCoef());
    }

    public int getQ() {
        return q;
    }

    public void setSeed(int seed) {
        random.setSeed(seed);
    }

    public int[] generateBits(int n) {
        int[] temp = new int[n];
        for (int i = 0; i < n; i++) {
            temp[i] = random.nextInt(2);
        }
        return temp;
    }

    public byte[] generateSeed() {
        int[] temp = generateBits(256);
        byte[] seed = new byte[32];
        for (int i = 0; i < 32; i += 1) {
            seed[i] = (byte) (temp[8 * i]
                    | temp[8 * i + 1] << 1
                    | temp[8 * i + 2] << 2
                    | temp[8 * i + 3] << 3
                    | temp[8 * i + 4] << 4
                    | temp[8 * i + 5] << 5
                    | temp[8 * i + 6] << 6
                    | temp[8 * i + 7] << 7);
        }
        return seed;
    }

    public Polynomial parseSeed(byte[] seed) throws Exception {
        SHAKEDigest shake = new SHAKEDigest(128);
        long[] coef = new long[n];
        int last_coef = 0;
        shake.update(seed, 0, seed.length);
        while (last_coef < n) {
            byte[] hashSeed = new byte[2];
            shake.doOutput(hashSeed, 0, 2);
            int temp = ((hashSeed[0] & 0xFF)) |
                    ((hashSeed[1] & 0xFF) << 8);
            if (temp < 5 * q) {
                coef[last_coef] = temp;
                last_coef++;
            }
            if (last_coef >= n) {
                break;
            }
        }
        return new Polynomial(coef);
    }

    public Polynomial generateBinoPol() {
        long[] coef = new long[n];
        for (int i = 0; i < n; i++) {
            long temp = 0;
            for (int j = 0; j < 16; j++) {
                temp = temp + (random.nextInt(2) - random.nextInt(2));
            }
            coef[i] = temp;
        }
        return new Polynomial(coef);
    }

    public int[][] hint(Polynomial x) {
        int[][] resp = new int[n / 4][4];
        long[] temp = new long[n];
        System.arraycopy(x.GetCoef(), 0, temp, 0, x.GetCoef().length);
        for (int i = 0; i < resp.length; i++) {
            int b = random.nextInt(2);
            float[] paramHint = new float[] {
                    R / q * ((float) temp[i] + (float) .5f * b),
                    R / q * ((float) temp[i + n / 4] + (float) .5f * b),
                    R / q * ((float) temp[i + n / 2] + (float) .5f * b),
                    R / q * ((float) temp[i + 3 * n / 4] + (float) .5f * b)
            };
            for (int j = 0; j < R; j++) {
                resp[i][j] = ((CVP(paramHint, false)[j] % 4) + 4) % 4;
            }
        }

        return resp;
    }

    public int[] CVP(float[] x, boolean log) {
        int[] v0 = new int[4];
        int[] v1 = new int[4];
        float norm = 0;
        for (int i = 0; i < x.length; i++) {
            v0[i] = Math.round(x[i]);
            v1[i] = Math.round(x[i] - G[i]);
            norm = norm + Math.abs(x[i] - v0[i]);
        }
        if (log) {
            logger.info(
                    String.format(Locale.US, "v0 = ( %d, %d, %d, %d) v1 = ( %d, %d, %d, %d)  ||x-v0|| = %f  ",
                            v0[0], v0[1], v0[2], v0[3], v1[0], v1[1], v1[2], v1[3], norm));
        }
        if (norm < 1) {
            int[] resp = { (v0[0] - v0[3]),
                    (v0[1] - v0[3]),
                    (v0[2] - v0[3]),
                    (2 * v0[3]) };

            return resp;
        } else {
            int[] resp = { (v1[0] - v1[3]),
                    (v1[1] - v1[3]),
                    (v1[2] - v1[3]),
                    (1 + 2 * v1[3]) };
            return resp;
        }
    }

    public int[] Rec(Polynomial x, int[][] hint) {
        int[] resp = new int[n / 4];
        long[] temp = new long[n];
        System.arraycopy(x.GetCoef(), 0, temp, 0, x.GetCoef().length);
        for (int i = 0; i < resp.length; i++) {
            float[] decodeParam = new float[] {
                    (float) temp[i] / q - .25f * (hint[i][0] + .5f * hint[i][3]),
                    (float) temp[i + n / 4] / q - .25f * (hint[i][1] + .5f * hint[i][3]),
                    (float) temp[i + n / 2] / q - .25f * (hint[i][2] + .5f * hint[i][3]),
                    (float) temp[i + 3 * n / 4] / q - hint[i][3] * .5f * .25f
            };
            resp[i] = Decode(decodeParam, false);
        }
        return resp;
    }

    public int Decode(float[] x, boolean log) {
        float norm = 0;
        for (int i = 0; i < x.length; i++) {
            norm = norm + Math.abs(x[i] - Math.round(x[i]));
        }
        if (log) {
            logger.info(String.format(Locale.US, "Norma: %f                                        ", norm));
        }
        if (norm <= 1)
            return 0;
        else
            return 1;
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
        for (int i = 0; i < this.n / 8; i += 1) {
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

    public byte[] toByte(int[] p) {
        byte[] f = new byte[n / 32];
        for (int i = 0; 8 * i + 7 < p.length; i += 1) {
            f[i] = (byte) (p[8 * i]
                    | p[8 * i + 1] << 1
                    | p[8 * i + 2] << 2
                    | p[8 * i + 3] << 3
                    | p[8 * i + 4] << 4
                    | p[8 * i + 5] << 5
                    | p[8 * i + 6] << 6
                    | p[8 * i + 7] << 7);
        }
        return f;
    }

}
