package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Alice {
    private static ServerSocket server;
    private static Socket conection;
    private static DataInputStream input;
    private static DataOutputStream output;
    private static RWLE rwle;
    private static Double stddev;
    static final int PORT = 8888;

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            server = new ServerSocket(PORT);
        } else {
            server = new ServerSocket(Integer.parseInt(args[3]));
        }

        rwle = new RWLE(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        stddev = Double.parseDouble(args[2]);

        System.out.println("Server is listening");
        while (true) {
            conection = server.accept();
            input = new DataInputStream(conection.getInputStream());
            output = new DataOutputStream(conection.getOutputStream());

            Polynomial m = rwle.generateUnfPol();
            Polynomial sa = rwle.generateGauPol(stddev);
            Polynomial ea = rwle.generateGauPol(stddev);
            Polynomial pa = Polynomial.PolyModInt(
                    Polynomial.PolyModF(
                            Polynomial.SumPoly(
                                    Polynomial.MultPoly(m, sa),
                                    Polynomial.EscalarPoly(2, ea)),
                            rwle.getF()),
                    rwle.getQ());

            sendPol(m);
            sendPol(pa);

            Polynomial pb = readPol(4 * Integer.parseInt(args[0]));
            Polynomial oKb = readPol(4 * Integer.parseInt(args[0]));

            Polynomial ea1 = rwle.generateGauPol(stddev);

            Polynomial Ka = Polynomial.PolyModInt(
                    Polynomial.PolyModF(
                            Polynomial.SumPoly(
                                    Polynomial.MultPoly(sa, pb),
                                    Polynomial.EscalarPoly(2, ea1)),
                            rwle.getF()),
                    rwle.getQ());
            Polynomial SKa = rwle.extractor(Ka, oKb);

            SecretKey SK = new SecretKeySpec(rwle.toByte(SKa), "AES");
            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.ENCRYPT_MODE, SK);
            byte[] message = Base64.getEncoder().encode(cipher.doFinal("Test".getBytes()));
            sendData(message);
        }

    }

    public static void sendPol(Polynomial p) throws IOException {
        byte[] message = rwle.toByteArray(p);
        output.write(message);
        output.flush();
    }

    public static Polynomial readPol(int n) throws IOException {
        byte[] mBytes = new byte[n];
        input.read(mBytes);
        return rwle.fromByteArray(mBytes);
    }

    public static void sendData(byte[] bytes) throws IOException {
        output.writeInt(bytes.length);
        output.write(bytes);
        output.flush();
    }

}
