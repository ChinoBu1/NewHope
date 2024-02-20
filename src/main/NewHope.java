
package main;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import uk.org.bobulous.java.crypto.keccak.FIPS202;
import uk.org.bobulous.java.crypto.keccak.KeccakSponge;

public class NewHope {
    private final Polynomial F;
    private final int Q = 12289;
    private final int N = 1024;
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
        KeccakSponge sponge = FIPS202.ExtendableOutputFunction.SHAKE128.withOutputLength(16);
        long[] coef = new long[1024];
        int last_coef = 0;

        while (last_coef < 1024) {
            byte[] hashSeed = sponge.apply(seed);
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

    public Polynomial hint(Polynomial a) {
        int b = random.nextInt(2);
        long[] coef_a = a.GetCoef();
        long[] coef_r = new long[this.N - 1];

        Polynomial result = new Polynomial(coef_r);
        return result;
    }

    public byte[] toByteArray(Polynomial p) {
        byte[] f = new byte[(p.GetGrado() + 1) * 4];
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

}
