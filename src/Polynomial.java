
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;

public class Polynomial {
    private final ArrayList<Integer> coef;
    private final int grado;

    public Polynomial() {
        this.coef = new ArrayList<Integer>();
        this.coef.add(0);
        this.grado = 0;
    }

    public Polynomial(ArrayList<Integer> coef) {
        while (!coef.isEmpty() && coef.getLast() == 0) {
            coef.remove(coef.size() - 1);
        }
        if (!coef.isEmpty()) {
            this.coef = coef;
            this.grado = coef.size() - 1;
        } else {
            this.coef = new ArrayList<Integer>();
            this.coef.add(0);
            this.grado = 0;
        }

    }

    public ArrayList<Integer> GetCoef() {
        return new ArrayList<Integer>(this.coef);
    }

    public int GetGrado() {
        return this.grado;
    }

    public static Polynomial EscalarPoly(int a, Polynomial b) {
        ArrayList<Integer> resultcoef = new ArrayList<Integer>();
        for (Integer coef : b.GetCoef()) {
            resultcoef.add(a * coef);
        }
        Polynomial result = new Polynomial(resultcoef);
        return result;
    }

    public static Polynomial SumPoly(Polynomial a, Polynomial b) {
        ArrayList<Integer> resultcoef = new ArrayList<Integer>();
        if (a.GetGrado() < b.GetGrado()) {
            Polynomial temp = a;
            a = b;
            b = temp;
        }
        int i;
        for (i = 0; i <= b.GetGrado(); i++) {
            resultcoef.add(a.GetCoef().get(i) + b.GetCoef().get(i));
        }
        for (int j = i; j <= a.GetGrado(); j++) {
            resultcoef.add(a.GetCoef().get(j));
        }
        Polynomial result = new Polynomial(resultcoef);
        return result;

    }

    public static Polynomial MultPoly(Polynomial a, Polynomial b) {
        ArrayList<Integer> resultcoef = new ArrayList<Integer>();

        while (resultcoef.size() <= a.grado + b.grado) {
            resultcoef.add(0);
        }

        Iterator<Integer> acoef = a.GetCoef().iterator();
        int i = 0;

        while (acoef.hasNext()) {
            int tempa = acoef.next();
            Iterator<Integer> bcoef = b.GetCoef().iterator();
            int j = 0;

            while (bcoef.hasNext()) {
                int tempb = bcoef.next();

                if (i == 0) {
                    resultcoef.set(j, tempa * tempb);
                    j++;
                } else {
                    int valor = resultcoef.get(j + i) + tempa * tempb;
                    resultcoef.set(j + i, valor);
                    j++;
                }
            }
            i++;
        }
        Polynomial c = new Polynomial(resultcoef);
        return c;
    }

    public static Polynomial PolyModInt(Polynomial a, int q) {
        ArrayList<Integer> resultcoef = new ArrayList<Integer>();
        for (Integer integer : a.GetCoef()) {
            if (integer % q < 0) {
                resultcoef.add(integer % q + q);
            } else {
                resultcoef.add(integer % q);
            }
        }
        Polynomial b = new Polynomial(resultcoef);
        return b;
    }

    public static Polynomial PolyModF(Polynomial a, Polynomial F) {
        Polynomial q = new Polynomial();
        Polynomial r = a;
        if (a.GetGrado() < F.GetGrado()) {
            return a;
        }
        while (r.GetGrado() >= F.GetGrado()) {
            ArrayList<Integer> coef_t = new ArrayList<Integer>();
            int temp1 = r.GetGrado() - F.GetGrado();
            int temp2 = r.GetCoef().getLast() / F.GetCoef().getLast();
            for (int i = 0; i < temp1; i++) {
                coef_t.add(0);
            }
            coef_t.add(temp2);
            Polynomial t = new Polynomial(coef_t);
            q = SumPoly(q, t);
            r = SumPoly(r, EscalarPoly(-1, MultPoly(F, t)));
        }
        return r;
    }

    public static Polynomial hint(Polynomial a, int q) throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("Windows-PRNG");
        int b = random.nextInt(2);
        ArrayList<Integer> coef_a = a.GetCoef();
        ArrayList<Integer> coef_r = new ArrayList<Integer>();
        if (b == 0) {
            for (Integer integer : coef_a) {
                if (integer >= q - q / 4 || integer <= q / 4) {
                    coef_r.add(0);
                } else {
                    coef_r.add(1);
                }
            }
        } else {
            for (Integer integer : coef_a) {
                if (integer >= q - q / 4 + 1 || integer <= q / 4 + 1) {
                    coef_r.add(0);
                } else {
                    coef_r.add(1);
                }
            }
        }
        Polynomial result = new Polynomial(coef_r);
        return result;
    }

    @Override
    public String toString() {
        String p = new String();
        for (int i = 0; i <= grado; i++) {
            int temp = coef.get(i);
            if (temp == 0)
                continue;
            if (i == 0) {
                p = p + String.format("%d ", temp);
            } else {
                if (temp == 1) {
                    if (p.equals("")) {
                        p = p + String.format("x^%d ", i);
                    } else {
                        p = p + String.format("+x^%d ", i);
                    }
                } else if (temp == -1) {
                    p = p + String.format("-x^%d ", i);
                } else {
                    if (p.equals("")) {
                        p = p + String.format("%dx^%d ", temp, i);
                    } else {
                        p = p + String.format("%+dx^%d ", temp, i);
                    }
                }
            }

        }
        if (p.equals(""))
            p = "0";
        return p;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Polynomial)) {
            return false;
        }
        Polynomial c = (Polynomial) o;
        return c.GetCoef().equals(this.coef);
    }
}