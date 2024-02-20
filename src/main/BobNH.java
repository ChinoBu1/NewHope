package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Base64;

public class BobNH {
    private static Socket conection;
    private static DataInputStream input;
    private static DataOutputStream output;
    private static NewHope nh;
    static final int PORT = 8888;

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            conection = new Socket(InetAddress.getLocalHost(), PORT);
        } else {
            conection = new Socket(InetAddress.getLocalHost(), Integer.parseInt(args[0]));
        }

        nh = new NewHope();

        input = new DataInputStream(conection.getInputStream());
        output = new DataOutputStream(conection.getOutputStream());

        byte[] message = reciveData();
        byte[] seed = new byte[32];
        byte[] paByte = new byte[message.length - 32];

        System.arraycopy(message, message.length - 32, seed, 0, 32);
        System.arraycopy(message, 0, paByte, 0, message.length - 32);

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

        System.out.println(m);

        conection.close();

    }

    public static byte[] reciveData() throws IOException {
        byte[] message = new byte[input.readInt()];
        input.read(message);
        return message;
    }
}
