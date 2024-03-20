package main;

import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Attack {
        public static void main(String[] args) throws Exception {
                Logger logger = Logger.getLogger("Attack");
                FileHandler fh = new FileHandler("Attack.log");
                fh.setFormatter(new myformatter());
                logger.addHandler(fh);
                logger.setUseParentHandlers(false);
                for (int j = 1; j < 27; j++) {
                        logger.info(String.format("%d \n", j));
                        int n = j;
                        int q = 12289;
                        NewHope test = new NewHope(4 * n, q);
                        byte[] seed = test.generateSeed();
                        Polynomial m = test.parseSeed(seed);
                        Polynomial sa = test.generateBinoPol();
                        Polynomial ea = test.generateBinoPol();

                        logger.info(test.getF().toString() + "\n");
                        logger.info(sa.toString() + "\n");

                        Polynomial pa = Polynomial.PolyModInt(
                                        Polynomial.PolyModF(
                                                        Polynomial.PolySum(
                                                                        Polynomial.PolyMult(m, sa),
                                                                        ea),
                                                        test.getF()),
                                        test.getQ());

                        Polynomial sb = test.generateBinoPol();
                        Polynomial eb1 = test.generateBinoPol();

                        Polynomial Kb = Polynomial.PolyModInt(
                                        Polynomial.PolyModF(
                                                        Polynomial.PolySum(
                                                                        Polynomial.PolyMult(pa, sb),
                                                                        eb1),
                                                        test.getF()),
                                        test.getQ());

                        Polynomial eb = test.generateBinoPol();
                        Polynomial pb = Polynomial.PolyModInt(
                                        Polynomial.PolyModF(
                                                        Polynomial.PolySum(
                                                                        Polynomial.PolyMult(m, sb),
                                                                        eb),
                                                        test.getF()),
                                        test.getQ());
                        Polynomial Ka = Polynomial.PolyModInt(
                                        Polynomial.PolyModF(
                                                        Polynomial.PolyMult(sa, pb),
                                                        test.getF()),
                                        test.getQ());
                        int[][] testHint = test.hint(Kb);
                        int[] testRecA = test.Rec(Ka, testHint);
                        int[] testRecB = test.Rec(Kb, testHint);

                        for (int a : testRecA) {
                                System.out.print(a + " ");
                        }
                        System.out.println();
                        for (int b : testRecB) {
                                System.out.print(b + " ");
                        }
                        System.out.println();
                        long start = System.currentTimeMillis();
                        long[] coef_r = new long[n];
                        Arrays.fill(coef_r, -16);
                        boolean end_random = false;
                        boolean end_seq = false;
                        boolean time_break = false;
                        long finish_seq = 0;
                        long finish_random = 0;
                        Polynomial pr = new Polynomial();
                        Polynomial prr = new Polynomial();
                        while (!(end_seq && end_random)) {
                                if (!end_random) {
                                        prr = test.generateBinoPol();
                                        Polynomial Krr = Polynomial.PolyModInt(
                                                        Polynomial.PolyModF(
                                                                        Polynomial.PolyMult(prr, pb),
                                                                        test.getF()),
                                                        test.getQ());
                                        int[] testRecRr = test.Rec(Krr, testHint);
                                        if (Arrays.equals(testRecA, testRecRr)) {
                                                finish_random = System.currentTimeMillis();
                                                end_random = true;
                                        }
                                }
                                if (!end_seq) {
                                        pr = new Polynomial(coef_r);

                                        Polynomial Kr = Polynomial.PolyModInt(
                                                        Polynomial.PolyModF(
                                                                        Polynomial.PolyMult(pr, pb),
                                                                        test.getF()),
                                                        test.getQ());
                                        int[] testRecR = test.Rec(Kr, testHint);

                                        if (Arrays.equals(testRecA, testRecR)) {
                                                finish_seq = System.currentTimeMillis();
                                                end_seq = true;
                                        }
                                        coef_r[0] += 1;
                                        for (int i = 1; i < coef_r.length; i++) {
                                                if (coef_r[i - 1] == 17) {
                                                        coef_r[i - 1] = -16;
                                                        coef_r[i] += 1;
                                                }
                                        }
                                        if (coef_r[coef_r.length - 1] == 17) {
                                                break;
                                        }
                                }

                                if (System.currentTimeMillis() - start > 1_800_000) {
                                        time_break = true;
                                        System.out.println("Secure code");
                                        break;
                                }

                        }

                        logger.info(pr.toString() + "\n");
                        logger.info(String.format("De manera secuencial: %6d ms\n", finish_seq - start));
                        logger.info(prr.toString() + "\n");
                        logger.info(String.format("De manera aletoria: %6d ms\n", finish_random - start));
                        logger.info(String.format("\n \n"));

                        if (time_break) {
                                break;
                        }
                        System.out.println("\u0007");
                }
        }
}
