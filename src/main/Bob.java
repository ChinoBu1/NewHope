package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.crypto.Cipher;

public class Bob {
    private static Socket conection;
    private static DataInputStream input;
    private static DataOutputStream output;
    private static RWLE rwle;
    private static Double stddev;
    static final int PORT = 8888;

    public static void main(String[] args) throws Exception {
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Polynomial SharedKey;
        Polynomial SKb;
        int intentos = 0;
        if (args.length < 4) {
            conection = new Socket(InetAddress.getLocalHost(), PORT);
        } else {
            conection = new Socket(InetAddress.getLocalHost(), Integer.parseInt(args[3]));
        }

        rwle = new RWLE(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        stddev = Double.parseDouble(args[2]);

        input = new DataInputStream(conection.getInputStream());
        output = new DataOutputStream(conection.getOutputStream());

        Polynomial m = readPol(4 * Integer.parseInt(args[0]));
        Polynomial pa = readPol(4 * Integer.parseInt(args[0]));

        Polynomial sb = rwle.generateGauPol(stddev);
        Polynomial eb = rwle.generateGauPol(stddev);
        Polynomial pb = Polynomial.PolyModInt(
                Polynomial.PolyModF(
                        Polynomial.SumPoly(
                                Polynomial.MultPoly(m, sb),
                                Polynomial.EscalarPoly(2, eb)),
                        rwle.getF()),
                rwle.getQ());

        sendPol(pb);

        Polynomial eb1 = rwle.generateGauPol(stddev);
        Polynomial Kb = Polynomial.PolyModInt(
                Polynomial.PolyModF(
                        Polynomial.SumPoly(
                                Polynomial.MultPoly(pa, sb),
                                Polynomial.EscalarPoly(2, eb1)),
                        rwle.getF()),
                rwle.getQ());

        Polynomial oKb = rwle.hint(Kb);

        sendPol(oKb);

        SKb = rwle.extractor(Kb, oKb);
        System.out.println(Byte.toUnsignedInt(rwle.toByte(SKb)[0]));
        conection.close();
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
}
