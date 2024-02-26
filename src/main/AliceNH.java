package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AliceNH {
    private static ServerSocket server;
    private static Socket conection;
    private static DataInputStream input;
    private static DataOutputStream output;
    static final int PORT = 8888;
    private static NewHope nh;

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            server = new ServerSocket(PORT);
        } else {
            server = new ServerSocket(Integer.parseInt(args[0]));
        }
        nh = new NewHope();

        while (true) {
            System.out.println("Server is listening");
            conection = server.accept();
            long start = System.currentTimeMillis();
            input = new DataInputStream(conection.getInputStream());
            output = new DataOutputStream(conection.getOutputStream());
            byte[] seed = nh.generateSeed();
            Polynomial m = nh.parseSeed(seed);
            Polynomial sa = nh.generateBinoPol();
            Polynomial ea = nh.generateBinoPol();

            Polynomial pa = Polynomial.PolyModInt(
                    Polynomial.PolyModF(
                            Polynomial.SumPoly(
                                    Polynomial.MultPoly(m, sa),
                                    ea),
                            nh.getF()),
                    nh.getQ());
            byte[] paByte = nh.toByteArray(pa);
            byte[] message = new byte[paByte.length + seed.length];
            System.arraycopy(paByte, 0, message, 0, paByte.length);
            System.arraycopy(seed, 0, message, paByte.length, seed.length);

            sendData(message);

            byte[] rmessage = reciveData();
            byte[] pbByte = new byte[rmessage.length - 256 * 4 * 4];
            byte[] hintByte = new byte[256 * 4 * 4];
            int[][] hint = new int[256][4];
            System.arraycopy(rmessage, 0, pbByte, 0, pbByte.length);
            System.arraycopy(rmessage, pbByte.length, hintByte, 0, hintByte.length);
            Polynomial pb = nh.fromByteArray(pbByte);

            for (int i = 0; i < hint.length; i++) {
                for (int j = 0; j < 4; j++) {
                    hint[i][j] = (hintByte[4 * (4 * i + j)] & 0xFF) |
                            ((hintByte[4 * (4 * i + j) + 1] & 0xFF) << 8)
                            | ((hintByte[4 * (4 * i + j) + 2] & 0xFF) << 16)
                            | ((hintByte[4 * (4 * i + j) + 3] & 0xFF) << 24);
                }
            }
            Polynomial Ka = Polynomial.PolyModInt(
                    Polynomial.PolyModF(
                            Polynomial.MultPoly(sa, pb),
                            nh.getF()),
                    nh.getQ());

            int[] SK = nh.REC(Ka, hint);
            byte[] K = nh.toByte(SK);
            MessageDigest ms = MessageDigest.getInstance("SHA3-256");
            byte[] Key = ms.digest(K);
            SecretKey sk = new SecretKeySpec(Key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, sk);
            byte[] secret = Base64.getEncoder().encode(cipher.doFinal("Test".getBytes()));
            sendData(secret);
            long finish = System.currentTimeMillis();
            System.out.println("Finished in " + (finish - start) + " ms");
        }

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
