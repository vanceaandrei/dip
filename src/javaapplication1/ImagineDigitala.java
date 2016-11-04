/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication1;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author vancea
 */
public class ImagineDigitala {

    static final int DIM = 512;

    public float Y[][] = new float[DIM][DIM];
    public float I[][] = new float[DIM][DIM];
    public float Q[][] = new float[DIM][DIM];

    float h[][] = new float[DIM][DIM];
    float s[][] = new float[DIM][DIM];
    float v[][] = new float[DIM][DIM];

    float S[][] = new float[DIM][DIM];
    float S2[][] = new float[DIM][DIM];
    float Theta[][] = new float[DIM][DIM];

    float saturatie = 1;
    ImagineDigitala abc;
    public JFrame interfata;
    int pixelImagine[] = new int[DIM * DIM];

    /**
     * @param args the command line arguments
     */
    public ImagineDigitala(ImageIcon poza, JFrame interfata) {
        this.interfata = interfata;
        separaCulori(poza);
    }

    void dilatareTot() {
        dilata(Y, DIM);
        dilata(I, DIM / 2);
        dilata(Q, DIM / 2);
    }

    void dilata(float[][] tab, int DIM) {
        for (int i = 0; i < DIM / 2; i++) {
            for (int j = 0; j < DIM / 2; j++) {
                S[2 * i][2 * j] = tab[i][j];
            }
        }
        Interpolare(S, DIM);
        copiaza(tab, S, DIM);
    }

    void Interpolare(float[][] tab, int n) {
        //pe coloane
        for (int i = 0; i < n; i += 2) {
            for (int j = 1; j < n - 1; j += 2) {
                tab[i][j] = (tab[i][j - 1] + tab[i][j + 1]) / 2;
            }
        }
        //pe linii
        for (int i = 1; i < n - 1; i += 2) {
            for (int j = 0; j < n; j++) {
                tab[i][j] = (tab[i - 1][j] + tab[i + 1][j]) / 2;
            }
        }
    }

    void separaCulori(ImageIcon poza) {

        PixelGrabber grabber = new PixelGrabber(poza.getImage().getSource(), 0, 0, DIM, DIM, pixelImagine, 0, DIM);
        try {
            grabber.grabPixels();
        } catch (Exception e) {
            return;
        }
        int r, g, b;
        ColorModel CM = ColorModel.getRGBdefault();
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                r = CM.getRed(pixelImagine[i * DIM + j]);
                g = CM.getGreen(pixelImagine[i * DIM + j]);
                b = CM.getBlue(pixelImagine[i * DIM + j]);

                Y[i][j] = 0.299f * r + 0.587f * g + 0.114f * b;
                if (i % 2 == 0 && j % 2 == 0) {
                    I[i / 2][j / 2] = 0;
                    Q[i / 2][j / 2] = 0;

                }

                I[i / 2][j / 2] += (0.5f * r - 0.2f * g - 0.3f * b) / 4;
                Q[i / 2][j / 2] += (0.3f * r + 0.4f * g - 0.7f * b) / 4;
            }
        }
    }

    public void afiseaza(JLabel eticheta, boolean albNegru) {
        //System.out.println("afiseaza Imagine Digitala");
        System.out.println("culoare alb/negru " + albNegru);
        eticheta.setIcon(new ImageIcon(compuneCulori(albNegru)));
    }

    Image compuneCulori(boolean albNegru) {
        System.out.println("Compune culori");

        int r, g, b;
        float pY, pI, pQ;
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                pY = Y[i][j];
                if (albNegru) {
                    pI = pQ = 0;
                } else {

                    pI = I[i / 2][j / 2];
                    pQ = Q[i / 2][j / 2];
                }

                r = corectCapete(Math.round(pY + 1.765f * pI - 0.590 * pQ));
                g = corectCapete(Math.round(pY - 0.937f * pI + 0.564 * pQ));
                b = corectCapete(Math.round(pY + 0.217f * pI - 1.359 * pQ));

                pixelImagine[i * DIM + j] = r << 16 | g << 8 | b | 0xff000000;

            }

        }
        return interfata.createImage(new MemoryImageSource(DIM, DIM, pixelImagine, 0, DIM));
        //Afisare pe rand a cate una din componentele spectrale
    }

    int corectCapete(long v) {
        if (v < 0) {
            return 0;
        }
        if (v > 255) {
            return 255;
        }
        return (int) v;
    }

    //luminozitate RGB
    void ajustareLuminozitate(int k) {
        luminozitate(Y, Y, DIM, (float) k);
    }

    void luminozitate(float[][] dest, float[][] orig, int n, float a) {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                dest[i][j] = orig[i][j] + a;
            }
        }
    }
    float t = 0;

    void ajustareLuminozitateSlider(float c) {
        luminozitateSlider(Y, Y, DIM, c);
    }

    void luminozitateSlider(float[][] dest, float[][] orig, int n, float k) {
        float b;
        if ((k - t) > 0) {
            b = (float) 3;
            t = k;
        } else {
            b = (float) -3;
            t = k;
        }
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                dest[i][j] = orig[i][j] + b;
            }
        }
    }

    //contrast RGB
    void Constrast(float k) {
        contrast(Y, Y, DIM, k);
    }

    void contrast(float[][] dest, float[][] orig, int n, float k) {
        float a = 1;
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                dest[i][j] = (orig[i][j] * k) + a;
            }
        }
    }

    void contrastSlider(float c) {
        Slider(Y, Y, DIM, c);
    }

    void Slider(float[][] dest, float[][] orig, int n, float k) {
        float b;
        if ((k - t) < 0) {
            b = (float) 0.98;
            t = k;
        } else {
            b = (float) 1.001;
            t = k;
        }
        //System.out.println(b);               
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                dest[i][j] = (orig[i][j] * b) + 1;
            }
        }
    }

    void convertestelaHSV(ImageIcon poza) {
        int pixeliImagine[] = new int[DIM * DIM];
        PixelGrabber grabber = new PixelGrabber(poza.getImage().getSource(), 0, 0, DIM, DIM, pixeliImagine, 0, DIM);
        try {
            grabber.grabPixels();
        } catch (Exception e) {
            return;
        }
        int r, g, b;
        ColorModel CM = ColorModel.getRGBdefault();
        float[] hsv = new float[3];
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                r = CM.getRed(pixeliImagine[i * DIM + j]);
                g = CM.getGreen(pixeliImagine[i * DIM + j]);
                b = CM.getBlue(pixeliImagine[i * DIM + j]);
                hsv = Color.RGBtoHSB(r, g, b, hsv);
                h[i][j] = hsv[0];
                s[i][j] = hsv[1];
                v[i][j] = hsv[2];
            }
        }
    }

    Image compuneDinHSV() {
        int pixeliImagine[] = new int[DIM * DIM];
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                pixeliImagine[i * DIM + j] = Color.HSBtoRGB(h[i][j], s[i][j], v[i][j]);
            }
        }
        return interfata.createImage(new MemoryImageSource(DIM, DIM, pixeliImagine, 0, DIM));
    }

    //butoane
    void modiH(double k) {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                h[i][j] += k;
                if (h[i][j] > 1) {
                    h[i][j] = 1;
                }
                if (h[i][j] < 0) {
                    h[i][j] = 0;
                }
            }
        }
    }

    void modiS(double k) {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                s[i][j] += k;
                if (s[i][j] > 1) {
                    s[i][j] = 1;
                }
                if (s[i][j] < 0) {
                    s[i][j] = 0;
                }
            }
        }
    }

    void modiV(double k) {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                v[i][j] += k;
                if (v[i][j] > 1) {
                    v[i][j] = 1;
                }
                if (v[i][j] < 0) {
                    v[i][j] = 0;
                }
            }
        }
    }

    //slider
    void modiSS(int k) {
        float b;
        if ((k - t) > 0) {
            b = (float) -0.03;
            t = k;
        } else {
            b = (float) 0.03;
            t = k;
        }
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                s[i][j] += b;
                if (s[i][j] > 1) {
                    s[i][j] = 1;
                }
                if (s[i][j] < 0) {
                    s[i][j] = 0;
                }
            }
        }
    }
    float y = 49;

    void modiVS(int k) {
        float b;
        if ((k - y) > 0) {
            b = (float) -0.03;
            y = k;
        } else {
            b = (float) 0.03;
            y = k;
        }
        //System.out.println(b);
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                v[i][j] += b;
                if (v[i][j] > 1) {
                    v[i][j] = 1;
                }
                if (v[i][j] < 0) {
                    v[i][j] = 0;
                }
            }
        }
    }

    Image desaturareRGB(ImageIcon imagine, float k) {
        int r, g, b;
        int rd, gd, bd;
        saturatie = k / 100;
        //System.out.println(saturatie);
//        if (saturatie > 1.5) {
//            saturatie = 1;
//        }
//        if (saturatie < 0.5) {
//            saturatie = 0;
//        }
        int pixeliImagine[] = new int[DIM * DIM];
        PixelGrabber grabber = new PixelGrabber(imagine.getImage().getSource(), 0, 0, DIM, DIM, pixeliImagine, 0, DIM);
        try {
            grabber.grabPixels();
        } catch (Exception e) {
            return null;
        }
        ColorModel CM = ColorModel.getRGBdefault();
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                r = CM.getRed(pixeliImagine[i * DIM + j]);
                g = CM.getGreen(pixeliImagine[i * DIM + j]);
                b = CM.getBlue(pixeliImagine[i * DIM + j]);

                float y = (float) (0.299 * r + 0.587 * g + 0.114 * b);
                rd = Math.round(y + saturatie * (r - y));
                gd = Math.round(y + saturatie * (g - y));
                bd = Math.round(y + saturatie * (b - y));
                pixeliImagine[i * DIM + j] = rd << 16 | gd << 8 | bd | 0xff000000;
            }
        }
        return interfata.createImage(new MemoryImageSource(DIM, DIM, pixeliImagine, 0, DIM));
    }

    void box3x3() {
        filtreaza(Y, S, new float[][]{{1, 1, 1}, {1, 1, 1}, {1, 1, 1}}, 9, DIM);
        //9 se foloseste cand avem coeficienti 0
        copiaza(Y, S, DIM - 2);

        filtreaza(I, S, new float[][]{{1, 1, 1}, {1, 1, 1}, {1, 1, 1}}, 9, DIM / 2);
        copiaza(I, S, (DIM / 2) - 2);

        filtreaza(Q, S, new float[][]{{1, 1, 1}, {1, 1, 1}, {1, 1, 1}}, 9, DIM / 2);
        copiaza(Q, S, (DIM / 2) - 2);
    }

    void box5x5() {
        filtreaza(Y, S, new float[][]{{1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}}, 25, DIM);
        //9 se foloseste cand avem coeficienti 0
        copiaza(Y, S, DIM - 2);

        filtreaza(I, S, new float[][]{{1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}}, 25, DIM / 2);
        copiaza(I, S, (DIM / 2) - 2);

        filtreaza(Q, S, new float[][]{{1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}}, 25, DIM / 2);
        copiaza(Q, S, (DIM / 2) - 2);
    }

    boolean negru(int i, int j) {
        System.out.println(Y[i][j] < 50);
        return Y[i][j] < 50;
    }

    void filtreaza(float[][] orig, float[][] dest, float[][] w, float suma, int n) {
        //w filtru
        //calculeaza pozitia elementului aflat in centrul filtrului
        int l = (w.length - 1) / 2;
        int b = (w[0].length - 1) / 2;//tabloul e dreptunghiular
        //elementul cnetral al filtrului se gaseste pe linia l ci coloana h
        for (int i = l; i < n - l; i++) //nu filtre primele  si ultimile l randuri
        {
            for (int j = b; j < n - b; j++) {//nu filtre primele  si ultimile h coloane
                //i si j reprezinta pozitia filtrului pe care il filtram
                dest[i][j] = fil(orig, w, i, j, n) / suma;
//                if(!abc.negru(i,j)){
//                    dest[i][j] = fil(orig, w, i, j, n) / suma; 
//                } 
//                else {
//                    dest[i][j] = orig[i][j];
//                    
//                } //fil calculeaza suma de produse 
                //System.out.println(dest[i][j]);
                //evit sa pun inca 2 bucle
            }
        }
    }

    float fil(float[][] orig, float[][] w, int lin, int col, int n) {
        int l = (w.length - 1) / 2;
        int h = (w[0].length - 1) / 2;
        float rez = 0;
        for (int i = -l; i <= l; i++) {
            for (int j = -h; j <= h; j++) {
                rez += orig[lin + i][col + j] * w[i + l][j + h];
                //System.out.println(rez);
            }
        }

        return rez;
    }

    void copiaza(float[][] dest, float[][] sursa, int n) {
        for (int i = 1; i < n - 1; i++) {
            for (int j = 1; j < n - 1; j++) {
                dest[i][j] = sursa[i][j];
                //System.arraycopy(sursa[i], 0, dest[i], 0, n);
            }
        }
    }

    void zgomot2() {
        float k = 0.1f;
        //ImageIcon zgomot = Poze.zgomot;
        ImageIcon zgomot = new ImageIcon("zgomot.png");
        int pixeliImagine[] = new int[DIM * DIM];
        PixelGrabber grabber = new PixelGrabber(zgomot.getImage().getSource(), 0, 0, DIM, DIM, pixeliImagine, 0, DIM);
        try {
            grabber.grabPixels();
        } catch (Exception e) {
            System.out.println(e);
            return;
        }
        int r, g, b;
        ColorModel CM = ColorModel.getRGBdefault();
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                r = CM.getRed(pixeliImagine[i * DIM + j]);
                g = CM.getGreen(pixeliImagine[i * DIM + j]);
                b = CM.getBlue(pixeliImagine[i * DIM + j]);
                Y[i][j] += k * aleatorPozNeg() * (0.299f * r + 0.587f * g + 0.114f * b);
                if (i % 2 == 0 && j % 2 == 0) {
                    I[i / 2][j / 2] += k * aleatorPozNeg() * (0.5f * r - 0.2f * g - 0.3f * b);
                    Q[i / 2][j / 2] += k * aleatorPozNeg() * (0.3f * r + 0.4f * g - 0.7f * b);

                }
            }
        }

    }

    int aleatorPozNeg() {
        return Math.random() > 0.5 ? -1 : 1;
    }

    void laplacianHV() {
        filtreaza(Y, S, new float[][]{{0, 1, 0}, {1, -4, 1}, {0, 1, 0}}, 1, DIM);
        copiaza(Y, S, DIM);
    }

    void laplacianHVD() {
        filtreaza(Y, S, new float[][]{{1, 1, 1}, {1, -8, 1}, {1, 1, 1}}, 1, DIM);
        copiaza(Y, S, DIM);
    }

    void accentuareDetalii() {
        float c = 10f;
        filtreaza(Y, S, new float[][]{{-1, -1, -1}, {-1, c, -1}, {-1, -1, -1}}, c - 8, DIM);
        copiaza(Y, S, DIM);
    }

    void accentuareDetaliiSlider(float value) {
        float c;
        if (value >= 50) {
            c = 25f;
        } else {
            c = 1f;
        }

        System.out.println(c);
        filtreaza(Y, S, new float[][]{{-1, -1, -1}, {-1, c, -1}, {-1, -1, -1}}, c - 8, DIM);
        copiaza(Y, S, DIM);
    }

    //nu e gata inca .....
    void sorteaza(float[] tab) {
        int min;
        float t;
        for (int i = 0; i < tab.length - 1; i++) {
            min = 1;
            for (int j = 0; j < tab.length - 1; j++) {

            }
        }
    }

    void median() {
        minimMaximMedianInterval(Y, S, DIM, 3, 3);
        //1 minim
        //2 maxim
        //3=median
        //4 interval
        copiaza(Y, S, DIM);
    }

    void min() {
        minimMaximMedianInterval(Y, S, DIM, 3, 1);
        copiaza(Y, S, DIM);
    }

    void max() {
        minimMaximMedianInterval(Y, S, DIM, 3, 2);
        copiaza(Y, S, DIM);
    }

    void interval() {
        minimMaximMedianInterval(Y, S, DIM, 3, 4);
        copiaza(Y, S, DIM);
    }

    void minimMaximMedianInterval(float[][] orig, float[][] dest, int n, int lat, int tipFil) {
        int l = (lat - 1) / 2;
        for (int i = l; i < n - l; i++) {
            for (int j = l; j < n - l; j++) {
                dest[i][j] = calcMedian(orig, dest, i, j, lat, tipFil);
            }
        }

    }

    float calcMedian(float[][] orig, float[][] dest, int i, int j, int lat, int tipFil) {
        float[] tab = new float[lat * lat];
        int x = 0;
        int l = (lat - 1) / 2;
        for (int k = -l; k <= 1; k++) {
            for (int p = -l; p <= l; p++) {
                tab[x++] = orig[i + k][j + p];//copiaa pixeli
            }                 //ce urmeaza sa fie sortati in tbloul tab
        }
        //sortare tab
        Arrays.sort(tab);
        switch (tipFil) {
            case 1:
                return tab[0];//minim
            case 2:
                return tab[tab.length - 1];//maxim
            case 3:
                return tab[tab.length / 2];//median
            case 4:
                return Math.abs(tab[tab.length - 1] - tab[0]);//interval
        }
        return 0;//pt compilator
    }

    void trasareConturSobel() {
        float prag = 80;
        // prag = 70;
        filtreaza(Y, S, new float[][]{{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}}, 1, DIM);
        filtreaza(Y, S2, new float[][]{{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}}, 1, DIM);

        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (Math.abs(S[i][j]) + Math.abs(S2[i][j]) > prag) {
                    Y[i][j] = 0;
                } else {
                    Y[i][j] = 255;
                }
            }
        }
    }

    void trasareContur1() {
        trasareContur(Y, S, DIM);
        copiaza(Y, S, DIM);
    }

    void trasareContur(float[][] orig, float[][] dest, int n) {

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Interfata.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Interfata.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Interfata.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Interfata.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Interfata().setVisible(true);
            }
        });
    }
}
