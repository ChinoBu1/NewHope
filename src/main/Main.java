package main;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

class Main {
        public static void main(String[] args) throws Exception {

                int gradoPolinomiof = Integer.parseInt(args[0]);
                int q = Integer.parseInt(args[1]);
                Double stddev = Double.parseDouble(args[2]);
                RWLE prueba = new RWLE(gradoPolinomiof, q);
                NewHope test = new NewHope();

                Polynomial f = prueba.getF();
                System.out.println(f);

                Path path = Paths.get("intentos.txt");
                System.out.println(path);

                // for (int i = 0; i < 10000; i++) {

                int repeticiones = 1;
                boolean fin = false;
                long start = System.currentTimeMillis();
                while (!fin && repeticiones <= 1000000) {

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
                                long finish = System.currentTimeMillis();
                                System.out.println("Se han realizado " + repeticiones + " repeticiones en "
                                                + (finish - start)
                                                + " milisegundos");
                                System.out.println(Base64.getEncoder().encodeToString(test.generateSeed()));
                                Polynomial fg = test.parseSeed(test.generateSeed());
                                fin = true;
                                // Files.write(path, (repeticiones + "\n").toString().getBytes(),
                                // StandardOpenOption.APPEND);
                        }

                        long finish = System.currentTimeMillis();
                        if (repeticiones > 1000000) {
                                System.err.println("1000000 repeticiones alcanzadas en " + (finish - start)
                                                + " milisegundos");
                                // Files.write(path, (repeticiones + "\n").toString().getBytes(),
                                // StandardOpenOption.APPEND);

                        }

                        repeticiones++;
                }
                // }
        }

}
