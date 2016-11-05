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

/**
 *
 * @author ovidiu
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
        separateColors(picture);//realizeaza conversia din RGB in YC1C2
    }

    ImageIcon imageIcon() {
        //return new ImageIcon(compuneDinHSV());
        return new ImageIcon(composeFromRGB());
    }

    Image composeFromRGB() {
        return gui.createImage(new MemoryImageSource(DIM, DIM, imagePixels, 0, DIM));
    }

    void separateColors(ImageIcon poza) {
        //--------------------------------
        //plaseaza pixelii imaginii in tabloul pixeliimagine
        //converte?te imaginea �ntr-un spa?iu de culoare de tip luminan?a - crominan?a 
        //?i plaseaza cele 3 componente rezultate in tablourile Y, I ?i Q

        PixelGrabber grabber = new PixelGrabber(poza.getImage().getSource(),
                0, 0, DIM, DIM, imagePixels, 0, DIM);
        //argumente:
        //ImageProducer
        //0,0=coordonatele coltului din stanga sus
        //DIM, DIM=latime, inaltime
        //pixeliimagine=tabloul in care se plaseaza datele imaginii
        //0 = offsetul la care se plaseaza primul pixel
        //lungimea unei linii (tabloul nu e bidimensional)

        try {
            grabber.grabPixels();//plaseaza pixelii imaginii �n tabloul pixeliimagine
        } catch (Exception e) {
            return;
        }
        int r, g, b;
        ColorModel CM = ColorModel.getRGBdefault();
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                r = CM.getRed(imagePixels[i * DIM + j]);
                g = CM.getGreen(imagePixels[i * DIM + j]);
                b = CM.getBlue(imagePixels[i * DIM + j]);
                //r,g,b reprezinta componentele cromatice ale pixelului curent
                Y[i][j] = 0.299f * r + 0.587f * g + 0.114f * b;//calculam luminozitatea pixelului curent (Y)
                //---crominantele sunt pastrate la rezolutie injumatatit
                //---fiecare element al tabloului contine media a 4 pixeli invecinati
                if (i % 2 == 0 && j % 2 == 0) {
                    I[i / 2][j / 2] = 0;
                    Q[i / 2][j / 2] = 0;
                }
                //calculeaza media grupului de 4 pixeli �nvecina?i
                I[i / 2][j / 2] += (0.5f * r - 0.2f * g - 0.3f * b) / 4;//I[i/2][j/2]=0;//poza alb - negru
                Q[i / 2][j / 2] += (0.3f * r + 0.4f * g - 0.7f * b) / 4;//Q[i/2][j/2]=0;
                // matricea de transformare
                // se folose?te un spa?iu de culoare original
                //   0.299    +0.587  +0.114
                //   0.5      -0.2    -0.3 suma liniei=0
                //   0.3      +0.4    -0.7 suma liniei=0

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
        //int pixeliImagine[]=new int[DIM*DIM];
        PixelGrabber grabber = new PixelGrabber(poza.getImage().getSource(),
                0, 0, DIM, DIM, imagePixels, 0, DIM);
        //argumente:
        //ImageProducer
        //0,0=coordonatele coltului din stanga sus
        //DIM, DIM=latime, inaltime
        //pixeliimagine=tabloul in care se plaseaza datele imaginii
        //0 = offsetul la care se plaseaza primul pixel
        //lungimea unei linii (tabloul nu e bidimensional)

        try {
            grabber.grabPixels();//plaseaza pixelii imaginii �n tabloul pixeliimagine
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

    Image compuneDinHSV() {
        //int r,g,b;
        //float pY, pI, pQ;
        //int pixeliImagine[]=new int[DIM*DIM];
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                imagePixels[i * DIM + j] = Color.HSBtoRGB(h[i][j], s[i][j], v[i][j]);
            }
        }
        return gui.createImage(new MemoryImageSource(DIM, DIM, imagePixels, 0, DIM));
    }

    Image composeColors(boolean blackAndWhite) {
        //-------------------
        int r, g, b;
        float pY, pI, pQ;
        //int pixeliImagine[]=new int[DIM*DIM];
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                pY = Y[i][j];
                if (blackAndWhite) {
                    pI = pQ = 0;
                } else {
                    pI = I[i / 2][j / 2];
                    pQ = Q[i / 2][j / 2];
                }
                //---YC'C"
                //pI=pY=0;
                r = fixEnds(Math.round(pY + 1.756f * pI - 0.590 * pQ));
                g = fixEnds(Math.round(pY - 0.937f * pI + 0.564 * pQ));
                b = fixEnds(Math.round(pY + 0.217f * pI - 1.359 * pQ));
                //g=b=0; //afiseaza doar r
                //r=b=0; //afiseaza doar g
                //r=g=0; //afiseaza doar b
                //inversa matricei de transformare
                //  1    +1.756  -0.590
                //  1    -0.937  +0.564
                //  1    +0.217  -1.359

                //fiecare pixel e reprezentat pe 24 de bi?i
                imagePixels[i * DIM + j] = 0xff000000; // primii 8 bi?i reprezinta opacitatea
                imagePixels[i * DIM + j] |= r << 16;//urmatorii 8 reprezinta componenta r
                imagePixels[i * DIM + j] |= g << 8;//urmatorii 8 reprezinta componenta g
                imagePixels[i * DIM + j] |= b;//urmatorii 8 reprezinta componenta b

                //pixeliImagine[i*DIM+j]=r<<16 | g<<8 | b | 0xff000000;
                //fiecare pixel e reprezentat pe 24 de biti
                //primii 8 reprezinta opacitatea
                //urmatorii 8 reprezinta componenta r
                //urmatorii 8 g
                //ultimii 8 b
            }
        }
        //sintetizeaza imaginea pe baza datelor din tabloul pixeliImagine
        return gui.createImage(new MemoryImageSource(DIM, DIM, imagePixels, 0, DIM));
        //apel metoda a clasei JFrame
        //latime, inaltime, pixeli, offset, lungimea unui rand de pixeli
        //tema de casa
        //modificati aplica?ia astfel inc�t sa foloseasca spa?iile de culoare
        //YIQ, YCbCr ?i YUV
        //afi?a?i pe r�nd c�te una din componentele spectrale.
    }

    int fixEnds(long v) {
        //---corecteaza eventualele erori la capete
//         return (int)v;  
        if (v < 0) {
            return 0;
        }
        if (v > 255) {
            return 255;
        }
        return (int) v;
    }

    void thresholding() {
        float k = 170;//stabileste pragul
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (Y[i][j] > k) {
                    Y[i][j] = 0;//binarizare + negativare
                } else {
                    Y[i][j] = 255;
                }
            }
        }
    }

    void outlineBin() {
        calculateErosion(Y, S, DIM);
        //scadem imaginea erodata din original
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (S[i][j] == 0 && Y[i][j] == 0)//anulam pixelii din interiorul contururilor
                {
                    Y[i][j] = 255;
                }
            }
        }
        //pixelii care sunt negri ?i �n imaginea originala ?i in cea erodata
        //se transforma in albi
    }

    void fill(int mouseY, int mouseX) {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                S[i][j] = 255;//imagine alba
            }
        }
        S[mouseY][mouseX] = 0;//un pixel negru in regiune

        do {
            copy(S2, S, DIM);
            calcExpansion(S2, S, DIM);
            //stergem pixelii de pe contur
            for (int i = 0; i < DIM; i++) {
                for (int j = 0; j < DIM; j++) {
                    if (Y[i][j] == 0)//daca suntem pe contur
                    {
                        S[i][j] = 255;//?terge conturul (�l traseaza cu alb)
                    }
                }
            }
        } while (!compare(S, S2, DIM));

        //calculam "suma" cu imaginea originala
        //adaugam conturul umplut la imaginea originala
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
        calculateErosion(Y, S, DIM);//plaseaza rezultatul in S
        copy(Y, S, DIM);
    }

    void calculateErosion(float[][] orig, float[][] dest, int n) {
        for (int i = 1; i < n - 1; i++) {
            for (int j = 1; j < n - 1; j++) {
                if (orig[i][j] == 0)//origine=negru
                {
                    if (orig[i - 1][j] == 255 || orig[i + 1][j] == 255
                            || orig[i][j - 1] == 255 || orig[i][j + 1] == 255) //exista un vecin alb
                    {
                        dest[i][j] = 255;//origine=alb
                    } else {
                        dest[i][j] = 0;
                    }
                } else {
                    dest[i][j] = 255;//zonele albe raman albe
                }
            }
        }
    }

    void expansion() {
        calcExpansion(Y, S, DIM);
        copy(Y, S, DIM);
    }

    void calcExpansion(float[][] orig, float[][] dest, int n) {
        DigitalImage.this.fill(dest, n, 255);//curata tabloul destinatie
        for (int i = 1; i < n - 1; i++) {
            for (int j = 1; j < n - 1; j++) {
                if (orig[i][j] == 0)//orig elem = negru
                {
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

    void canny() {
        float pragj = 5, prags = 60;
        filtrate(Y, S, new float[][]{{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}}, 1, DIM);
        //s=gx
        filtrate(Y, S2, new float[][]{{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}}, 1, DIM);
        //s2=gy
        double t;
        for (int i = 1; i < DIM - 1; i++) {
            for (int j = 1; j < DIM - 1; j++) {
                t = Math.atan(S2[i][j] / S[i][j]) * 180 / Math.PI;
                //rotunjite theta
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
        //plasam in S2 modulul gradientului
        for (int i = 1; i < DIM - 1; i++) {
            for (int j = 1; j < DIM - 1; j++) {
                S2[i][j]
                        = (float) Math.sqrt(S[i][j] * S[i][j] + S2[i][j] * S2[i][j]);
            }
        }
        //urmeaza sub?ierea conturului
        //punem rezultatul �n S
        for (int i = 1; i < DIM - 1; i++) {
            for (int j = 1; j < DIM - 1; j++) {
                if ((Theta[i][j] == 0 && (S2[i][j] < S2[i][j + 1] || S2[i][j] < S2[i][j - 1]))
                        || (Theta[i][j] == 45 && (S2[i][j] < S2[i - 1][j + 1] || S2[i][j] < S2[i + 1][j - 1]))
                        || (Theta[i][j] == -45 && (S2[i][j] < S2[i + 1][j + 1] || S2[i][j] < S2[i - 1][j - 1]))
                        || ((Theta[i][j] == -90 || Theta[i][j] == 90)
                        && (S2[i][j] < S2[i + 1][j] || S2[i][j] < S2[i - 1][j]) //asa merge mai prost(Theta[i][j]==-90 && S2[i][j]<S2[i+1][j])
                        //                       ||
                        //                 (Theta[i][j]==90 && S2[i][j]<S2[i-1][j])
                        )) {
                    S[i][j] = 0;
                } //daca gasesc un vecin mai important, sterg pixelul de pe contur
                else {
                    S[i][j] = S2[i][j];//toti vecinii au gradient mai mic
                }
            }
        }

        //binarizare cu histerezis
        //punem rezultatul �n S2
        //toti pixelii pt care gradientul depaseste pragul de sus sunt adaugati
        //la contur
        for (int i = 1; i < DIM - 1; i++) {
            for (int j = 1; j < DIM - 1; j++) {
                if (S[i][j] > prags) {
                    S2[i][j] = 0;
                } else {
                    S2[i][j] = 255;
                }
            }
        }
        //toti vecinii pt care gradientul depaseste pragul de jos sunt adaugati
        //la contur
        for (int i = 1; i < DIM - 1; i++) {
            for (int j = 1; j < DIM - 1; j++) {
                Y[i][j] = S2[i][j];
                if (S2[i][j] == 0) {
                    if (S[i - 1][j] > pragj) {
                        Y[i - 1][j] = 0;
                    }
                    if (S[i][j + 1] > pragj) {
                        Y[i][j + 1] = 0;
                    }
                    if (S[i][j - 1] > pragj) {
                        Y[i][j - 1] = 0;
                    }
                    if (S[i + 1][j] > pragj) {
                        Y[i + 1][j] = 0;
                    }
                }//tema de pus si ceilalti 4 vecini
            }
        }

    }

    void markSobelOutline() {
        float threshold = 80;
        filtrate(Y, S, new float[][]{{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}},
                1, DIM);//S va contine gradientii orizontali
        filtrate(Y, S2, new float[][]{{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}},
                1, DIM);//S2 va contine gradientii verticali

        //urmeaza binarizarea care ?ine cont de modului gradientului
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (Math.abs(S[i][j]) + Math.abs(S2[i][j]) > threshold) {
                    Y[i][j] = 0;//modulul gradientului este peste prag
                } //pixel = negru
                else {
                    Y[i][j] = 255;//pixel=alb
                }
            }
        }
    }

    void markOutline1() {
        markOutline(Y, S, DIM);
        copy(Y, S, DIM);
    }

    void markOutline(float[][] orig, float[][] dest, int n) {
        for (int i = 1; i < n - 1; i++) {
            for (int j = 1; j < n - 1; j++) {
                if (insideOutline(orig, i, j)) {
                    dest[i][j] = 0;
                } else {
                    dest[i][j] = 255;
                }
            }
        }
    }

    void markOutline2() {
        markOutlineE(Y, S, DIM);
        copy(Y, S, DIM);
    }

    void markOutlineE(float[][] orig, float[][] dest, int n) {
        for (int i = 1; i < n - 1; i++) {
            for (int j = 1; j < n - 1; j++) {
                if (peConturExt(orig, i, j)) {
                    dest[i][j] = 0;
                } else {
                    dest[i][j] = 255;
                }
            }
        }
    }

    boolean insideOutline(float[][] tab, int i, int j) {
        float p = 12;//prag determinare contur
        float x = tab[i][j];//pixelul acoperit de elementul central
        //pixel central mai intunecat==> contuir interior
        return tab[i - 1][j - 1] - x > p
                || tab[i - 1][j] - x > p
                || tab[i - 1][j + 1] - x > p
                || tab[i][j - 1] - x > p
                || tab[i + 1][j + 1] - x > p
                || tab[i][j + 1] - x > p
                || tab[i + 1][j - 1] - x > p
                || tab[i + 1][j] - x > p;
    }

    boolean peConturExt(float[][] tab, int i, int j) {
        float p = 12;//prag determinare contur
        float x = tab[i][j];//pixelul acoperit de elementul central
        //pixel central mai luminos==> contur exterior
        return x - tab[i - 1][j - 1] > p
                || x - tab[i - 1][j] > p
                || x - tab[i - 1][j + 1] > p
                || x - tab[i][j - 1] > p
                || x - tab[i + 1][j + 1] > p
                || x - tab[i][j + 1] > p
                || x - tab[i + 1][j - 1] > p
                || x - tab[i + 1][j] > p;
    }

    void sharpenDetails() {
        //c>8
        //c=8 --> Filtrul trece sus
        //c mare --> imagine nemodificata
        //c apropiat de 8 --> accentuare detalii
        float c = 10f;
        filtrate(Y, S, new float[][]{{-1, -1, -1}, {-1, c, -1}, {-1, -1, -1}},
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
                tab[x++] = orig[i + k][j + p];//copiaza pixelii 
            }                //ce urmeaza sa fie sortati in tabloul tab
        }        //sorteaza(tab);
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

    void minimMaximMedianInterval(float[][] orig, float[][] dest,
            int n, int lat, int tipFil) {
        int l = (lat - 1) / 2;
        for (int i = l; i < n - l; i++) {
            for (int j = l; j < n - l; j++) {
                dest[i][j] = calculateMedian(orig, dest, i, j, lat, tipFil);
            }
        }
    }

    void median() {
        minimMaximMedianInterval(Y, S, DIM, 3, 3);
        //3=dimensiunea filtrului (3x3)
        //1=minim
        //2=maxim
        //3=median
        //4=interval
        copy(Y, S, DIM);
    }

    void max() {
        minimMaximMedianInterval(Y, S, DIM, 3, 2);
        copy(Y, S, DIM);
    }

    void min() {
        minimMaximMedianInterval(Y, S, DIM, 3, 1);
        copy(Y, S, DIM);
    }

    void interval() {
        minimMaximMedianInterval(Y, S, DIM, 3, 4);
        copy(Y, S, DIM);
    }

    void filtrate(float[][] orig, float[][] dest,
            float[][] w, float suma, int n) {
        //w filtrul
        //calculez pozitia elementului aflat in centrul filtrului
        int l = (w.length - 1) / 2;
        int h = (w[0].length - 1) / 2;//tabloul e dreptunghiular
        //elementul central al filtrului se gaseste pe linia l si coloana h
        for (int i = 0; i < n; i++)//nu filtram primele si ultimele l randuri
        {
            for (int j = 0; j < n; j++)//nu filtram primele si ultimele h coloane
            //i,j reprezinta pozitia pixelului pe care il filtram
            {
                dest[i][j] = fil(orig, w, i, j, n) / suma;//fil calculeaza suma de produse
            }                                               //evit sa pun �nca 2 bucle
        }
    }

    float fil(float[][] orig, float[][] w, int lin, int col, int n) {
        int l = (w.length - 1) / 2;
        int h = (w[0].length - 1) / 2;//tabloul e dreptunghiular
        //elementul central al filtrului se gaseste pe linia l si coloana h
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
        filtrate(Y, S, filtruBox, 9, DIM);
        //param: f, g, h, suma_coef, latura_imaginii
        copy(Y, S, DIM);

        filtrate(I, S, filtruBox, 9, DIM / 2);
        copy(I, S, DIM / 2);
        filtrate(Q, S, filtruBox, 9, DIM / 2);
        copy(Q, S, DIM / 2);
    }

    void box5x5() {
        filtrate(Y, S, new float[][]{{1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}}, 25, DIM);
        copy(Y, S, DIM);

        filtrate(I, S, new float[][]{{1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}}, 25, DIM / 2);
        copy(I, S, DIM / 2);
        filtrate(Q, S, new float[][]{{1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1}, {1, 1, 1, 1, 1}}, 25, DIM / 2);
        copy(Q, S, DIM / 2);
    }

    void gaussian() {
        filtrate(Y, S, new float[][]{{1, 2, 1}, {2, 4, 2}, {1, 2, 1}}, 16, DIM);
        copy(Y, S, DIM);
        filtrate(I, S, new float[][]{{1, 2, 1}, {2, 4, 2}, {1, 2, 1}}, 16, DIM / 2);
        copy(I, S, DIM / 2);
        filtrate(Q, S, new float[][]{{1, 2, 1}, {2, 4, 2}, {1, 2, 1}}, 16, DIM / 2);
        copy(Q, S, DIM / 2);
    }

    void noise2() {
        //adauga/scade zgomot
        float k = 0.1f;
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
        //adauga zgomotul la Y
        ColorModel cm = ColorModel.getRGBdefault();
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                r = cm.getRed(pixeliImagine[i * DIM + j]);
                g = cm.getGreen(pixeliImagine[i * DIM + j]);
                b = cm.getBlue(pixeliImagine[i * DIM + j]);
                Y[i][j] += k * (0.299f * r + 0.587f * g + 0.114f * b);
                //adaug la Y luminozitatea zgomotului*k
            }
        }

    }

    void laplacianHV() {
        filtrate(Y, S, new float[][]{{0, 1, 0}, {1, -4, 1}, {0, 1, 0}}, 1, DIM);
        copy(Y, S, DIM);
    }

    void laplacianHVD() {
        filtrate(Y, S, new float[][]{{1, 1, 1}, {1, -8, 1}, {1, 1, 1}}, 1, DIM);
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
        //pe coloane
        for (int i = 0; i < n; i += 2)//trateaza doar liniile pare
        {
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

    void normalizeHistogram2() {
        normH(Y);
    }

    void normH(float[][] tab) {
        //Normalizare histograma 2
        int[] shades = new int[256];
        int p, n;
        for (int i = 0; i < DIM; i++)//caculez nr de pixeli de fiecare nuanta
        {
            for (int j = 0; j < DIM; j++) {
                n = fixEnds(Math.round(tab[i][j]));
                shades[n]++;//pozitia din tablou reprezinta nuanta
                //valorile din tablou reprezinta distributiile nuantelor (nr pixeli de nuanta n)
            }
        }
        //determin nr de pixeli de intensitate minima
        int dmin = 0;
        for (int i = 0; i < 256; i++)//calculez dmin
        {
            if (shades[i] != 0) {
                dmin = shades[i];//primul element nenul din tablou e dmin
                break;
            }
        }
        int sum = 0;
        for (int i = 0; i < 256; i++) {//calculez distributiile cumulative
            sum += shades[i];
            shades[i] = sum;//nuante[255]=DIM*DIM
        }
        int u;
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                u = fixEnds(Math.round(tab[i][j]));//(int)tab[i][j];
                tab[i][j] = (shades[u] - dmin) * 255 / (DIM * DIM - dmin);
            }
        }
    }

    void normalizeHistogram() {
        //normalizare histograma
        float[] weights = new float[256];
        float sum = 0;
        for (int i = 0; i < 256; i++) {
            sum += pixels[i];//suma pixeli mai intunecati decat i
            //obs trebuie trasata mai intai histograma, pt calcularea tabloului pixeli
            weights[i] = sum / DIM / DIM;
            //calculeaza ponderea pt fiecare nuan?a
        }
        int p;
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                p = fixEnds(Math.round(Y[i][j]));
                //p=luminozitatea initiala
                Y[i][j] *= weights[p];//pozitia in tabloul de ponderi
                //corespunde luminozitatii
            }
        }
    }
    int[] pixels;

    //frecvente nuante, va fi folosit la normalizarea histogramei
    void histogram(JPanel jPanel2) {
        //trasare histograma
        pixels = new int[256];//pozitia reprezinta nuanta
        //tabloul e initializat automat cu 0

//        for(int i=0; i<256; i++)
//            pixeli[i]=0;//initializarea nu e necesara
        //scest tablou contine frecventele de aparitie ale nuantelor
        //pozitia in tablou indica nuanta
        //valoarea elem i -> nr de pixeli de nuanta i
        int p, zero;
        //numara pixelii de fiecare nuan?a
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                p = fixEnds(Math.round(Y[i][j]));
                pixels[p]++;
                //pozitia din tabloul pixeli indica nuanta
            }
        }
        Graphics g = jPanel2.getGraphics();
        //?terge o eventuala histograma existenta
        g.setColor(Color.white);
        g.fillRect(0, 0, jPanel2.getWidth(), jPanel2.getHeight());
        //deseneaza cu negru
        g.setColor(Color.black);
        //stabile?te pozi?ia abscisei
        zero = jPanel2.getHeight() - 10;
        for (int i = 0; i < 256; i++) {
            p = pixels[i];
            g.drawLine(i, zero, i, zero - p / 10);
            //traseaza o linie verticala,
            //lungimea e propor?ionala cu nr de pixeli
            //de intensitate i
        }
    }

    void adjustBrightness(int k) {
        brightness(Y, Y, DIM, k);
    }

    void brightness(float[][] dest, float[][] orig, int n, float a) {
        //---------------------------------------------
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

    void binarizes(int k) {
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
        //---------------------------------------------
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
        //---------------------------------------------
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

}
