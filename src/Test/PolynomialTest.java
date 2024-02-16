package Test;

import static org.junit.Assert.assertEquals;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.junit.Test;

import main.Polynomial;

public class PolynomialTest {

    @Test
    public void testEqual() {
        ArrayList<Integer> coef_1 = new ArrayList<Integer>();
        coef_1.add(1);
        coef_1.add(1);
        coef_1.add(1);
        coef_1.add(1);

        ArrayList<Integer> coef_2 = new ArrayList<Integer>();
        coef_2.add(1);
        coef_2.add(1);
        coef_2.add(1);
        coef_2.add(1);

        Polynomial p = new Polynomial(coef_1);
        Polynomial q = new Polynomial(coef_2);
        assertEquals(true, p.equals(q));
    }

    @Test
    public void testSumPol() {
        ArrayList<Integer> coef_1 = new ArrayList<Integer>();
        coef_1.add(4);
        coef_1.add(6);
        coef_1.add(2);
        coef_1.add(10);

        ArrayList<Integer> coef_2 = new ArrayList<Integer>();
        coef_2.add(-1);
        coef_2.add(-1);
        coef_2.add(0);
        coef_2.add(-8);

        Polynomial p = new Polynomial(coef_1);
        Polynomial q = new Polynomial(coef_2);

        ArrayList<Integer> coef_result = new ArrayList<Integer>();
        coef_result.add(3);
        coef_result.add(5);
        coef_result.add(2);
        coef_result.add(2);
        assertEquals(new Polynomial(coef_result), Polynomial.SumPoly(p, q));
    }

    @Test
    public void testMulPol() {
        ArrayList<Integer> coef_1 = new ArrayList<Integer>();
        coef_1.add(4);
        coef_1.add(6);
        coef_1.add(2);

        ArrayList<Integer> coef_2 = new ArrayList<Integer>();
        coef_2.add(-1);
        coef_2.add(-1);
        coef_2.add(4);

        Polynomial p = new Polynomial(coef_1);
        Polynomial q = new Polynomial(coef_2);

        ArrayList<Integer> coef_result = new ArrayList<Integer>();
        coef_result.add(-4);
        coef_result.add(-10);
        coef_result.add(8);
        coef_result.add(22);
        coef_result.add(8);
        assertEquals(new Polynomial(coef_result), Polynomial.MultPoly(p, q));
    }

    @Test
    public void testEscPol() {
        ArrayList<Integer> coef_1 = new ArrayList<Integer>();
        coef_1.add(4);
        coef_1.add(6);
        coef_1.add(2);
        coef_1.add(-2);

        Polynomial p = new Polynomial(coef_1);
        int q = 4;

        ArrayList<Integer> coef_result = new ArrayList<Integer>();
        coef_result.add(16);
        coef_result.add(24);
        coef_result.add(8);
        coef_result.add(-8);
        assertEquals(new Polynomial(coef_result), Polynomial.EscalarPoly(q, p));
    }

    @Test
    public void testPolModInt() {
        ArrayList<Integer> coef_1 = new ArrayList<Integer>();
        coef_1.add(54);
        coef_1.add(354);
        coef_1.add(-5);
        coef_1.add(-65);
        coef_1.add(-362);

        Polynomial p = new Polynomial(coef_1);
        int q = 13;

        ArrayList<Integer> coef_result = new ArrayList<Integer>();
        coef_result.add(2);
        coef_result.add(3);
        coef_result.add(8);
        coef_result.add(0);
        coef_result.add(2);
        assertEquals(new Polynomial(coef_result), Polynomial.PolyModInt(p, q));
    }

    @Test
    public void testPolModF() {
        ArrayList<Integer> coef_1 = new ArrayList<Integer>();
        coef_1.add(54);
        coef_1.add(354);
        coef_1.add(-5);
        coef_1.add(-62);
        coef_1.add(-362);

        ArrayList<Integer> coef_2 = new ArrayList<Integer>();
        coef_2.add(1);
        coef_2.add(0);
        coef_2.add(0);
        coef_2.add(0);
        coef_2.add(1);

        Polynomial p = new Polynomial(coef_1);
        Polynomial q = new Polynomial(coef_2);

        ArrayList<Integer> coef_result = new ArrayList<Integer>();
        coef_result.add(416);
        coef_result.add(354);
        coef_result.add(-5);
        coef_result.add(-62);
        assertEquals(new Polynomial(coef_result), Polynomial.PolyModF(p, q));
    }

    @Test
    public void testPolModFModInt() {
        ArrayList<Integer> coef_1 = new ArrayList<Integer>();
        coef_1.add(534);
        coef_1.add(54);
        coef_1.add(0);
        coef_1.add(-62);
        coef_1.add(-362);

        ArrayList<Integer> coef_2 = new ArrayList<Integer>();
        coef_2.add(1);
        coef_2.add(0);
        coef_2.add(0);
        coef_2.add(0);
        coef_2.add(1);

        Polynomial p = new Polynomial(coef_1);
        Polynomial q = new Polynomial(coef_2);
        int a = 13;

        assertEquals(Polynomial.PolyModInt(Polynomial.PolyModF(Polynomial.PolyModInt(p, a), q), a),
                Polynomial.PolyModInt(Polynomial.PolyModF(p, q), a));
    }

    @Test
    public void testSignal() throws NoSuchAlgorithmException {
        ArrayList<Integer> coef_1 = new ArrayList<Integer>();
        coef_1.add(9);
        coef_1.add(6);
        coef_1.add(3);
        coef_1.add(13);

        Polynomial p = new Polynomial(coef_1);
        int a = 17;
        Polynomial q = Polynomial.hint(p, a);

        ArrayList<Integer> coef_r = new ArrayList<Integer>();
        coef_r.add(1);
        coef_r.add(1);
        coef_r.add(0);
        coef_r.add(0);

        Polynomial r = new Polynomial(coef_r);

        assertEquals(r, q);
    }

    @Test
    public void testExtractor() throws NoSuchAlgorithmException {
        ArrayList<Integer> coef_1 = new ArrayList<Integer>();
        coef_1.add(1);
        coef_1.add(2);
        coef_1.add(0);
        coef_1.add(6);
        coef_1.add(2);

        Polynomial p = new Polynomial(coef_1);
        int a = 13;
        Polynomial q = Polynomial.hint(p, a);
        Polynomial Extrac = Polynomial.PolyModInt(
                Polynomial.PolyModInt(Polynomial.SumPoly(p, Polynomial.EscalarPoly((a - 1) / 2, q)), a),
                2);

        ArrayList<Integer> coef_2 = new ArrayList<Integer>();
        coef_2.add(1);
        coef_2.add(0);
        coef_2.add(0);
        coef_2.add(0);
        coef_2.add(0);

        assertEquals(new Polynomial(coef_2), Extrac);
    }

}
