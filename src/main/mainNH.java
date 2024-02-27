package main;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class mainNH {
        public static void main(String[] args) throws Exception {
                NewHope nh = new NewHope();
                long start = System.currentTimeMillis();

                MessageDigest ms = MessageDigest.getInstance("SHA3-256");

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

                Polynomial sb = nh.generateBinoPol();
                Polynomial eb1 = nh.generateBinoPol();

                Polynomial Kb = Polynomial.PolyModInt(
                                Polynomial.PolyModF(
                                                Polynomial.SumPoly(
                                                                Polynomial.MultPoly(pa, sb),
                                                                eb1),
                                                nh.getF()),
                                nh.getQ());

                Polynomial eb = nh.generateBinoPol();
                Polynomial pb = Polynomial.PolyModInt(
                                Polynomial.PolyModF(
                                                Polynomial.SumPoly(
                                                                Polynomial.MultPoly(m, sb),
                                                                eb),
                                                nh.getF()),
                                nh.getQ());

                int[][] hint = nh.hint(Kb);
                int[] SKb = nh.REC(Kb, hint);
                byte[] K_b = nh.toByte(SKb);
                byte[] key_b = ms.digest(K_b);

                Polynomial Rival = nh.generateBinoPol();

                Polynomial Ka = Polynomial.PolyModInt(
                                Polynomial.PolyModF(
                                                Polynomial.MultPoly(sa, pb),
                                                nh.getF()),
                                nh.getQ());
                ms.reset();
                int[] SKa = nh.REC(Ka, hint);
                byte[] K_a = nh.toByte(SKa);
                byte[] key_a = ms.digest(K_a);
                int[] SKr = nh.REC(Rival, hint);
                byte[] K_r = nh.toByte(SKr);
                long finish = System.currentTimeMillis();
                System.out.println((finish - start) + " milisegundos");
                for (byte b : K_a) {
                        System.out.print(b);
                        System.out.print(" ");
                }
                System.out.println();
                for (byte b : K_b) {
                        System.out.print(b);
                        System.out.print(" ");
                }
                System.out.println();
                for (byte b : K_r) {
                        System.out.print(b);
                        System.out.print(" ");
                }
                System.out.println();
                if (Arrays.equals(key_a, key_b)) {
                        System.out.println("Succes");
                } else {

                        System.out.println("Fail");
                }
        }
}
