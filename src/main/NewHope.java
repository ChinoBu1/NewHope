
package main;

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
        int[] coef_f = new int[N + 1];
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
        System.out.println(sun.security.provider.SHAKE128.class);
        KeccakSponge sponge = FIPS202.ExtendableOutputFunction.SHAKE128.withOutputLength(16);
        int[] coef = new int[1024];
        int last_coef = 0;

        while (last_coef < 1024) {
            byte[] hashSeed = sponge.apply(seed);
            for (int i = 0; 2 * i + 1 < hashSeed.length; i++) {
                int temp = ((hashSeed[2 * i] & 0xFF)) |
                        ((hashSeed[2 * i + 1] & 0xFF) << 8);
                if (temp < 5 * Q) {
                    coef[last_coef] = temp;
                    last_coef++;
                }
                if (last_coef >= 1024) {
                    break;
                }
            }
        }
        return new Polynomial(coef);
    }

    public Polynomial generateBinoPol() {
        int[] coef = new int[this.N];
        for (int i = 0; i < this.N; i++) {
            coef[i] = random.nextInt(-16, 17);
        }
        return new Polynomial(coef);
    }

    public Polynomial hint(Polynomial a) {
        int b = random.nextInt(2);
        int[] coef_a = a.GetCoef();
        int[] coef_r = new int[this.N - 1];

        Polynomial result = new Polynomial(coef_r);
        return result;
    }

    public byte[] toByteArray(Polynomial p) {
        byte[] f = new byte[(p.GetGrado() + 1) * 4];
        for (int i = 0; i <= p.GetGrado(); i++) {
            f[4 * i] = (byte) p.GetCoef()[i];
            f[4 * i + 1] = (byte) (p.GetCoef()[i] >> 8);
            f[4 * i + 2] = (byte) (p.GetCoef()[i] >> 16);
            f[4 * i + 3] = (byte) (p.GetCoef()[i] >> 24);
        }
        return f;
    }

    public Polynomial fromByteArray(byte[] bytes) {
        int[] coef = new int[this.N];
        for (int i = 0; 4 * i + 3 < bytes.length; i++) {
            coef[i] = ((bytes[4 * i] & 0xFF)) |
                    ((bytes[4 * i + 1] & 0xFF) << 8) |
                    ((bytes[4 * i + 2] & 0xFF) << 16) |
                    ((bytes[4 * i + 3] & 0xFF) << 24);
        }
        return new Polynomial(coef);
    }

    public byte[] toByte(Polynomial p) {
        byte[] f = new byte[this.N / 8];
        int[] temp = new int[this.N];
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
