
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("Windows-PRNG");
        int gradoPolinomiof = Integer.parseInt(args[0]);
        int q = Integer.parseInt(args[1]);
        Double stddev = Double.parseDouble(args[2]);

        ArrayList<Integer> coef_f = new ArrayList<Integer>();
        coef_f.add(1);
        while (coef_f.size() < gradoPolinomiof) {
            coef_f.add(0);
        }
        coef_f.add(1);
        Polynomial f = new Polynomial(coef_f);
        System.out.println(f);
        int repeticiones = 0;
        boolean fin = false;
        while (!fin && repeticiones <= 1000) {

            ArrayList<Integer> coef_m = new ArrayList<Integer>();
            ArrayList<Integer> coef_sa = new ArrayList<Integer>();
            ArrayList<Integer> coef_ea = new ArrayList<Integer>();
            for (int i = 0; i < gradoPolinomiof; i++) {
                coef_m.add(random.nextInt(q));
                coef_sa.add((int) random.nextGaussian(0, stddev));
                coef_ea.add((int) random.nextGaussian(0, stddev));
            }
            Polynomial m = new Polynomial(coef_m);
            Polynomial sa = new Polynomial(coef_sa);
            Polynomial ea = new Polynomial(coef_ea);
            Polynomial pa = Polynomial.PolyModInt(
                    Polynomial.PolyModF(
                            Polynomial.SumPoly(
                                    Polynomial.MultPoly(m, sa), Polynomial.EscalarPoly(2, ea)),
                            f),
                    q);

            ArrayList<Integer> coef_sb = new ArrayList<Integer>();
            ArrayList<Integer> coef_eb = new ArrayList<Integer>();
            ArrayList<Integer> coef_eb1 = new ArrayList<Integer>();

            for (int i = 0; i < gradoPolinomiof; i++) {
                coef_sb.add((int) random.nextGaussian(0, stddev));
                coef_eb.add((int) random.nextGaussian(0, stddev));
                coef_eb1.add((int) random.nextGaussian(0, stddev));
            }
            Polynomial sb = new Polynomial(coef_sb);
            Polynomial eb = new Polynomial(coef_eb);
            Polynomial eb1 = new Polynomial(coef_eb1);
            Polynomial Kb = Polynomial.PolyModInt(
                    Polynomial.PolyModF(
                            Polynomial.SumPoly(
                                    Polynomial.MultPoly(pa, sb), Polynomial.EscalarPoly(2, eb1)),
                            f),
                    q);

            Polynomial oKb = Polynomial.hint(Kb, q);

            Polynomial pb = Polynomial.PolyModInt(
                    Polynomial.PolyModF(
                            Polynomial.SumPoly(
                                    Polynomial.MultPoly(m, sb), Polynomial.EscalarPoly(2, eb)),
                            f),
                    q);

            Polynomial SKb = Polynomial.PolyModInt(
                    Polynomial.PolyModInt(
                            Polynomial.SumPoly(Kb, Polynomial.EscalarPoly((q - 1) / 2, oKb)), q),
                    2);

            ArrayList<Integer> coef_ea1 = new ArrayList<Integer>();
            for (int i = 0; i < gradoPolinomiof; i++) {
                coef_ea1.add((int) random.nextGaussian(0, stddev));
            }
            Polynomial ea1 = new Polynomial(coef_ea1);
            Polynomial Ka = Polynomial.PolyModInt(
                    Polynomial.PolyModF(
                            Polynomial.SumPoly(
                                    Polynomial.MultPoly(sa, pb), Polynomial.EscalarPoly(2, ea1)),

                            f),
                    q);

            Polynomial SKa = Polynomial.PolyModInt(
                    Polynomial.PolyModInt(
                            Polynomial.SumPoly(Ka, Polynomial.EscalarPoly((q - 1) / 2, oKb)), q),
                    2);

            System.out.println(repeticiones);
            if (SKa.equals(SKb)) {
                System.out.println("Shared Key = " + SKa);
                fin = true;
            }
            repeticiones++;
        }
        if (repeticiones > 1000) {
            System.err.println("1000 repeticiones alcanzadas");
        }
    }

}
