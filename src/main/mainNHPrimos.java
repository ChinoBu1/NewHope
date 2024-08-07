package main;

import java.util.Arrays;
import java.util.logging.*;

public class mainNHPrimos {

        public static void main(String[] args) throws Exception {
                Logger logger = Logger.getLogger("mainNHPrimos");
                FileHandler fh = new FileHandler("pTime2.log");
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
                logger.info(String.format("primo;tiempo\n"));
                for (int p : NewHope.primos) {
                        nh = new NewHope(n, p);
                        long start = System.currentTimeMillis();

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

                        long finish = System.currentTimeMillis();
                        logger.info(p + ";" + (finish - start) + "\n");
                        if (Arrays.equals(SKa, SKb)) {
                                System.out.println("Succes");
                        } else {

                                System.out.println("Fail");
                        }
                }
        }
}
