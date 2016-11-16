/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vanceaandrei.dip;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import util.Const;

/**
 *
 * @author vancea
 */
public class DigitalImage {

    static final int DIM = 512;

    public float Y[][] = new float[DIM][DIM];
    public float I[][] = new float[DIM / 2][DIM / 2];
    public float Q[][] = new float[DIM / 2][DIM / 2];

    float h[][] = new float[DIM][DIM];
    float s[][] = new float[DIM][DIM];
    float v[][] = new float[DIM][DIM];

    float[][] S = new float[DIM][DIM];
    float[][] S2 = new float[DIM][DIM];
    float[][] Theta = new float[DIM][DIM];
    int[] imagePixels = new int[DIM * DIM];

    JFrame gui;

    DigitalImage(ImageIcon picture, JFrame gui) {
        this.gui = gui;
        separateColors(picture);
    }

    ImageIcon imageIcon() {
        //return new ImageIcon(composeFromHSV());
        return new ImageIcon(composeFromRGB());
    }

    Image composeFromRGB() {
        return gui.createImage(new MemoryImageSource(DIM, DIM, imagePixels, 0, DIM));
    }

    void separateColors(ImageIcon poza) {

        PixelGrabber grabber = new PixelGrabber(poza.getImage().getSource(),
                0, 0, DIM, DIM, imagePixels, 0, DIM);
        //parameters:
        //ImageProducer
        //0,0=upper left corner coordinates
        //DIM, DIM=Height, Width
        //imagePixels=array that contains images pixels
        //0 = offsetul where the first pixel is placed
        //length of a line

        try {
            grabber.grabPixels(); //place all pixels into imagePixels array
        } catch (InterruptedException e) {
            return;
        }
        int r, g, b;
        ColorModel CM = ColorModel.getRGBdefault();
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                r = CM.getRed(imagePixels[i * DIM + j]);
                g = CM.getGreen(imagePixels[i * DIM + j]);
                b = CM.getBlue(imagePixels[i * DIM + j]);

                Y[i][j] = 0.299f * r + 0.587f * g + 0.114f * b;//calculate brightness of the current pixel (Y)
                if (i % 2 == 0 && j % 2 == 0) {
                    I[i / 2][j / 2] = 0;
                    Q[i / 2][j / 2] = 0;
                }
                I[i / 2][j / 2] += (0.5f * r - 0.2f * g - 0.3f * b) / 4;
                Q[i / 2][j / 2] += (0.3f * r + 0.4f * g - 0.7f * b) / 4;
            }
        }
    }

    void show(JLabel label, boolean blackAndWhite) {//afiseaza imaginea in eticheta
        label.setIcon(new ImageIcon(composeColors(blackAndWhite)));
    }

    void showHSV() {
        for (int i = 0; i < DIM; i++) {
            System.out.println("");
            for (int j = 0; j < DIM; j++) {
                System.out.printf("%.3f ", s[i][j]);
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

    void minusB() {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (v[i][j] < 0.99) {
                    v[i][j] += 0.01;
                } else {
                    v[i][j] = 1;
                }
            }
        }

    }

    void convertToHSV(ImageIcon poza) {
        PixelGrabber grabber = new PixelGrabber(poza.getImage().getSource(),
                0, 0, DIM, DIM, imagePixels, 0, DIM);

        try {
            grabber.grabPixels();
        } catch (Exception e) {
            return;
        }
        int r, g, b;
        ColorModel cm = ColorModel.getRGBdefault();
        float[] hsv = new float[3];
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                r = cm.getRed(imagePixels[i * DIM + j]);
                g = cm.getGreen(imagePixels[i * DIM + j]);
                b = cm.getBlue(imagePixels[i * DIM + j]);
                hsv = Color.RGBtoHSB(r, g, b, hsv);
                h[i][j] = hsv[0];
                s[i][j] = hsv[1];
                v[i][j] = hsv[2];
            }
        }
    }

    Image composeFromHSV() {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                imagePixels[i * DIM + j] = Color.HSBtoRGB(h[i][j], s[i][j], v[i][j]);
            }
        }
        return gui.createImage(new MemoryImageSource(DIM, DIM, imagePixels, 0, DIM));
    }

    Image composeColors(boolean blackAndWhite) {
        int r, g, b;
        float pY, pI, pQ;
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                pY = Y[i][j];
                if (blackAndWhite) {
                    pI = pQ = 0;
                } else {
                    pI = I[i / 2][j / 2];
                    pQ = Q[i / 2][j / 2];
                }
                r = fixEnds(Math.round(pY + 1.756f * pI - 0.590 * pQ));
                g = fixEnds(Math.round(pY - 0.937f * pI + 0.564 * pQ));
                b = fixEnds(Math.round(pY + 0.217f * pI - 1.359 * pQ));

                //each pixel is represented on 24 bytes
                imagePixels[i * DIM + j] = 0xff000000; // first 8 represent opacity
                imagePixels[i * DIM + j] |= r << 16;// next 8 represent r component
                imagePixels[i * DIM + j] |= g << 8;// next 8 represent g component
                imagePixels[i * DIM + j] |= b;// last 8 represent b component
            }
        }
        return gui.createImage(new MemoryImageSource(DIM, DIM, imagePixels, 0, DIM));
    }

    int fixEnds(long v) {
        if (v < 0) {
            return 0;
        }
        if (v > 255) {
            return 255;
        }
        return (int) v;
    }

    void thresholding() {
        float k = 170;// set the threshold
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (Y[i][j] > k) {
                    Y[i][j] = 0;//thresholding + negative
                } else {
                    Y[i][j] = 255;
                }
            }
        }
    }

    void outlineBin() {
        calculateErosion(Y, S, DIM);
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (S[i][j] == 0 && Y[i][j] == 0) {
                    Y[i][j] = 255;
                }
            }
        }
    }

    void fill(int mouseY, int mouseX) {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                S[i][j] = 255;//white image
            }
        }
        S[mouseY][mouseX] = 0;

        do {
            copy(S2, S, DIM);
            calcExpansion(S2, S, DIM);
            for (int i = 0; i < DIM; i++) {
                for (int j = 0; j < DIM; j++) {
                    if (Y[i][j] == 0) {
                        S[i][j] = 255;
                    }
                }
            }
        } while (!compare(S, S2, DIM));

        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (S2[i][j] == 0) {
                    Y[i][j] = 0;
                }
            }
        }
    }

    boolean compare(float[][] tab1, float[][] tab2, int n) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tab1[i][j] != tab2[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    void erosion() {
        calculateErosion(Y, S, DIM);
        copy(Y, S, DIM);
    }

    void calculateErosion(float[][] orig, float[][] dest, int n) {
        for (int i = 1; i < n - 1; i++) {
            for (int j = 1; j < n - 1; j++) {
                if (orig[i][j] == 0) {
                    if (orig[i - 1][j] == 255 || orig[i + 1][j] == 255
                            || orig[i][j - 1] == 255 || orig[i][j + 1] == 255) {
                        dest[i][j] = 255;
                    } else {
                        dest[i][j] = 0;
                    }
                } else {
                    dest[i][j] = 255;
                }
            }
        }
    }

    void expansion() {
        calcExpansion(Y, S, DIM);
        copy(Y, S, DIM);
    }

    void calcExpansion(float[][] orig, float[][] dest, int n) {
        DigitalImage.this.fill(dest, n, 255);//clean destination array
        for (int i = 1; i < n - 1; i++) {
            for (int j = 1; j < n - 1; j++) {
                if (orig[i][j] == 0) {
                    dest[i][j] = dest[i - 1][j]
                            = dest[i + 1][j] = dest[i][j - 1] = dest[i][j + 1] = 0;
                }
            }
        }
    }

    void fill(float[][] tab, int n, float val) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tab[i][j] = val;
            }
        }
    }

    void canny(float lowThreshold, float highThreshold) {
        filter(Y, S, new float[][]{{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}}, 1, DIM);
        //s=gx
        filter(Y, S2, new float[][]{{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}}, 1, DIM);
        //s2=gy
        double t;
        for (int i = 1; i < DIM - 1; i++) {
            for (int j = 1; j < DIM - 1; j++) {
                t = Math.atan(S2[i][j] / S[i][j]) * 180 / Math.PI;
                //theta round
                if (t < -67.5) {
                    Theta[i][j] = -90;
                } else if (t < -22.5) {
                    Theta[i][j] = -45;
                } else if (t < 22.5) {
                    Theta[i][j] = 0;
                } else if (t < 67.5) {
                    Theta[i][j] = 45;
                } else {
                    Theta[i][j] = 90;
                }
            }
        }
        for (int i = 1; i < DIM - 1; i++) {
            for (int j = 1; j < DIM - 1; j++) {
                S2[i][j] = (float) Math.sqrt(S[i][j] * S[i][j] + S2[i][j] * S2[i][j]);
            }
        }
        //outline thinning
        while (true) {
            boolean changed = false;
            for (int i = 1; i < DIM - 1; i++) {
                for (int j = 1; j < DIM - 1; j++) {
                    if ((Theta[i][j] == 0 && (S2[i][j] < S2[i][j + 1] || S2[i][j] < S2[i][j - 1]))
                            || (Theta[i][j] == 45 && (S2[i][j] < S2[i - 1][j + 1] || S2[i][j] < S2[i + 1][j - 1]))
                            || (Theta[i][j] == -45 && (S2[i][j] < S2[i + 1][j + 1] || S2[i][j] < S2[i - 1][j - 1]))
                            || ((Theta[i][j] == -90 || Theta[i][j] == 90)
                            && (S2[i][j] < S2[i + 1][j] || S2[i][j] < S2[i - 1][j]) //                       ||
                            //                 (Theta[i][j]==90 && S2[i][j]<S2[i-1][j])
                            )) {
                        S[i][j] = 0;
                        changed = true;
                    } else {
                        S[i][j] = S2[i][j];
                    }
                }
            }
            if (!changed) {
                break;
            }
        }

        //hysteresis thresholding
        for (int i = 1; i < DIM - 1; i++) {
            for (int j = 1; j < DIM - 1; j++) {
                if (S[i][j] > highThreshold) {
                    S2[i][j] = 0;
                } else {
                    S2[i][j] = 255;
                }
            }
        }
        //all neighbours that go over the lower threshold are added to the outline
        for (int i = 1; i < DIM - 1; i++) {
            for (int j = 1; j < DIM - 1; j++) {
                Y[i][j] = S2[i][j];
                if (S2[i][j] == 0) {
                    if (S[i - 1][j] > lowThreshold) { //top
                        Y[i - 1][j] = 0;
                    }
                    if (S[i][j + 1] > lowThreshold) { //right
                        Y[i][j + 1] = 0;
                    }
                    if (S[i][j - 1] > lowThreshold) { //left
                        Y[i][j - 1] = 0;
                    }
                    if (S[i + 1][j] > lowThreshold) { //bottom
                        Y[i + 1][j] = 0;
                    }
                    if (S[i - 1][j - 1] > lowThreshold) { //left top
                        Y[i - 1][j - 1] = 0;
                    }
                    if (S[i - 1][j + 1] > lowThreshold) {  //left bottom
                        Y[i - 1][j + 1] = 0;
                    }
                    if (S[i - 1][j + 1] > lowThreshold) {  //right top
                        Y[i - 1][j + 1] = 0;
                    }
                    if (S[i + 1][j + 1] > lowThreshold) { //right bottom
                        Y[i + 1][j + 1] = 0;
                    }
                }
            }
        }

    }

    void markSobelOutline(float threshold) {
        filter(Y, S, new float[][]{{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}},
                1, DIM);//S will contain horisontal gradients
        filter(Y, S2, new float[][]{{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}},
                1, DIM);//S2 will contain vertical gradients

        //thresholding
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (Math.abs(S[i][j]) + Math.abs(S2[i][j]) > threshold) {
                    Y[i][j] = 0;//if module of the gradient exceedes the threshold the pixel transforms to black
                } else {
                    Y[i][j] = 255;//else it's white
                }
            }
        }
    }

    void markOutline1(int p) {
        markOutline(Y, S, DIM, p);
        copy(Y, S, DIM);
    }

    void markOutline(float[][] orig, float[][] dest, int n, int p) {
        for (int i = 1; i < n - 1; i++) {
            for (int j = 1; j < n - 1; j++) {
                if (insideOutline(orig, i, j, p)) {
                    dest[i][j] = 0;
                } else {
                    dest[i][j] = 255;
                }
            }
        }
    }

    void markOutline2(float p) {
        markOutlineE(Y, S, DIM, p);
        copy(Y, S, DIM);
    }

    void markOutlineE(float[][] orig, float[][] dest, int n, float p) {
        for (int i = 1; i < n - 1; i++) {
            for (int j = 1; j < n - 1; j++) {
                if (outsideOutline(orig, i, j, p)) {
                    dest[i][j] = 0;
                } else {
                    dest[i][j] = 255;
                }
            }
        }
    }

    boolean insideOutline(float[][] tab, int i, int j, int p) {
        float x = tab[i][j];//pixel covered by the central element
        //darker pixel ==> inside outline
        return tab[i - 1][j - 1] - x > p
                || tab[i - 1][j] - x > p
                || tab[i - 1][j + 1] - x > p
                || tab[i][j - 1] - x > p
                || tab[i + 1][j + 1] - x > p
                || tab[i][j + 1] - x > p
                || tab[i + 1][j - 1] - x > p
                || tab[i + 1][j] - x > p;
    }

    boolean outsideOutline(float[][] tab, int i, int j, float p) {
        float x = tab[i][j];//pixel covered by the central element
        //brighter pixel ==> outside outline
        return x - tab[i - 1][j - 1] > p
                || x - tab[i - 1][j] > p
                || x - tab[i - 1][j + 1] > p
                || x - tab[i][j - 1] > p
                || x - tab[i + 1][j + 1] > p
                || x - tab[i][j + 1] > p
                || x - tab[i + 1][j - 1] > p
                || x - tab[i + 1][j] > p;
    }

    void sharpenDetails(float c) {

        filter(Y, S, new float[][]{{-1, -1, -1}, {-1, c, -1}, {-1, -1, -1}},
                c - 8, DIM);
        copy(Y, S, DIM);
    }

    void sort(float[] tab) {
        int min;
        float t;
        for (int i = 0; i < tab.length - 1; i++) {
            min = i;
            for (int j = i + 1; j < tab.length; j++) {
                if (tab[j] < tab[min]) {
                    min = j;
                }
            }
            t = tab[i];
            tab[i] = tab[min];
            tab[min] = t;
        }
    }

    float calculateMedian(float[][] orig, float[][] dest,
            int i, int j, int lat, int tipFil) {
        float[] tab = new float[lat * lat];
        int x = 0;
        int l = (lat - 1) / 2;
        for (int k = -l; k <= l; k++) {
            for (int p = -l; p <= l; p++) {
                tab[x++] = orig[i + k][j + p];
            }
        }
        Arrays.sort(tab);
        switch (tipFil) {
            case 1:
                return tab[0];//min
            case 2:
                return tab[tab.length - 1];//max
            case 3:
                return tab[tab.length / 2];//median
            case 4:
                return Math.abs(tab[tab.length - 1] - tab[0]);//interval
        }
        return 0;
    }

    void minMaxMedianInterval(float[][] orig, float[][] dest,
            int n, int lat, int tipFil) {
        int l = (lat - 1) / 2;
        for (int i = l; i < n - l; i++) {
            for (int j = l; j < n - l; j++) {
                dest[i][j] = calculateMedian(orig, dest, i, j, lat, tipFil);
            }
        }
    }

    void median() {
        minMaxMedianInterval(Y, S, DIM, 3, 3);
        fixBorders(S, DIM);
        copy(Y, S, DIM);
    }

    void max() {
        minMaxMedianInterval(Y, S, DIM, 3, 2);
        fixBorders(S, DIM);
        copy(Y, S, DIM);
    }

    void min() {
        minMaxMedianInterval(Y, S, DIM, 3, 1);
        fixBorders(S, DIM);
        copy(Y, S, DIM);
    }

    void interval() {
        minMaxMedianInterval(Y, S, DIM, 3, 4);
        fixBorders(S, DIM);
        copy(Y, S, DIM);
    }

    void filter(float[][] orig, float[][] dest,
            float[][] w, float suma, int n) {
        // w = filter
        //calculate position of the center element of the filter
        int l = (w.length - 1) / 2;
        int h = (w[0].length - 1) / 2;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dest[i][j] = fil(orig, w, i, j, n) / suma;
            }
        }
    }

    float fil(float[][] orig, float[][] w, int lin, int col, int n) {
        int l = (w.length - 1) / 2;
        int h = (w[0].length - 1) / 2;
        float rez = 0;
        int lc, cc;
        for (int i = -l; i <= l; i++) {
            for (int j = -h; j <= h; j++) {
                lc = lin + i;
                cc = col + j;
                if (lc < 0) {
                    lc = 0;
                } else if (lc >= n) {
                    lc = n - 1;
                }
                if (cc < 0) {
                    cc = 0;
                } else if (cc >= n) {
                    cc = n - 1;
                }
                rez += orig[lc][cc] * w[i + l][j + h];
            }
        }
        return rez;
    }

    void copy(float[][] dest, float[][] sursa, int n) {
        //--------------------------------------------
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dest[i][j] = sursa[i][j];
            }
        }

    }
    float[][] filtruBox = {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}};

    void box3x3() {
        filter(Y, S, filtruBox, 9, DIM);
        //param: f, g, h, suma_coef, latura_imaginii
        copy(Y, S, DIM);

        filter(I, S, filtruBox, 9, DIM / 2);
        copy(I, S, DIM / 2);
        filter(Q, S, filtruBox, 9, DIM / 2);
        copy(Q, S, DIM / 2);
    }

    void box5x5() {
        filter(Y, S, new float[][]{{1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}}, 25, DIM);
        copy(Y, S, DIM);

        filter(I, S, new float[][]{{1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}}, 25, DIM / 2);
        copy(I, S, DIM / 2);
        filter(Q, S, new float[][]{{1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}}, 25, DIM / 2);
        copy(Q, S, DIM / 2);
    }

    void box7x7() {
        filter(Y, S, new float[][]{{1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1}}, 49, DIM);
        copy(Y, S, DIM);

        filter(I, S, new float[][]{{1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1}}, 49, DIM / 2);
        copy(I, S, DIM / 2);
        filter(Q, S, new float[][]{{1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1}}, 49, DIM / 2);
        copy(Q, S, DIM / 2);
    }

    void gaussian() {
        filter(Y, S, new float[][]{{1, 2, 1}, {2, 4, 2}, {1, 2, 1}}, 16, DIM);
        copy(Y, S, DIM);
        filter(I, S, new float[][]{{1, 2, 1}, {2, 4, 2}, {1, 2, 1}}, 16, DIM / 2);
        copy(I, S, DIM / 2);
        filter(Q, S, new float[][]{{1, 2, 1}, {2, 4, 2}, {1, 2, 1}}, 16, DIM / 2);
        copy(Q, S, DIM / 2);
    }

    void noise2() {
        //add/reduce noise
        float k = 0.1f;
        ImageIcon zgomot = Const.picture;
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

    void noise() {
        float k = 0.2f;
        ImageIcon zgomot = new ImageIcon("zgomot.gif");
        int pixeliImagine[] = new int[DIM * DIM];
        PixelGrabber grabber = new PixelGrabber(zgomot.getImage().getSource(), 0, 0, DIM, DIM, pixeliImagine, 0, DIM);
        try {
            grabber.grabPixels();
        } catch (Exception e) {
            System.out.println(e);
            return;
        }
        int r, g, b;
        //add noise to Y
        ColorModel cm = ColorModel.getRGBdefault();
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                r = cm.getRed(pixeliImagine[i * DIM + j]);
                g = cm.getGreen(pixeliImagine[i * DIM + j]);
                b = cm.getBlue(pixeliImagine[i * DIM + j]);
                Y[i][j] += k * (0.299f * r + 0.587f * g + 0.114f * b);
            }
        }

    }

    void laplacianHV() {
        filter(Y, S, new float[][]{{0, 1, 0}, {1, -4, 1}, {0, 1, 0}}, 1, DIM);
        copy(Y, S, DIM);
    }

    void laplacianHVD() {
        filter(Y, S, new float[][]{{1, 1, 1}, {1, -8, 1}, {1, 1, 1}}, 1, DIM);
        copy(Y, S, DIM);
    }

    void expandAll() {
        expand(Y, DIM);
        expand(I, DIM / 2);
        expand(Q, DIM / 2);
    }

    void expand(float[][] tab, int DIM) {
        for (int i = 0; i < DIM / 2; i++) {
            for (int j = 0; j < DIM / 2; j++) {
                S[2 * i][2 * j] = tab[i][j];
            }
        }
        interpolate(S, DIM);
        copy(tab, S, DIM);
    }

    void interpolate(float[][] tab, int n) {
        for (int i = 0; i < n; i += 2) {
            for (int j = 1; j < n - 1; j += 2) {
                tab[i][j] = (tab[i][j - 1] + tab[i][j + 1]) / 2;
            }
        }
        for (int i = 1; i < n - 1; i += 2) {
            for (int j = 0; j < n; j++) {
                tab[i][j] = (tab[i - 1][j] + tab[i + 1][j]) / 2;
            }
        }

    }

    void normalizeHistogram2() {
        normH(Y);
    }

    void normH(float[][] tab) {
        //normalize hystogram 2
        int[] shades = new int[256];
        int p, n;
        for (int i = 0; i < DIM; i++)//calculate nr of pixel for each nuance
        {
            for (int j = 0; j < DIM; j++) {
                n = fixEnds(Math.round(tab[i][j]));
                shades[n]++;//position from array represents nuance
            }
        }
        //determine number of pixels with lower intensity
        int dmin = 0;
        for (int i = 0; i < 256; i++)//calculate dmin
        {
            if (shades[i] != 0) {
                dmin = shades[i];
                break;
            }
        }
        int sum = 0;
        for (int i = 0; i < 256; i++) {
            sum += shades[i];
            shades[i] = sum;
        }
        int u;
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                u = fixEnds(Math.round(tab[i][j]));
                tab[i][j] = (shades[u] - dmin) * 255 / (DIM * DIM - dmin);
            }
        }
    }

    void normalizeHistogram() {
        //normalizare hystogram
        float[] weights = new float[256];
        float sum = 0;
        for (int i = 0; i < 256; i++) {
            sum += pixels[i];//sum of pixels darker than i
            weights[i] = sum / DIM / DIM;
        }
        int p;
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                p = fixEnds(Math.round(Y[i][j]));
                //p=initial brightness
                Y[i][j] *= weights[p];
            }
        }
    }
    int[] pixels;

    void histogram(JPanel jPanel2) {
        //mark hystogram
        pixels = new int[256];//position represents nuance
        int p, zero;
        //count pixels for each nuance
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                p = fixEnds(Math.round(Y[i][j]));
                pixels[p]++;
            }
        }
        Graphics g = jPanel2.getGraphics();
        //clean screen
        g.setColor(Color.white);
        g.fillRect(0, 0, jPanel2.getWidth(), jPanel2.getHeight());
        //paint black
        g.setColor(Color.black);
        zero = jPanel2.getHeight() - 10;
        for (int i = 0; i < 256; i++) {
            p = pixels[i];
            g.drawLine(i, zero, i, zero - p / 10);
            //mark a vertical line,
            //the size of the line equals the number of pixels of same nuance
        }
    }

    void adjustBrightness(int k) {
        brightness(Y, Y, DIM, k);
    }

    void brightness(float[][] dest, float[][] orig, int n, float a) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dest[i][j] = orig[i][j] + a;
            }
        }
    }

    void negative() {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                Y[i][j] = 255 - Y[i][j];
            }
        }
    }

    void threshold(int k) {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                Y[i][j] = Y[i][j] > k ? 255 : 0;
            }
        }
    }

    void contrastSin() {
        contrast2(Y, Y, DIM);
        //contrast2(I,I,DIM/2);
        //contrast2(Q,Q,DIM/2);
    }

    void contrast2(float[][] dest, float[][] orig, int n) {
        float MAX = 255;
        float a = -10;
        float x;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                x = orig[i][j];
                dest[i][j] = x - (float) (a * Math.sin(x * 2 * Math.PI / MAX));//
            }
        }

    }

    void autoContrast() {
        float max = 0, min = 255;
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (Y[i][j] < min) {
                    min = Y[i][j];
                }
                if (Y[i][j] > max) {
                    max = Y[i][j];
                }
            }
        }
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                Y[i][j] = (Y[i][j] - min) * 255 / (max - min);
            }
        }
    }

    void contrastLin() {
        contrast(Y, Y, DIM);
        //contrast(I,I,DIM/2);
        //contrast(Q,Q,DIM/2);
    }

    void contrast(float[][] dest, float[][] orig, int n) {
        float a = 10;
        float b = 245;
        float fa = a - 9;
        float fb = b + 9;
        float x, y;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                x = orig[i][j];
                if (x < a) {
                    y = fa / a * x;
                } else if (x < b) {
                    y = (fb - fa) * (x - a) / (b - a) + fa;
                } else {
                    y = (255 - fb) * (x - b) / (255 - b) + fb;
                }
                dest[i][j] = y;
            }
        }
    }

    private void fixBorders(float[][] dest, int n) {
        for (int i = 0; i < n; i++) {
            dest[i][0] = dest[i][1]; //left border
            dest[0][i] = dest[1][i]; //top border
            dest[n - 1][i] = dest[n - 2][i]; //bot border
            dest[i][n - 1] = dest[i][n - 2];
        }

        //fix image corners
        dest[0][0] = dest[1][1]; //top left
        dest[0][n - 1] = dest[1][n - 2]; //top right
        dest[n - 1][0] = dest[n - 2][2]; //bot left
        dest[n - 1][n - 1] = dest[n - 2][n - 2]; //bot right
    }

    void expandBinary() {
        calcExpansion(Y,S,DIM);
        copy(Y, S, DIM);
    }
}
