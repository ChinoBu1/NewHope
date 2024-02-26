
package main;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import org.bouncycastle.crypto.digests.SHAKEDigest;

public class NewHope {
    private final Polynomial F;
    private final int Q = 12289;
    private final int N = 1024;
    private final float[] G = { 0.5f, 0.5f, 0.5f, 0.5f };
    SecureRandom random;

    public NewHope() {
        long[] coef_f = new long[N + 1];
        coef_f[0] = 1;
        Arrays.fill(coef_f, 1, N, 0);
        coef_f[N] = 1;
        this.F = new Polynomial(coef_f);
        try {
            random = SecureRandom.getInstance("Windows-PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public Polynomial getF() {
        return new Polynomial(this.F.GetCoef());
    }

    public int getQ() {
        return this.Q;
    }

    public byte[] generateSeed() {
        int[] temp = new int[256];
        byte[] seed = new byte[32];
        for (int i = 0; i < 256; i++) {
            temp[i] = random.nextInt(2);
        }
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
        long[] coef = new long[1024];
        int last_coef = 0;
        shake.update(seed, 0, seed.length);
        while (last_coef < 1024) {
            byte[] hashSeed = new byte[2];
            shake.doOutput(hashSeed, 0, 2);
            int temp = ((hashSeed[0] & 0xFF)) |
                    ((hashSeed[1] & 0xFF) << 8);
            if (temp < 5 * Q) {
                coef[last_coef] = temp;
                last_coef++;
            }
            if (last_coef >= 1024) {
                break;
            }
        }
        return new Polynomial(coef);
    }

    public Polynomial generateBinoPol() {
        long[] coef = new long[this.N];
        for (int i = 0; i < this.N; i++) {
            coef[i] = random.nextInt(-16, 17);
        }
        return new Polynomial(coef);
    }

    public int[][] hint(Polynomial a) {
        long[] coef_a = a.GetCoef();
        int[][] hint = new int[256][];
        for (int i = 0; i < 256; i++) {
            int b = random.nextInt(2);
            float x0 = (float) ((float) coef_a[i] + (float) b / 2);
            float x1 = (float) ((float) coef_a[i + 256] + (float) b / 2);
            float x2 = (float) ((float) coef_a[i + 512] + (float) b / 2);
            float x3 = (float) ((float) coef_a[i + 768] + (float) b / 2);
            float[] temp = { 4 * x0 / Q,
                    4 * x1 / Q,
                    4 * x2 / Q,
                    4 * x3 / Q };
            hint[i] = CVP(temp);
        }
        return hint;
    }

    private int[] CVP(float[] x) {
        int[] v0 = new int[4];
        int[] v1 = new int[4];
        float norm = 0;
        for (int i = 0; i < x.length; i++) {
            v0[i] = Math.round(x[i]);
            v1[i] = Math.round(x[i] - G[i]);
            norm = norm + Math.abs(x[i] - v0[i]);
        }
        if (norm < 1) {
            int[] resp = { (((v0[0] - v0[3]) % (4)) + (4)) % (4),
                    (((v0[1] - v0[3]) % (4)) + (4)) % (4),
                    (((v0[2] - v0[3]) % (4)) + (4)) % (4),
                    (((2 * v0[3]) % (4)) + (4)) % (4) };

            return resp;
        } else {
            int[] resp = { (((v1[0] - v1[3]) % (4)) + (4)) % (4),
                    (((v1[1] - v1[3]) % (4)) + (4)) % (4),
                    (((v1[2] - v1[3]) % (4)) + (4)) % (4),
                    (((1 + 2 * v1[3]) % (4)) + (4)) % (4) };
            return resp;
        }
    }

    public int[] REC(Polynomial x, int[][] hint) {
        long[] coef_x = x.GetCoef();
        int result[] = new int[256];
        for (int i = 0; i < 256; i++) {
            float x0 = ((float) coef_x[i]);
            float x1 = ((float) coef_x[i + 256]);
            float x2 = ((float) coef_x[i + 512]);
            float x3 = ((float) coef_x[i + 768]);
            float[] temp = { x0 - (float) (hint[i][0] + ((float) hint[i][3]) / 2) / 4,
                    x1 - (float) (hint[i][1] + ((float) hint[i][3]) / 2) / 4,
                    x2 - (float) (hint[i][2] + ((float) hint[i][3]) / 2) / 4,
                    x3 - (((float) hint[i][3]) / 2) / 4 };
            result[i] = Decode(temp);

        }
        return result;
    }

    private int Decode(float[] x) {
        float norm = 0;
        for (int i = 0; i < x.length; i++) {
            norm = norm + Math.abs(x[i] - Math.round(x[i]));
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
        long[] coef = new long[this.N];
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
        byte[] f = new byte[this.N / 8];
        long[] temp = new long[this.N];
        for (int i = 0; i < p.GetCoef().length; i++) {
            temp[i] = p.GetCoef()[i];
        }
        for (int i = 0; i < this.N / 8; i += 1) {
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
        byte[] f = new byte[32];
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
