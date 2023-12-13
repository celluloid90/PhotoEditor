//
//  ColorUtils.cpp
//  RGB-ColorDifference
//
//  Created by Mohamed Shahawy on 4/22/16.
//  Copyright © 2016 Mohamed Shahawy. All rights reserved.
//

#include "ColorUtils.h"
#include <cmath>

ColorUtils::xyzColor ColorUtils::rgbToXyz(ColorUtils::rgbColor c) {
    float x, y, z, r, g, b;

    r = c.r / 255.0;
    g = c.g / 255.0;
    b = c.b / 255.0;

    if (r > 0.04045)
        r = powf(((r + 0.055) / 1.055), 2.4);
    else r /= 12.92;

    if (g > 0.04045)
        g = powf(((g + 0.055) / 1.055), 2.4);
    else g /= 12.92;

    if (b > 0.04045)
        b = powf(((b + 0.055) / 1.055), 2.4);
    else b /= 12.92;

    r *= 100;
    g *= 100;
    b *= 100;

    // Calibration for observer @2° with illumination = D65
    x = r * 0.4124 + g * 0.3576 + b * 0.1805;
    y = r * 0.2126 + g * 0.7152 + b * 0.0722;
    z = r * 0.0193 + g * 0.1192 + b * 0.9505;

    return xyzColor(x, y, z);
}

ColorUtils::CIELABColorSpace ColorUtils::xyzToCIELAB(ColorUtils::xyzColor c) {
    float x, y, z, l, a, b;
    const float refX = 95.047, refY = 100.0, refZ = 108.883;

    // References set at calibration for observer @2° with illumination = D65
    x = c.x / refX;
    y = c.y / refY;
    z = c.z / refZ;

    if (x > 0.008856)
        x = powf(x, 1 / 3.0);
    else x = (7.787 * x) + (16.0 / 116.0);

    if (y > 0.008856)
        y = powf(y, 1 / 3.0);
    else y = (7.787 * y) + (16.0 / 116.0);

    if (z > 0.008856)
        z = powf(z, 1 / 3.0);
    else z = (7.787 * z) + (16.0 / 116.0);

    l = 116 * y - 16;
    a = 500 * (x - y);
    b = 200 * (y - z);

    return CIELABColorSpace(l, a, b);
}

float ColorUtils::getColorDeltaE(ColorUtils::rgbColor c1, ColorUtils::rgbColor c2) {
    xyzColor xyzC1 = rgbToXyz(c1), xyzC2 = rgbToXyz(c2);
    CIELABColorSpace labC1 = xyzToCIELAB(xyzC1), labC2 = xyzToCIELAB(xyzC2);

    float deltaE;

    // Euclidian Distance between two points in 3D matrices
    deltaE = sqrtf(powf(labC1.l - labC2.l, 2) + powf(labC1.a - labC2.a, 2) + powf(labC1.b - labC2.b, 2));

    return deltaE;
}

float ColorUtils::getColorDeltaE2000(ColorUtils::rgbColor c1, ColorUtils::rgbColor c2) {
    xyzColor xyzC1 = rgbToXyz(c1), xyzC2 = rgbToXyz(c2);
    CIELABColorSpace labC1 = xyzToCIELAB(xyzC1), labC2 = xyzToCIELAB(xyzC2);

    // float deltaE;

    // Euclidian Distance between two points in 3D matrices
    // deltaE = sqrtf( powf(labC1.l - labC2.l, 2) + powf(labC1.a - labC2.a, 2) + powf(labC1.b - labC2.b, 2) );

    return CIEDE2000(labC1, labC2);
}

constexpr double
ColorUtils::deg2Rad(
        const double deg) {
    return (deg * /*(M_PI / 180.0)*/ 0.0174533);
}

constexpr double
ColorUtils::rad2Deg(
        const double rad) {
    return ((180.0 / M_PI) * rad);
}

double
ColorUtils::CIEDE2000(
        const CIELABColorSpace &lab1,
        const CIELABColorSpace &lab2) {
    /*
     * "For these and all other numerical/graphical 􏰀delta E00 values
     * reported in this article, we set the parametric weighting factors
     * to unity(i.e., k_L = k_C = k_H = 1.0)." (Page 27).
     */
    const double k_L = 1.0, k_C = 1.0, k_H = 1.0;
    const double deg360InRad = /*ColorUtils::deg2Rad(360.0)*/ 6.28319;
    const double deg180InRad = /*ColorUtils::deg2Rad(180.0)*/ 3.14159;
    const double pow25To7 = 6103515625.0; /* pow(25, 7) */

    double a1Square = lab1.a * lab1.a;
    double a2Square = lab2.a * lab2.a;
    double b1Square = lab1.b * lab1.b;
    double b2Square = lab2.b * lab2.b;

    /*
     * Step 1
     */
    /* Equation 2 */
    double C1 = sqrt(/*(lab1.a * lab1.a)*/ a1Square + /*(lab1.b * lab1.b)*/ b1Square);
    double C2 = sqrt(/*(lab2.a * lab2.a)*/ a2Square + /*(lab2.b * lab2.b)*/ b2Square);
    /* Equation 3 */
    double barC = (C1 + C2) / 2.0;
    /* Equation 4 */
    double barC7 = barC * barC;
    barC7 *= barC7;
    barC7 *= barC7;
    barC7 /= barC;
    const double powBarC7 = /*pow(barC, 7)*/ barC7;
    double G = 0.5 * (1 - sqrt(/*pow(barC, 7)*/ powBarC7 / (/*pow(barC, 7)*/powBarC7 + pow25To7)));
    /* Equation 5 */
    double a1Prime = (1.0 + G) * lab1.a;
    double a2Prime = (1.0 + G) * lab2.a;
    /* Equation 6 */
    double CPrime1 = sqrt((a1Prime * a1Prime) + /*(lab1.b * lab1.b)*/ b1Square);
    double CPrime2 = sqrt((a2Prime * a2Prime) + /*(lab2.b * lab2.b)*/ b2Square);
    /* Equation 7 */
    double hPrime1;
    if (lab1.b == 0 && a1Prime == 0)
        hPrime1 = 0.0;
    else {
        hPrime1 = atan2(lab1.b, a1Prime);
        /*
         * This must be converted to a hue angle in degrees between 0
         * and 360 by addition of 2􏰏 to negative hue angles.
         */
        if (hPrime1 < 0)
            hPrime1 += deg360InRad;
    }
    double hPrime2;
    if (lab2.b == 0 && a2Prime == 0)
        hPrime2 = 0.0;
    else {
        hPrime2 = atan2(lab2.b, a2Prime);
        /*
         * This must be converted to a hue angle in degrees between 0
         * and 360 by addition of 2􏰏 to negative hue angles.
         */
        if (hPrime2 < 0)
            hPrime2 += deg360InRad;
    }

    /*
     * Step 2
     */
    /* Equation 8 */
    double deltaLPrime = lab2.l - lab1.l;
    /* Equation 9 */
    double deltaCPrime = CPrime2 - CPrime1;
    /* Equation 10 */
    double deltahPrime;
    double CPrimeProduct = CPrime1 * CPrime2;
    if (CPrimeProduct == 0)
        deltahPrime = 0;
    else {
        /* Avoid the fabs() call */
        deltahPrime = hPrime2 - hPrime1;
        if (deltahPrime < -deg180InRad)
            deltahPrime += deg360InRad;
        else if (deltahPrime > deg180InRad)
            deltahPrime -= deg360InRad;
    }
    /* Equation 11 */
    double deltaHPrime = 2.0 * sqrt(CPrimeProduct) *
                         sin(deltahPrime / 2.0);

    /*
     * Step 3
     */
    /* Equation 12 */
    double barLPrime = (lab1.l + lab2.l) / 2.0;
    /* Equation 13 */
    double barCPrime = (CPrime1 + CPrime2) / 2.0;
    /* Equation 14 */
    double barhPrime, hPrimeSum = hPrime1 + hPrime2;
    if (CPrime1 * CPrime2 == 0) {
        barhPrime = hPrimeSum;
    } else {
        if (fabs(hPrime1 - hPrime2) <= deg180InRad)
            barhPrime = hPrimeSum / 2.0;
        else {
            if (hPrimeSum < deg360InRad)
                barhPrime = (hPrimeSum + deg360InRad) / 2.0;
            else
                barhPrime = (hPrimeSum - deg360InRad) / 2.0;
        }
    }
    /* Equation 15 */
    double T = 1.0 - (0.17 * cos(barhPrime - /*ColorUtils::deg2Rad(30.0)*/ 0.523599)) +
               (0.24 * cos(2.0 * barhPrime)) +
               (0.32 * cos((3.0 * barhPrime) + /*ColorUtils::deg2Rad(6.0)*/ 0.10472)) -
               (0.20 * cos((4.0 * barhPrime) - /*ColorUtils::deg2Rad(63.0)*/ 1.09956));
    /* Equation 16 */
    double deltaTheta = /*ColorUtils::deg2Rad(30.0)*/ 0.523599 *
                        exp(-pow((barhPrime - /*deg2Rad(275.0)*/ 4.79966) / /*deg2Rad(25.0)*/ 0.436332, 2.0));
    /* Equation 17 */
    double barCPrime7 = barCPrime * barCPrime;
    barCPrime7 *= barCPrime7;
    barCPrime7 *= barCPrime7;
    barCPrime7 /= barCPrime;
    const double powBarCPrime = /*pow(barCPrime, 7.0)*/ barCPrime7;
    double R_C = 2.0 * sqrt(/*pow(barCPrime, 7.0)*/ powBarCPrime /
                            (/*pow(barCPrime, 7.0)*/ powBarCPrime + pow25To7));
    /* Equation 18 */
    const double powBarLPrime = pow(barLPrime - 50.0, 2.0);
    double S_L = 1 + ((0.015 * /*pow(barLPrime - 50.0, 2.0)*/ powBarLPrime) /
                      sqrt(20 + /*pow(barLPrime - 50.0, 2.0)*/ powBarLPrime));
    /* Equation 19 */
    double S_C = 1 + (0.045 * barCPrime);
    /* Equation 20 */
    double S_H = 1 + (0.015 * barCPrime * T);
    /* Equation 21 */
    double R_T = (-sin(2.0 * deltaTheta)) * R_C;

    /* Equation 22 */
    double deltaE = sqrt(
            pow(deltaLPrime / (k_L * S_L), 2.0) +
            pow(deltaCPrime / (k_C * S_C), 2.0) +
            pow(deltaHPrime / (k_H * S_H), 2.0) +
            (R_T * (deltaCPrime / (k_C * S_C)) * (deltaHPrime / (k_H * S_H))));

    return (deltaE);
}
