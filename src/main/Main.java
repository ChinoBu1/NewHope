package main;

import java.security.NoSuchAlgorithmException;

class Main {
        public static void main(String[] args) throws NoSuchAlgorithmException {

                int gradoPolinomiof = Integer.parseInt(args[0]);
                int q = Integer.parseInt(args[1]);
                Double stddev = Double.parseDouble(args[2]);
                RWLE prueba = new RWLE(gradoPolinomiof, q);

                Polynomial f = prueba.getF();
                System.out.println(f);
                int repeticiones = 1;
                boolean fin = false;

                while (!fin && repeticiones <= 1000) {

                        Polynomial m = prueba.generateUnfPol();

                        Polynomial sa = prueba.generateGauPol(stddev);
                        Polynomial ea = prueba.generateGauPol(stddev);
                        Polynomial pa = Polynomial.PolyModInt(
                                        Polynomial.PolyModF(
                                                        Polynomial.SumPoly(
                                                                        Polynomial.MultPoly(m, sa),
                                                                        Polynomial.EscalarPoly(2, ea)),
                                                        f),
                                        q);

                        Polynomial sb = prueba.generateGauPol(stddev);
                        Polynomial eb = prueba.generateGauPol(stddev);
                        Polynomial eb1 = prueba.generateGauPol(stddev);
                        Polynomial Kb = Polynomial.PolyModInt(
                                        Polynomial.PolyModF(
                                                        Polynomial.SumPoly(
                                                                        Polynomial.MultPoly(pa, sb),
                                                                        Polynomial.EscalarPoly(2, eb1)),
                                                        f),
                                        q);

                        Polynomial oKb = prueba.hint(Kb);

                        Polynomial pb = Polynomial.PolyModInt(
                                        Polynomial.PolyModF(
                                                        Polynomial.SumPoly(
                                                                        Polynomial.MultPoly(m, sb),
                                                                        Polynomial.EscalarPoly(2, eb)),
                                                        f),
                                        q);

                        Polynomial SKb = prueba.extractor(Kb, oKb);

                        Polynomial ea1 = prueba.generateGauPol(stddev);
                        Polynomial Ka = Polynomial.PolyModInt(
                                        Polynomial.PolyModF(
                                                        Polynomial.SumPoly(
                                                                        Polynomial.MultPoly(sa, pb),
                                                                        Polynomial.EscalarPoly(2, ea1)),

                                                        f),
                                        q);

                        Polynomial SKa = prueba.extractor(Ka, oKb);

                        if (SKa.equals(SKb)) {
                                System.out.println("Se han realizado " + repeticiones);

                                System.out.println("Shared Key = " + SKa);
                                for (int i = 0; i < prueba.toByte(SKa).length; i++) {
                                        System.out.print(Byte.toUnsignedInt(prueba.toByte(SKa)[i]) + " ");
                                }
                                fin = true;
                        }
                        repeticiones++;
                }
                if (repeticiones > 1000) {
                        System.err.println("1000 repeticiones alcanzadas");

                }
        }

}
