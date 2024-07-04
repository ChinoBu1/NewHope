package main;

public class test {
    public static void main(String[] args) {
        float[] test = new float[] { 5206, 6152 };
        float[] BCVP = new float[] { 0, 0 };

        float sum = 0;
        for (int j = 0; j < BCVP.length; j++) {
            float x = test[j] / 12289 - .25f * (BCVP[j]);
            sum = sum + Math.abs(x - Math.round(x));
            System.out.println(x);
        }
        System.out.println(sum);
    };

}
