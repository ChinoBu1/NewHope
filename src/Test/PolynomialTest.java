package Test;

import static org.junit.Assert.assertEquals;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.junit.Test;

import main.Polynomial;
import main.RWLE;

public class PolynomialTest {

    @Test
    public void testEqual() {
        long[] coef_1 = new long[4];
        Arrays.fill(coef_1, 1);
        coef_1[3] = 0;

        long[] coef_2 = new long[3];
        Arrays.fill(coef_2, 1);

        Polynomial p = new Polynomial(coef_1);
        Polynomial q = new Polynomial(coef_2);
        assertEquals(true, p.equals(q));
    }

    @Test
    public void testStrng() {
        long[] coef_1 = new long[4];
        Arrays.fill(coef_1, 1);

        Polynomial p = new Polynomial(coef_1);
        assertEquals("1 +x^1 +x^2 +x^3 ", p.toString());
    }

    @Test
    public void testSumPol() {
        long[] coef_1 = new long[4];
        coef_1[0] = 4;
        coef_1[1] = 6;
        coef_1[2] = 2;
        coef_1[3] = 10;

        long[] coef_2 = new long[4];
        coef_2[0] = -1;
        coef_2[1] = -1;
        coef_2[2] = 0;
        coef_2[3] = -8;

        Polynomial p = new Polynomial(coef_1);
        Polynomial q = new Polynomial(coef_2);

        long[] coef_result = new long[4];
        coef_result[0] = 3;
        coef_result[1] = 5;
        coef_result[2] = 2;
        coef_result[3] = 2;
        assertEquals(new Polynomial(coef_result), Polynomial.SumPoly(p, q));
    }

    @Test
    public void testMulPol() {
        long[] coef_1 = new long[3];
        coef_1[0] = 4;
        coef_1[1] = 6;
        coef_1[2] = 2;

        long[] coef_2 = new long[3];
        coef_2[0] = -1;
        coef_2[1] = -1;
        coef_2[2] = 4;

        Polynomial p = new Polynomial(coef_1);
        Polynomial q = new Polynomial(coef_2);

        long[] coef_result = new long[5];
        coef_result[0] = -4;
        coef_result[1] = -10;
        coef_result[2] = 8;
        coef_result[3] = 22;
        coef_result[4] = 8;
        assertEquals(new Polynomial(coef_result), Polynomial.MultPoly(p, q));
    }

    @Test
    public void testEscPol() {
        long[] coef_1 = new long[4];
        coef_1[0] = 4;
        coef_1[1] = 6;
        coef_1[2] = 2;
        coef_1[3] = -2;

        Polynomial p = new Polynomial(coef_1);
        int q = 4;

        long[] coef_result = new long[4];
        coef_result[0] = 16;
        coef_result[1] = 24;
        coef_result[2] = 8;
        coef_result[3] = -8;
        assertEquals(new Polynomial(coef_result), Polynomial.EscalarPoly(q, p));
    }

    @Test
    public void testPolModInt() {
        long[] coef_1 = new long[5];
        coef_1[0] = 54;
        coef_1[1] = 354;
        coef_1[2] = -5;
        coef_1[3] = -65;
        coef_1[4] = -362;

        Polynomial p = new Polynomial(coef_1);
        int q = 13;

        long[] coef_result = new long[5];
        coef_result[0] = 2;
        coef_result[1] = 3;
        coef_result[2] = 8;
        coef_result[3] = 0;
        coef_result[4] = 2;
        assertEquals(new Polynomial(coef_result), Polynomial.PolyModInt(p, q));
    }

    @Test
    public void testPolModF() {
        long[] coef_1 = new long[5];
        coef_1[0] = 54;
        coef_1[1] = 354;
        coef_1[2] = -5;
        coef_1[3] = -62;
        coef_1[4] = -362;

        long[] coef_2 = new long[5];
        coef_2[0] = 1;
        coef_2[1] = 0;
        coef_2[2] = 0;
        coef_2[3] = 0;
        coef_2[4] = 1;

        Polynomial p = new Polynomial(coef_1);
        Polynomial q = new Polynomial(coef_2);

        long[] coef_result = new long[4];
        coef_result[0] = 416;
        coef_result[1] = 354;
        coef_result[2] = -5;
        coef_result[3] = -62;
        assertEquals(new Polynomial(coef_result), Polynomial.PolyModF(p, q));
    }

    @Test
    public void testPolModFModInt() {
        long[] coef_1 = new long[5];
        coef_1[0] = 534;
        coef_1[1] = 54;
        coef_1[2] = 0;
        coef_1[3] = -62;
        coef_1[4] = -362;

        long[] coef_2 = new long[5];
        coef_2[0] = 1;
        coef_2[1] = 0;
        coef_2[2] = 0;
        coef_2[3] = 0;
        coef_2[4] = 1;

        Polynomial p = new Polynomial(coef_1);
        Polynomial q = new Polynomial(coef_2);
        int a = 13;

        assertEquals(Polynomial.PolyModInt(Polynomial.PolyModF(Polynomial.PolyModInt(p, a), q), a),
                Polynomial.PolyModInt(Polynomial.PolyModF(p, q), a));
    }

    @Test
    public void testSignal() throws NoSuchAlgorithmException {
        RWLE test = new RWLE(4, 13);
        long[] coef_1 = new long[4];
        coef_1[0] = 9;
        coef_1[1] = 6;
        coef_1[2] = 3;
        coef_1[3] = 13;

        Polynomial p = new Polynomial(coef_1);
        Polynomial q = test.hint(p);

        long[] coef_r = new long[4];
        coef_r[0] = 1;
        coef_r[1] = 1;
        coef_r[2] = 0;
        coef_r[3] = 0;

        Polynomial r = new Polynomial(coef_r);

        assertEquals(r, q);
    }

    @Test
    public void testExtractor() throws NoSuchAlgorithmException {
        RWLE test = new RWLE(6, 13);
        long[] coef_1 = new long[5];
        coef_1[0] = 1;
        coef_1[1] = 2;
        coef_1[2] = 0;
        coef_1[3] = 6;
        coef_1[4] = 2;

        Polynomial p = new Polynomial(coef_1);
        Polynomial q = test.hint(p);
        Polynomial Extrac = test.extractor(p, q);

        long[] coef_2 = new long[5];
        coef_2[0] = 1;
        coef_2[1] = 0;
        coef_2[2] = 0;
        coef_2[3] = 0;
        coef_2[4] = 0;

        assertEquals(new Polynomial(coef_2), Extrac);
    }

    @Test
    public void testbyte() {
        RWLE test = new RWLE(6, 13);
        long[] coef_1 = new long[5];
        coef_1[0] = 1;
        coef_1[1] = 2;
        coef_1[2] = 0;
        coef_1[3] = 6;
        coef_1[4] = 2;

        Polynomial p = new Polynomial(coef_1);
        byte[] q = test.toByteArray(p);
        Polynomial reconstruc = test.fromByteArray(q);

        assertEquals(p, reconstruc);
    }

}
