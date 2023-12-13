#include <jni.h>
#include <string>

#include <android/log.h>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <cmath>
#include <queue>
#include <utility>
#include <map>
#include <vector>
#include <unordered_map>
#include <tuple>
#include <thread>
#include <algorithm>
#include <android/bitmap.h>

// https://github.com/ThunderStruct/Color-Utilities
// http://zschuessler.github.io/DeltaE/learn/

#include "ColorUtils.h"

#define MIN(a,b) ((a)<(b)?(a):(b))
#define MAX(a,b) ((a)>(b)?(a):(b))

#define  LOG_TAG    "jnibitmap"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

#define PI 3.14159265

#define DEBUG 0

#define JNI_METHOD(NAME) \
    Java_com_bcl_nativemagicbrush_NativeLib_##NAME

typedef struct COLOR {
    int color;

    int alpha() const {
        return (color >> 24) & 0xFF;
    }

    int _color() const {
        return abgr_to_argb(color);
    }

    int abgr_to_argb(int abgr) const {
        return (abgr & 0xff00ff00) | ((abgr << 16) & 0xff0000) | ((abgr >> 16) & 0xff);
    }

    int red() const {
        return (abgr_to_argb(color) >> 16) & 0xFF;
    }

    int green() const {
        return (abgr_to_argb(color) >> 8) & 0xFF;
    }

    int blue() const {
        return abgr_to_argb(color) & 0xFF;
    }

    int isTransparent() const {
        return alpha() == 0;
    }

    bool operator ==(const COLOR a) const {
        return this->red() == a.red() && this->green() == a.green() && this->blue() == a.blue() && this->alpha() == a.alpha();
    }

};

uint32_t width = 0;
uint32_t height = 0;
uint32_t length = 0;
const float DYNAMIC_THRESHOLD_MODIFIER = 10.0f;
double powMagicThreshold = -1.0;

extern "C" {
int findSmartColorDistanceSquared(const COLOR &colorA, const COLOR &colorB);
int findEuclideanColorDistanceSquared(const COLOR &colorA, const COLOR &colorB);
float findEuclideanColorDistanceSquaredCIELab(const ColorUtils::CIELABColorSpace &colorA, const ColorUtils::CIELABColorSpace &colorB);
inline COLOR createCOLOR(int color);
bool isValidToRemoveWithDynamicThreshold(int pivotColor, const uint32_t *pixelsImage, int x1, int y1, int x2, int y2, const int radius,
                                         const float magicThreshold);
bool
isValidToRemoveWithDynamicThresholdModified(int pivotColor, const uint32_t *pixelsImage, int x1, int y1, int x2, int y2, const int radius,
                                            const float magicThreshold);
bool isValidToRemove(int pivotColor, const uint32_t *pixelsImage, int x1, int y1, int x2, int y2, const float magicThreshold);
bool inPixelValid(int x, int y);
bool isSimilarColor(const COLOR &colorA, const COLOR &colorB);
bool isNeighbourSimilar(const uint32_t *pixelsImage, int x, int y, int &alphaThreshold);

int findEuclideanDistanceSquared(int x1, int y1, int x2, int y2);
float findEuclideanDistance(int x1, int y1, int x2, int y2);
inline int pointToIndex(int x, int y);
inline int positionToInt(int x, int y);
inline std::pair<int, int> intToPosition(int x);

float smoothShift(float value, int32_t times);

JNIEXPORT void JNICALL
JNI_METHOD(eraseMagicallyWithDynamicThreshold)(JNIEnv *env, jobject obj, jobject bitmap, jint x, jint y, jint radius, jint magicThreshold);

JNIEXPORT void JNICALL JNI_METHOD(eraseMagically)(JNIEnv *env, jobject obj, jobject bitmap, jint x, jint y, jint magicThreshold);

JNIEXPORT void JNICALL
JNI_METHOD(eraseMagicallyWithDynamicThresholdModified)(JNIEnv *env, jobject obj, jobject bitmap, jint x, jint y, jint color, jint radius,
                                                       jint magicThreshold);

JNIEXPORT void JNICALL JNI_METHOD(removeNoise)(JNIEnv *env, jobject obj, jobject bitmap, jint alphaThreshold);
}

//-----------------separator with method declaration----------------------------

int findEuclideanDistanceSquared(int x1, int y1, int x2, int y2) {
    int x = x1 - x2;
    int y = y1 - y2;
    return (x * x) + (y * y);
}

float findEuclideanDistance(int x1, int y1, int x2, int y2) {
    float dx = (x1 - x2) * 1.0f;
    float dy = (y1 - y2) * 1.0f;

    return sqrt((dx * dx) + (dy * dy));
}

int findSmartColorDistanceSquared(const COLOR &colorA, const COLOR &colorB) {
    int rmean = ((colorA.red()) + (colorB.red())) / 2;
    int r = (colorA.red()) - (colorB.red());
    int g = (colorA.green()) - (colorB.green());
    int b = (colorA.blue()) - (colorB.blue());
    return (((512 + rmean) * r * r) >> 8) + 4 * g * g + (((767 - rmean) * b * b) >> 8);
}

int findEuclideanColorDistanceSquared(const COLOR &colorA, const COLOR &colorB) {
    int rd = (colorA.red()) - (colorB.red());
    int gd = (colorA.green()) - (colorB.green());
    int bd = (colorA.blue()) - (colorB.blue());

    return rd * rd + gd * gd + bd * bd;
}

float findEuclideanColorDistanceSquaredCIELab(const ColorUtils::CIELABColorSpace &colorA, const ColorUtils::CIELABColorSpace &colorB) {
    float ld = colorA.l - colorB.l;
    float ad = colorA.a - colorB.a;
    float bd = colorA.b - colorB.b;

    return ld*ld + ad * ad + bd * bd;
}

inline COLOR createCOLOR(int color) {
    COLOR _color;
    _color.color = color;

    return _color;
}

bool inPixelValid(int x, int y) {
    return x >= 0 && x < width && y >= 0 && y < height;
}

inline int pointToIndex(int x, int y) {
    return (y * width) + x;
}

inline int positionToInt(int x, int y) {
    return x * 10000 + y;
}

inline std::pair<int, int> intToPosition(int x) {
    std::pair<int, int> p;
    p.first = x / 10000;
    p.second = x % 10000;

    return p;
}

int argbToABGR(int argbColor) {
    int r = (argbColor >> 16) & 0xFF;
    int b = argbColor & 0xFF;
    return (argbColor & 0xFF00FF00) | (b << 16) | r;
}

bool isValidToRemoveWithDynamicThreshold(int pivotColor, const uint32_t *pixelsImage, int x1, int y1, int x2, int y2, const int radius,
                                         const float magicThreshold) {
    COLOR pivotCOLOR = createCOLOR(pivotColor);
    int targetColor = pixelsImage[pointToIndex(x2, y2)]; // assign from x2, y2

    COLOR targetCOLOR = createCOLOR(targetColor);

    if (targetColor == 0) return false;

    if (radius > 0 && findEuclideanDistanceSquared(x1, y1, x2, y2) > radius * radius) {
        LOGI("return false first if (%d, %d), (%d, %d)", x1, y1, x2, y2);
        return false;
    }

    float radiusProgress = (1.0f - (findEuclideanDistance(x1, y1, x2, y2) / (radius * 1.0f)));
    radiusProgress *= radiusProgress;

    float dynamicMagicThreshold = (DYNAMIC_THRESHOLD_MODIFIER * magicThreshold * radiusProgress);
    dynamicMagicThreshold *= dynamicMagicThreshold;

    float colorDiff = findSmartColorDistanceSquared(pivotCOLOR, targetCOLOR) * 1.0f;

    ColorUtils::rgbColor c1(static_cast<unsigned int >(pivotCOLOR.red()), pivotCOLOR.green(), pivotCOLOR.blue());
    ColorUtils::rgbColor c2(static_cast<unsigned int >(targetCOLOR.red()), targetCOLOR.green(), targetCOLOR.blue());

    //LOGE("delta E %f", ColorUtils::getColorDeltaE(c1, c2));

    /*if (colorDiff < dynamicMagicThreshold) {
        return true;
    }*/

//    LOGE("color delta 2000 %f", ColorUtils::getColorDeltaE(c1, c2));

    if (ColorUtils::getColorDeltaE2000(c1, c2) < 21) return true;

    return false;
}

bool
isValidToRemoveWithDynamicThresholdModified(int pivotColor, const uint32_t *pixelsImage, int x1, int y1, int x2, int y2, const int radius,
                                            const float magicThreshold) {
    COLOR pivotCOLOR = createCOLOR(pivotColor);
    int targetColor = pixelsImage[pointToIndex(x2, y2)]; // assign from x2, y2

    COLOR targetCOLOR = createCOLOR(targetColor);

    if (targetColor == 0) return false;

    if (radius > 0 && findEuclideanDistanceSquared(x1, y1, x2, y2) > radius * radius) {
        //    LOGI("return false first if (%d, %d), (%d, %d)", x1, y1, x2, y2);
        return false;
    }

    float radiusProgress = ((findEuclideanDistance(x1, y1, x2, y2) / (radius * 1.0f)));
    if (radiusProgress > 0.85f) {
        return false;
    }

    radiusProgress = radiusProgress * 1.17647f;
    radiusProgress *= radiusProgress;

    ColorUtils::rgbColor c1(static_cast<unsigned int >(pivotCOLOR.red()), pivotCOLOR.green(), pivotCOLOR.blue());
    ColorUtils::rgbColor c2(static_cast<unsigned int >(targetCOLOR.red()), targetCOLOR.green(), targetCOLOR.blue());

    const float threshold = 100000.0f;
    const float colorDelta = ColorUtils::getColorDeltaE2000(c1, c2);
    const float  threshold1 = 23.0f;

    if(colorDelta < threshold1) {
        return true;
    }
    if (radiusProgress < 1.0f && (ColorUtils::getColorDeltaE2000(c1, c2)) * threshold * radiusProgress < threshold) {
        return true;
    }

    return false;
}

bool isValidToRemove(int pivotColor, const uint32_t *pixelsImage, int x1, int y1, int x2, int y2, const float magicThreshold) {
    COLOR pivotCOLOR = createCOLOR(pivotColor);
    int targetColor = pixelsImage[pointToIndex(x2, y2)]; // assign from x2, y2

    COLOR targetCOLOR = createCOLOR(targetColor);

    if (targetColor == 0) return false;
    if(pivotCOLOR == targetCOLOR) return true;

   /* ColorUtils::rgbColor c1(static_cast<unsigned int >(pivotCOLOR.red()), pivotCOLOR.green(), pivotCOLOR.blue());
    ColorUtils::rgbColor c2(static_cast<unsigned int >(targetCOLOR.red()), targetCOLOR.green(), targetCOLOR.blue());

    float colorDelta = ColorUtils::getColorDeltaE2000(c1, c2);

    const float  threshold1 = 0.13f;

    if(colorDelta < threshold1) {
        return true;
    }

    if(colorDelta > 51.0) return false;

    if(powMagicThreshold < 0.0) {
        powMagicThreshold = pow(magicThreshold, 0.43);
    }

    if(colorDelta < magicThreshold*(1/(powMagicThreshold))) return true;*/

    if(findSmartColorDistanceSquared(pivotCOLOR, targetCOLOR) < magicThreshold*magicThreshold) return true;

    return false;
}

bool isSimilarColor(const COLOR &colorA, const COLOR &colorB) {
    int distance = findSmartColorDistanceSquared(colorA, colorB);

    return distance < 10000;
}

bool isNeighbourSimilar(const uint32_t *pixelsImage, int x, int y, int &alphaThreshold) {

    for (int i = -1; i <= 1; i++) {
        for (int j = -1; j <= 1; j++) {
            if (i == 0 && j == 0) continue;

            int tx = x + i;
            int ty = y + j;

            COLOR colorA = createCOLOR(pixelsImage[pointToIndex(x, y)]);
            COLOR colorB = createCOLOR(pixelsImage[pointToIndex(tx, ty)]);

            if (colorB.alpha() < alphaThreshold) continue;

            if (isSimilarColor(colorA, colorB)) return true;
        }
    }

    return false;

    /*for (dx in -1..1){
        for (dy in -1..1){
            if (dx == 0 && dy == 0) continue

            val tx = dx + x
            val ty = dy + y

            if (bitmap[tx,ty].alpha < 25) continue

            if (isSimilarColor(bitmap[x,y],bitmap[tx,ty])) return true
        }
    }*/
}

JNIEXPORT void  JNICALL
JNI_METHOD(eraseMagicallyWithDynamicThreshold)(JNIEnv *env, jobject obj, jobject bitmap, jint x, jint y, jint radius, jint magicThreshold) {

    AndroidBitmapInfo imgInfo;

    int ret;
    if ((ret = AndroidBitmap_getInfo(env, bitmap, &imgInfo)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }

    if (imgInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888!");
        return;
    }

    void *oldBitmapPixels;
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &oldBitmapPixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        return;
    }

    width = imgInfo.width;
    height = imgInfo.height;
    length = width * height;

    uint32_t *pixelsImage = (uint32_t *) oldBitmapPixels;

    if ((y * width) + x >= length) {
        AndroidBitmap_unlockPixels(env, bitmap);
        return;
    }

    int currentIndex = pointToIndex(x, y);

    if (!inPixelValid(x, y)) {
        AndroidBitmap_unlockPixels(env, bitmap);
        return;
    }

    int currentColor = pixelsImage[currentIndex];

    COLOR currentCOLOR = createCOLOR(currentColor);

    if (currentColor == 0) {
        AndroidBitmap_unlockPixels(env, bitmap);
        return;
    }

    std::deque<int> Q;
    Q.push_front(positionToInt(x, y));

    int dir[] = {0, -1, -1, 0, 0, 1, 1, 0};

    while (!Q.empty()) {
        int value = Q.back();
        Q.pop_back();

        std::pair<int, int> p = intToPosition(value);
        int x1 = p.first;
        int y1 = p.second;

        pixelsImage[pointToIndex(x1, y1)] = 0;

        for (int i = 0; i < 8; i += 2) {
            int x2 = x1 + dir[i];
            int y2 = y1 + dir[i + 1];

            if (!inPixelValid(x2, y2) || pixelsImage[pointToIndex(x2, y2)] == 0) continue;

            if (isValidToRemoveWithDynamicThreshold(
                    currentCOLOR.color, pixelsImage, x, y, x2, y2, radius, magicThreshold
            )) {
                Q.push_back(positionToInt(x2, y2));
            }
        }
    }

    AndroidBitmap_unlockPixels(env, bitmap);
}

JNIEXPORT void JNICALL
JNI_METHOD(eraseMagicallyWithDynamicThresholdModified)(JNIEnv *env, jobject obj, jobject bitmap, jint x, jint y, jint color, jint radius,
                                                       jint magicThreshold) {

    AndroidBitmapInfo imgInfo;

    int ret;
    if ((ret = AndroidBitmap_getInfo(env, bitmap, &imgInfo)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }

    if (imgInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888!");
        return;
    }

    void *oldBitmapPixels;
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &oldBitmapPixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        return;
    }

    width = imgInfo.width;
    height = imgInfo.height;
    length = width * height;

    uint32_t *pixelsImage = (uint32_t *) oldBitmapPixels;

    if ((y * width) + x >= length) {
        AndroidBitmap_unlockPixels(env, bitmap);
        return;
    }

    int currentIndex = pointToIndex(x, y);

    if (!inPixelValid(x, y)) {
        AndroidBitmap_unlockPixels(env, bitmap);
        return;
    }

    int currentColor = argbToABGR(color);

    COLOR currentCOLOR = createCOLOR(currentColor);

    if (currentColor == 0) {
        AndroidBitmap_unlockPixels(env, bitmap);
        return;
    }

    for (int i = -radius; i < radius; i++) {
        for (int j = -radius; j < radius; j++) {
            //LOGI("dsajklhdjashd (%d, %d), %d, %d", x+i, y+j, inPixelValid(x+i, y+j), isValidToRemoveWithDynamicThreshold(currentCOLOR.color, pixelsImage, x, y, x+i, y+j, radius, magicThreshold));
            if (inPixelValid(x + i, y + j) &&
                isValidToRemoveWithDynamicThresholdModified(currentCOLOR.color, pixelsImage, x, y, x + i, y + j, radius, magicThreshold)) {
                pixelsImage[pointToIndex(x + i, y + j)] = 0;
            }
        }
    }

    AndroidBitmap_unlockPixels(env, bitmap);
}

JNIEXPORT void JNICALL JNI_METHOD(eraseMagically)(JNIEnv *env, jobject obj, jobject bitmap, jint x, jint y, jint magicThreshold) {

    AndroidBitmapInfo imgInfo;

    int ret;
    if ((ret = AndroidBitmap_getInfo(env, bitmap, &imgInfo)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }

    if (imgInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888!");
        return;
    }

    void *oldBitmapPixels;
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &oldBitmapPixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        return;
    }

    width = imgInfo.width;
    height = imgInfo.height;
    length = width * height;

    uint32_t *pixelsImage = (uint32_t *) oldBitmapPixels;

    if ((y * width) + x >= length) {
        AndroidBitmap_unlockPixels(env, bitmap);
        return;
    }

    int currentIndex = pointToIndex(x, y);

    if (!inPixelValid(x, y)) {
        AndroidBitmap_unlockPixels(env, bitmap);

        return;
    }

    int currentColor = pixelsImage[currentIndex];

    COLOR currentCOLOR = createCOLOR(currentColor);

    if (currentColor == 0) {
        AndroidBitmap_unlockPixels(env, bitmap);

        return;
    }

    std::deque<int> Q;
    Q.push_front(positionToInt(x, y));

    int dir[] = {0, -1, -1, 0, 0, 1, 1, 0};

    powMagicThreshold = -1.0;

    while (!Q.empty()) {
        int value = Q.back();
        Q.pop_back();

        std::pair<int, int> p = intToPosition(value);
        int x1 = p.first;
        int y1 = p.second;

        if (inPixelValid(x1, y1)) pixelsImage[pointToIndex(x1, y1)] = 0;

        for (int i = 0; i < 8; i += 2) {
            int x2 = x1 + dir[i];
            int y2 = y1 + dir[i + 1];

            if (!inPixelValid(x2, y2) || pixelsImage[pointToIndex(x2, y2)] == 0) continue;

            if (isValidToRemove(
                    currentCOLOR.color, pixelsImage, x, y, x2, y2, magicThreshold
            )) {
                Q.push_back(positionToInt(x2, y2));
            }
        }
    }

    powMagicThreshold = -1.0;

    AndroidBitmap_unlockPixels(env, bitmap);
}

JNIEXPORT void JNICALL JNI_METHOD(removeNoise)(JNIEnv *env, jobject obj, jobject bitmap, jint alphaThreshold) {

    AndroidBitmapInfo imgInfo;

    int ret;
    if ((ret = AndroidBitmap_getInfo(env, bitmap, &imgInfo)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }

    if (imgInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888!");
        return;
    }

    void *oldBitmapPixels;
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &oldBitmapPixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        return;
    }

    width = imgInfo.width;
    height = imgInfo.height;
    length = width * height;

    uint32_t *pixelsImage = (uint32_t *) oldBitmapPixels;

    for (int j = 2; j < height - 2; j++) {
        for (int i = 2; i < width - 2; i++) {
            COLOR colorA = createCOLOR(pixelsImage[pointToIndex(i, j)]);

            if (colorA.alpha() > alphaThreshold) continue;

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int tx = i + dx;
                    int ty = j + dy;

                    COLOR colorB = createCOLOR(pixelsImage[pointToIndex(tx, ty)]);

                    if (colorB.alpha() < alphaThreshold) continue;

                    if (!isNeighbourSimilar(pixelsImage, tx, ty, alphaThreshold)) {
                        pixelsImage[pointToIndex(tx, ty)] = 0;
                    }
                }
            }
        }
    }

    /*for (j in 2 until bitmap.height - 2){
        for (i in 2 until bitmap.width - 2){
            if (bitmap[i, j].alpha > 25) continue

            for (dx in -1..1){
                for (dy in -1..1){
                    val tx = i + dx
                    val ty = j + dy

                    if (bitmap[tx,ty].alpha < 25) continue

                    if(!isNeighbourSimilar(bitmap,tx,ty)){
                        bitmap[tx,ty] = Color.argb(0,0,0,0)
                    }
                }
            }
        }
    }*/

    AndroidBitmap_unlockPixels(env, bitmap);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_bcl_nativemagicbrush_NativeLib_makeOpaque(JNIEnv *env, jobject thiz, jobject mask, jint color) {
    AndroidBitmapInfo imgInfo;

    int ret;
    if ((ret = AndroidBitmap_getInfo(env, mask, &imgInfo)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }

    if (imgInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888!");
        return;
    }

    void *oldBitmapPixels;
    if ((ret = AndroidBitmap_lockPixels(env, mask, &oldBitmapPixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        return;
    }

    width = imgInfo.width;
    height = imgInfo.height;
    length = width * height;

    auto *pixelsImage = (uint32_t *) oldBitmapPixels;

    for (int index = 0; index < length; ++index) {
        uint32_t alpha = pixelsImage[index] >> 24;
        if (alpha == 0) {
            pixelsImage[index] = 0;
        } else {
            pixelsImage[index] = color;
        }
    }
    AndroidBitmap_unlockPixels(env, mask);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bcl_nativemagicbrush_NativeLib_makeSmoothShiftedMask(JNIEnv *env, jobject thiz, jobject bitmap) {
    AndroidBitmapInfo imgInfo;

    int ret;
    if ((ret = AndroidBitmap_getInfo(env, bitmap, &imgInfo)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }

    if (imgInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888!");
        return;
    }

    void *oldBitmapPixels;
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &oldBitmapPixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        return;
    }

    width = imgInfo.width;
    height = imgInfo.height;
    length = width * height;

    auto *pixelsImage = (uint32_t *) oldBitmapPixels;

    for (int index = 0; index < length; ++index) {
        uint32_t alpha = pixelsImage[index] >> 24;
        float shiftedValue = ((float) alpha) / (float) 255.0;
        shiftedValue = smoothShift(shiftedValue, 2);
        int32_t pixelValue = std::min(255, (int32_t) (shiftedValue * 255.0));
        pixelsImage[index] = (pixelValue << 24) + (pixelValue << 16) + (pixelValue << 8) + pixelValue;
    }

    AndroidBitmap_unlockPixels(env, bitmap);
}

float smoothShift(float value, int32_t times) {
    if (times == 0) {
        return value;
    }
    return smoothShift((float) ((sin(PI * (value - 0.5)) + 1.0) / 2.0), times - 1);
}
