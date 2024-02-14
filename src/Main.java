
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("Windows-PRNG");
        // ArrayList<Integer> coef_1 = new ArrayList<Integer>();
        // coef_1.add(1);
        // coef_1.add(0);
        // coef_1.add(1);
        // ArrayList<Integer> coef_2 = new ArrayList<Integer>();
        // coef_2.add(21);
        // coef_2.add(12);
        // coef_2.add(1);
        // Polymonial p = new Polymonial(coef_1);
        // Polymonial q = new Polymonial(coef_2);
        // System.out.println(Polymonial.PolyModInt(Polymonial.PolyModF(q, p), 11));
        // Polymonial qp = Polymonial.MultPoly(p, q);
        // System.out.println("p = " + p);
        // System.out.println("p Grado = " + p.GetGrado());
        // System.out.println("m = " + q);
        // System.out.println("p * m = " + qp);

        int gradoPolinomiof = Integer.parseInt(args[0]);
        int q = Integer.parseInt(args[1]);
        int stddev = Integer.parseInt(args[2]);

        ArrayList<Integer> coef_f = new ArrayList<Integer>();
        coef_f.add(1);
        while (coef_f.size() < gradoPolinomiof) {
            coef_f.add(0);
        }

        coef_f.add(1);
        Polynomial f = new Polynomial(coef_f);
        System.out.println(f);

        ArrayList<Integer> coef_m = new ArrayList<Integer>();
        ArrayList<Integer> coef_sa = new ArrayList<Integer>();
        ArrayList<Integer> coef_ea = new ArrayList<Integer>();
        for (int i = 0; i <= gradoPolinomiof; i++) {
            coef_m.add(random.nextInt(q));
            coef_sa.add((int) random.nextGaussian(0, stddev));
            coef_ea.add((int) random.nextGaussian(0, stddev));
        }
        Polynomial m = new Polynomial(coef_m);
        Polynomial sa = new Polynomial(coef_sa);
        Polynomial ea = new Polynomial(coef_ea);
        Polynomial pa = Polynomial.PolyModInt(
                Polynomial.SumPoly(
                        Polynomial.MultPoly(m, sa), Polynomial.EscalarPoly(2, ea)),
                Integer.parseInt(args[1]));
        System.out.println("m = " + m);
        System.out.println("sa = " + sa);
        System.out.println("ea = " + ea);
        System.out.println("m*sa+2ea = " + pa);
        System.out.println("m*sa+2ea mod x^n+1= " + Polynomial.PolyModF(pa, f));

        ArrayList<Integer> coef_sb = new ArrayList<Integer>();
        ArrayList<Integer> coef_eb1 = new ArrayList<Integer>();

        for (int i = 0; i <= gradoPolinomiof; i++) {
            coef_m.add(random.nextInt(q));
            coef_sb.add((int) random.nextGaussian(0, stddev));
            coef_eb1.add((int) random.nextGaussian(0, stddev));
        }

    }
}