package main;

import java.util.Arrays;

public class Polynomial {
    private final long[] coef;
    private final int grado;

    public Polynomial() {
        this.coef = new long[1];
        this.coef[0] = 0;
        this.grado = 0;
    }

    public Polynomial(long[] coef_f) {
        int i = coef_f.length;
        while (i > 1 && coef_f[i - 1] == 0) {
            i--;
        }
        this.coef = new long[i];
        System.arraycopy(coef_f, 0, this.coef, 0, i);
        ;
        this.grado = this.coef.length - 1;

    }

    public long[] GetCoef() {
        return this.coef;
    }

    public int GetGrado() {
        return this.grado;
    }

    public static Polynomial EscalarPoly(long a, Polynomial b) {
        long[] resultcoef = new long[b.GetGrado() + 1];
        for (int i = 0; i <= b.GetGrado(); i++) {
            resultcoef[i] = b.GetCoef()[i] * a;
        }
        Polynomial result = new Polynomial(resultcoef);
        return result;
    }

    public static Polynomial SumPoly(Polynomial a, Polynomial b) {
        long[] resultcoef = new long[Math.max(a.GetGrado(), b.GetGrado()) + 1];
        if (a.GetGrado() < b.GetGrado()) {
            Polynomial temp = a;
            a = b;
            b = temp;
        }
        int i;
        for (i = 0; i <= b.GetGrado(); i++) {
            resultcoef[i] = a.GetCoef()[i] + b.GetCoef()[i];
        }
        for (int j = i; j <= a.GetGrado(); j++) {
            resultcoef[j] = a.GetCoef()[j];
        }
        Polynomial result = new Polynomial(resultcoef);
        return result;

    }

    public static Polynomial MultPoly(Polynomial a, Polynomial b) {
        long[] resultcoef = new long[a.GetGrado() + b.GetGrado() + 1];

        int i = 0;

        while (i <= a.GetGrado()) {
            long tempa = a.GetCoef()[i];
            int j = 0;
            while (j <= b.GetGrado()) {
                long tempb = b.GetCoef()[j];
                if (i == 0) {
                    resultcoef[j] = tempa * tempb;
                } else {
                    long valor = resultcoef[j + i] + tempa * tempb;
                    resultcoef[j + i] = valor;
                }
                j++;
            }
            i++;
        }
        Polynomial c = new Polynomial(resultcoef);
        return c;
    }

    public static Polynomial PolyModInt(Polynomial a, long q) {
        long[] resultcoef = new long[a.GetGrado() + 1];
        for (int i = 0; i <= a.GetGrado(); i++) {
            if (a.GetCoef()[i] % q < 0) {
                resultcoef[i] = a.GetCoef()[i] % q + q;
            } else {
                resultcoef[i] = a.GetCoef()[i] % q;
            }
        }
        Polynomial b = new Polynomial(resultcoef);
        return b;
    }

    public static Polynomial PolyModF(Polynomial a, Polynomial F) {
        Polynomial q = new Polynomial();
        Polynomial r = a;
        if (a.GetGrado() < F.GetGrado()) {
            return r;
        }
        while (r.GetGrado() >= F.GetGrado()) {
            int temp1 = r.GetGrado() - F.GetGrado();
            long[] coef_t = new long[temp1 + 1];
            long temp2 = r.GetCoef()[r.GetGrado()] / F.GetCoef()[F.GetGrado()];
            coef_t[temp1] = temp2;
            Polynomial t = new Polynomial(coef_t);
            q = SumPoly(q, t);
            r = SumPoly(r, EscalarPoly(-1, MultPoly(F, t)));
        }
        return r;
    }

    @Override
    public String toString() {
        String p = new String();
        for (int i = 0; i <= this.grado; i++) {
            long temp = this.coef[i];
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
                        p = p + String.format("%d*x^%d ", temp, i);
                    } else {
                        p = p + String.format("%+d*x^%d ", temp, i);
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
        return Arrays.equals(c.GetCoef(), this.coef);
    }
}