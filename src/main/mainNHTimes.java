package main;

import java.util.Arrays;
import java.util.logging.*;
import java.time.*;

public class mainNHTimes {

        public static void main(String[] args) throws Exception {
                Logger logger = Logger.getLogger("mainNHTimes");
                FileHandler fh = new FileHandler("Succes.log");
                fh.setFormatter(new myformatter());
                logger.addHandler(fh);
                logger.setUseParentHandlers(false);
                NewHope nh;
                int q = 12289;
                int n = 1024;
                if (args.length == 0)
                        nh = new NewHope();
                else {
                        n = Integer.parseInt(args[0]);
                        q = Integer.parseInt(args[1]);
                        nh = new NewHope(n, q);
                }
                double Succes = 0;
                logger.info(String.format("Aciertos;\tTotal\n"));
                for (int p = 0; p < 1_000_000; p++) {
                        byte[] seed = nh.generateSeed();
                        Polynomial m = nh.parseSeed(seed);
                        Polynomial sa = nh.generateBinoPol();
                        Polynomial ea = nh.generateBinoPol();

                        Polynomial pa = Polynomial.PolyModInt(
                                        Polynomial.PolyModF(
                                                        Polynomial.PolySum(
                                                                        Polynomial.PolyMult(m, sa),
                                                                        ea),
                                                        nh.getF()),
                                        nh.getQ());

                        Polynomial sb = nh.generateBinoPol();
                        Polynomial eb1 = nh.generateBinoPol();
                        Polynomial Kb = Polynomial.PolyModInt(
                                        Polynomial.PolyModF(
                                                        Polynomial.PolySum(
                                                                        Polynomial.PolyMult(pa, sb),
                                                                        eb1),
                                                        nh.getF()),
                                        nh.getQ());

                        int[][] hint = nh.hint(Kb);
                        byte[] SKb = nh.toByte(nh.Rec(Kb, hint));

                        Polynomial eb = nh.generateBinoPol();
                        Polynomial pb = Polynomial.PolyModInt(
                                        Polynomial.PolyModF(
                                                        Polynomial.PolySum(
                                                                        Polynomial.PolyMult(m, sb),
                                                                        eb),
                                                        nh.getF()),
                                        nh.getQ());

                        Polynomial Ka = Polynomial.PolyModInt(
                                        Polynomial.PolyModF(
                                                        Polynomial.PolyMult(sa, pb),
                                                        nh.getF()),
                                        nh.getQ());

                        byte[] SKa = nh.toByte(nh.Rec(Ka, hint));

                        if (Arrays.equals(SKa, SKb)) {
                                Succes++;
                        }
                        if (p % 1_000 == 0) {
                                System.out.println(p);
                                System.out.println(LocalTime.now());
                        }
                }
                logger.info(String.format(Succes + "\t" + 1_000_000));
        }
}
