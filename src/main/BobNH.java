package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class BobNH {
    private static Socket conection;
    private static DataInputStream input;
    private static DataOutputStream output;
    private static NewHope nh;
    static final int PORT = 8888;

    public static void main(String[] args) throws Exception {
        int intentos = 0;
        long start = System.currentTimeMillis();
        while (intentos < 1000) {
            if (args.length < 1) {
                conection = new Socket(InetAddress.getLocalHost(), PORT);
            } else {
                conection = new Socket(InetAddress.getLocalHost(), Integer.parseInt(args[0]));
            }

            nh = new NewHope();

            input = new DataInputStream(conection.getInputStream());
            output = new DataOutputStream(conection.getOutputStream());

            byte[] rmessage = reciveData();
            byte[] seed = new byte[32];
            byte[] paByte = new byte[rmessage.length - 32];

            System.arraycopy(rmessage, rmessage.length - 32, seed, 0, 32);
            System.arraycopy(rmessage, 0, paByte, 0, rmessage.length - 32);

            Polynomial pa = nh.fromByteArray(paByte);

            Polynomial sb = nh.generateBinoPol();
            Polynomial eb1 = nh.generateBinoPol();

            Polynomial Kb = Polynomial.PolyModInt(
                    Polynomial.PolyModF(
                            Polynomial.SumPoly(
                                    Polynomial.MultPoly(pa, sb),
                                    eb1),
                            nh.getF()),
                    nh.getQ());

            Polynomial m = nh.parseSeed(seed);

            Polynomial eb = nh.generateBinoPol();
            Polynomial pb = Polynomial.PolyModInt(
                    Polynomial.PolyModF(
                            Polynomial.SumPoly(
                                    Polynomial.MultPoly(m, sb),
                                    eb),
                            nh.getF()),
                    nh.getQ());

            int[][] hint = nh.hint(Kb);

            byte[] hintByte = new byte[hint.length * 4 * 4];
            for (int i = 0; i < hint.length; i++) {
                for (int j = 0; j < hint[i].length; j++) {
                    hintByte[4 * (4 * i + j)] = (byte) (hint[i][j]);
                    hintByte[4 * (4 * i + j) + 1] = (byte) (hint[i][j] >> 8);
                    hintByte[4 * (4 * i + j) + 2] = (byte) (hint[i][j] >> 16);
                    hintByte[4 * (4 * i + j) + 3] = (byte) (hint[i][j] >> 24);
                }
            }

            byte[] pbByte = nh.toByteArray(pb);
            byte[] message = new byte[pbByte.length + hintByte.length];

            System.arraycopy(pbByte, 0, message, 0, pbByte.length);
            System.arraycopy(hintByte, 0, message, pbByte.length, hintByte.length);

            sendData(message);

            int[] SK = nh.REC(Kb, hint);
            byte[] K = nh.toByte(SK);
            MessageDigest ms = MessageDigest.getInstance("SHA3-256");
            byte[] Key = ms.digest(K);

            SecretKey sk = new SecretKeySpec(Key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, sk);
            byte[] secret = reciveData();
            conection.close();

            byte[] decode = Base64.getDecoder().decode(secret);
            byte[] decrypt = new byte[0];
            try {
                decrypt = cipher.doFinal(decode);
                System.out.println(new String(decrypt));
            } catch (Exception e) {
                intentos++;
                System.out.println("Fail");
            }
            if ("Test".equals(new String(decrypt))) {
                long finish = System.currentTimeMillis();
                intentos++;
                System.out.println("Succes in " + (finish - start) + " ms");
                break;
            }
        }
        System.out.println(intentos);

    }

    public static void sendData(byte[] bytes) throws IOException {
        output.writeInt(bytes.length);
        output.write(bytes);
        output.flush();
    }

    public static byte[] reciveData() throws IOException {
        byte[] message = new byte[input.readInt()];
        input.read(message);
        return message;
    }

}
