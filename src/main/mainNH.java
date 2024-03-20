package main;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.*;

public class mainNH {

        public static void main(String[] args) throws Exception {
                Logger logger = Logger.getLogger("mainNH");
                FileHandler fh = new FileHandler("mainNH.log");
                fh.setFormatter(new myformatter());
                logger.addHandler(fh);
                logger.setUseParentHandlers(false);
                NewHope nh;
                int q = 12289;
                int n = 1024;
                float r = 4;
                if (args.length == 0)
                        nh = new NewHope();
                else {
                        n = Integer.parseInt(args[0]);
                        q = Integer.parseInt(args[1]);
                        nh = new NewHope(n, q);
                }

                logger.info(String.format("Parametros: n = %d, q = %d f(x) = " + nh.getF() + "\n\n", n, q));
                long start = System.currentTimeMillis();

                MessageDigest ms = MessageDigest.getInstance("SHA3-256");

                byte[] seed = nh.generateSeed();
                Polynomial m = nh.parseSeed(seed);
                Polynomial sa = nh.generateBinoPol();
                Polynomial ea = nh.generateBinoPol();

                logger.info("m: " + m + "\n \n");
                logger.info("sa: " + sa + "\n");
                logger.info("ea: " + ea + "\n");

                Polynomial pa = Polynomial.PolyModInt(
                                Polynomial.PolyModF(
                                                Polynomial.PolySum(
                                                                Polynomial.PolyMult(m, sa),
                                                                ea),
                                                nh.getF()),
                                nh.getQ());
                logger.info("pa: " + pa + "\n");

                logger.info("\n");
                logger.info("\n");

                Polynomial sb = nh.generateBinoPol();
                Polynomial eb1 = nh.generateBinoPol();
                logger.info("sb: " + sb + "\n");
                logger.info("eb': " + eb1 + "\n");
                Polynomial Kb = Polynomial.PolyModInt(
                                Polynomial.PolyModF(
                                                Polynomial.PolySum(
                                                                Polynomial.PolyMult(pa, sb),
                                                                eb1),
                                                nh.getF()),
                                nh.getQ());

                Polynomial eb = nh.generateBinoPol();
                logger.info("eb: " + eb + "\n");
                Polynomial pb = Polynomial.PolyModInt(
                                Polynomial.PolyModF(
                                                Polynomial.PolySum(
                                                                Polynomial.PolyMult(m, sb),
                                                                eb),
                                                nh.getF()),
                                nh.getQ());
                logger.info("pb: " + pb + "\n");

                Polynomial Ka = Polynomial.PolyModInt(
                                Polynomial.PolyModF(
                                                Polynomial.PolyMult(sa, pb),
                                                nh.getF()),
                                nh.getQ());
                logger.info("\n");
                logger.info("Ka: " + Ka + "\n");

                logger.info("Kb: " + Kb + "\n");

                logger.info("\n \n");
                int[] b = nh.generateBits(n / 4);

                Polynomial adv = nh.generateBinoPol();
                Polynomial er = nh.generateBinoPol();
                Polynomial pr = Polynomial.PolyModInt(
                                Polynomial.PolyModF(
                                                Polynomial.PolySum(
                                                                Polynomial.PolyMult(m, adv),
                                                                er),
                                                nh.getF()),
                                nh.getQ());
                Polynomial rival = Polynomial.PolyModInt(
                                Polynomial.PolyModF(
                                                Polynomial.PolyMult(sb, pr),
                                                nh.getF()),
                                nh.getQ());
                long finishGen = System.currentTimeMillis();
                System.out.println((finishGen - start) + " milisegundos");
                int[] decodea = new int[n / 4];
                int[] decodeb = new int[n / 4];
                int[] decoder = new int[n / 4];
                for (int i = 0; i < b.length; i++) {
                        logger.info("Bit " + i + "\n");
                        float[] paramHint = new float[] {
                                        r / q * ((float) Kb.GetCoef()[i] + (float) .5f * b[i]),
                                        r / q * ((float) Kb.GetCoef()[i + n / 4] + (float) .5f * b[i]),
                                        r / q * ((float) Kb.GetCoef()[i + n / 2] + (float) .5f * b[i]),
                                        r / q * ((float) Kb.GetCoef()[i + 3 * n / 4] + (float) .5f * b[i])
                        };
                        logger.info(String.format(Locale.US,
                                        "Entrada CVP: b = %d  x = (%5f, %5f, %5f, %5f)\n", b[i],
                                        paramHint[0],
                                        paramHint[1],
                                        paramHint[2],
                                        paramHint[3]));
                        int[] CVP = nh.CVP(paramHint, true);
                        float error1 = 0;
                        float errorinf = 0;
                        logger.info(String.format(Locale.US, "B*CVP = ( %f, %f, %f, %f) \n", (CVP[0] + 0.5f * CVP[3]),
                                        (CVP[1] + 0.5f * CVP[3]), (CVP[2] + 0.5f * CVP[3]), (CVP[3] * .5f)));
                        for (int j = 0; j < paramHint.length - 1; j++) {
                                error1 = error1 + Math.abs(paramHint[j] - (CVP[j] + 0.5f * CVP[3]));
                                errorinf = Math.max(errorinf, Math.abs(paramHint[j] - (CVP[j] + 0.5f * CVP[3])));
                        }
                        error1 = error1 + Math.abs(paramHint[3] - (CVP[3] * .5f));
                        errorinf = Math.max(errorinf, Math.abs(paramHint[3] - (CVP[3] * .5f)));
                        logger.info(String.format(Locale.US,
                                        "CVP:(%d, %d, %d, %d)    ||e||1 = %f ||e||Inf = %f\n \n", CVP[0],
                                        CVP[1], CVP[2],
                                        CVP[3], error1, errorinf));
                        long[] coef_a = new long[] { Ka.GetCoef()[i],
                                        Ka.GetCoef()[i + n / 4],
                                        Ka.GetCoef()[i + n / 2],
                                        Ka.GetCoef()[i + 3 * n / 4]
                        };
                        long[] coef_b = new long[] { Kb.GetCoef()[i],
                                        Kb.GetCoef()[i + n / 4],
                                        Kb.GetCoef()[i + n / 2],
                                        Kb.GetCoef()[i + 3 * n / 4]
                        };
                        logger.info("Alice:                                                 Bob:\n");

                        logger.info(String.format(
                                        "Coef:(%5d, %5d, %5d, %5d)                      Coef:(%5d, %5d, %5d, %5d)\n",
                                        coef_a[0],
                                        coef_a[1],
                                        coef_a[2],
                                        coef_a[3],
                                        coef_b[0],
                                        coef_b[1],
                                        coef_b[2],
                                        coef_b[3]));
                        for (int j = 0; j < CVP.length; j++) {
                                CVP[j] = (int) (((CVP[j] % r) + r) % r);
                        }

                        float[] decode_a = new float[] {
                                        (float) Ka.GetCoef()[i] / q - .25f * (CVP[0] + .5f * CVP[3]),
                                        (float) Ka.GetCoef()[i + n / 4] / q - .25f * (CVP[1] + .5f * CVP[3]),
                                        (float) Ka.GetCoef()[i + n / 2] / q - .25f * (CVP[2] + .5f * CVP[3]),
                                        (float) Ka.GetCoef()[i + 3 * n / 4] / q - CVP[3] * .5f * .25f
                        };
                        float[] decode_b = new float[] {
                                        (float) Kb.GetCoef()[i] / q - .25f * (CVP[0] + .5f * CVP[3]),
                                        (float) Kb.GetCoef()[i + n / 4] / q - .25f * (CVP[1] + .5f * CVP[3]),
                                        (float) Kb.GetCoef()[i + n / 2] / q - .25f * (CVP[2] + .5f * CVP[3]),
                                        (float) Kb.GetCoef()[i + 3 * n / 4] / q - CVP[3] * .5f * .25f
                        };

                        logger.info(String.format(Locale.US,
                                        "deco:(%5f, %5f, %5f, %5f)        deco:(%5f, %5f, %5f, %5f)\n",
                                        decode_a[0],
                                        decode_a[1],
                                        decode_a[2],
                                        decode_a[3],
                                        decode_b[0],
                                        decode_b[1],
                                        decode_b[2],
                                        decode_b[3]));
                        decodea[i] = nh.Decode(decode_a, true);
                        decodeb[i] = nh.Decode(decode_b, true);
                        decoder[i] = nh.Decode(new float[] {
                                        (float) rival.GetCoef()[i] / q
                                                        - .25f * (CVP[0] + .5f * CVP[3]),
                                        (float) rival.GetCoef()[i + n / 4] / q
                                                        - .25f * (CVP[1] + .5f * CVP[3]),
                                        (float) rival.GetCoef()[i + n / 2] / q
                                                        - .25f * (CVP[2] + .5f * CVP[3]),
                                        (float) rival.GetCoef()[i + 3 * n / 4] / q
                                                        - CVP[3] * .5f * .25f },
                                        false);
                        logger.info(String.format(Locale.US,
                                        "\nDecode: %d                                              Decode: %d  \n",
                                        decodea[i], decodeb[i]));
                        float error = Math.abs(1f / q * (coef_a[0] - coef_b[0])) +
                                        Math.abs(1f / q * (coef_a[1] - coef_b[1])) +
                                        Math.abs(1f / q * (coef_a[2] - coef_b[2])) +
                                        Math.abs(1f / q * (coef_a[3] - coef_b[3]));

                        logger.info(String.format(Locale.US, "Error: ||x - x'||1 = %f \n", error));
                        logger.info("\n");
                        logger.info("\n");
                }

                logger.info("Clave secreta de A:\n");
                for (int j : decodea) {
                        logger.info(String.format("%d ", j));
                }
                logger.info("\n");
                logger.info("Clave secreta de B:\n");
                for (int j : decodeb) {
                        logger.info(String.format("%d ", j));
                }
                logger.info("\n");
                logger.info("Clave secreta de rival:\n");
                for (int j : decoder) {
                        logger.info(String.format("%d ", j));
                }
                logger.info("\n");
                logger.info("\n");
                logger.info("\n");

                byte[] va = nh.toByte(decodea);
                byte[] vb = nh.toByte(decodeb);
                byte[] vr = nh.toByte(decoder);

                byte[] SKa = ms.digest(va);
                ms.reset();
                byte[] SKb = ms.digest(vb);
                ms.reset();
                byte[] SKr = ms.digest(vr);

                long finish = System.currentTimeMillis();
                System.out.println((finish - start) + " milisegundos");
                System.out.println();
                if (Arrays.equals(SKa, SKb) && !Arrays.equals(SKa, SKr)) {
                        System.out.println("Succes");
                } else {

                        System.out.println("Fail");
                }
        }
}
