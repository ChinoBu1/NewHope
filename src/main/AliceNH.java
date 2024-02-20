package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;

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
            conection = server.accept();
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

            System.out.println(m);

            sendData(message);
        }

    }

    public static void sendData(byte[] bytes) throws IOException {
        output.writeInt(bytes.length);
        output.write(bytes);
        output.flush();
    }
}
