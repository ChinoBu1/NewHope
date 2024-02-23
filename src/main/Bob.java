package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Bob {
    private static Socket conection;
    private static DataInputStream input;
    private static DataOutputStream output;
    private static RWLE rwle;
    private static Double stddev;
    static final int PORT = 8888;

    public static void main(String[] args) throws Exception {
        int i = 0;
        while (i < 1000) {
            if (args.length < 4) {
                conection = new Socket(InetAddress.getLocalHost(), PORT);
            } else {
                conection = new Socket(InetAddress.getLocalHost(), Integer.parseInt(args[3]));
            }
            Polynomial SKb;

            rwle = new RWLE(Integer.parseInt(args[0]), Long.parseLong(args[1]));
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

            SecretKey SK = new SecretKeySpec(rwle.toByte(SKb), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, SK);
            byte[] message = reciveData();
            conection.close();

            byte[] decode = Base64.getDecoder().decode(message);
            byte[] decrypt = new byte[0];
            try {

                decrypt = cipher.doFinal(decode);
            } catch (Exception e) {
                i++;
                System.out.println("Fail");
            }
            if ("Test".equals(new String(decrypt))) {
                i++;
                System.out.println("Succes");
                break;
            }
        }
        System.out.println(i);
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

    public static byte[] reciveData() throws IOException {
        byte[] message = new byte[input.readInt()];
        input.read(message);
        return message;
    }
}
