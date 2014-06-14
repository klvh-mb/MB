package common.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import jjil.algorithm.RgbAvgGray;
import jjil.core.Rect;
import jjil.core.RgbImage;
import jjil.j2se.RgbImageJ2se;

/**
 * Created by IntelliJ IDEA.
 * Date: 14/6/14
 * Time: 12:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class FaceFinder {
    private static final play.api.Logger LOGGER = play.api.Logger.apply(FaceFinder.class);

    private static final int MIN_SCALE = 1;
    private static final int MAX_SCALE = 40;

    /**
     * @param origFile
     * @return
     * @throws Exception
     */
    public static BufferedImage getPictureWithFace(File origFile) throws IOException {
        BufferedImage origImage = ImageIO.read(origFile);
        LOGGER.underlyingLogger().info("getPictureWithFace - w="+origImage.getWidth()+" h="+origImage.getHeight());

        List<Rect> rects = findFaces(origImage, "/haar/HCSB.txt");
//        if (rects.size() == 0) {
//            rects = findFaces(origImage, "/haar/frontaldefault.txt");
//        }

        return fitForImage(rects, origImage);
    }


    private static List<Rect> findFaces(BufferedImage origImage, String profile) {
        try {
            InputStream is = FaceFinder.class.getResourceAsStream(profile);

            Gray8DetectHaarMultiScale detectHaar = new Gray8DetectHaarMultiScale(is, MIN_SCALE, MAX_SCALE);
            RgbImage im = RgbImageJ2se.toRgbImage(origImage);
            RgbAvgGray toGray = new RgbAvgGray();
            toGray.push(im);

            List<Rect> results = detectHaar.pushAndReturn(toGray.getFront());

            LOGGER.underlyingLogger().info("Found "+results.size()+" faces");
            for (int i = 0; i < results.size(); i++) {
                Rect rect = results.get(i);
                LOGGER.underlyingLogger().info("["+i+"] left="+rect.getLeft()+" top="+rect.getTop()+" w="+rect.getWidth()+" h="+rect.getHeight());
            }

            return results;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private static BufferedImage fitForImage(List<Rect> results, BufferedImage origImage) {
        try {
            int minLeft = Integer.MAX_VALUE, minTop = Integer.MAX_VALUE;
            int maxLeft = 0, maxTop = 0;
            int maxWidth = 0, maxHeight = 0;
            for (int i=0; i < results.size(); i++) {
                Rect rect = results.get(i);
                if (rect.getLeft() < minLeft) {
                    minLeft = rect.getLeft();
                }
                if (rect.getTop() < minTop) {
                    minTop = rect.getTop();
                }
                if (rect.getLeft() > maxLeft) {
                    maxLeft = rect.getLeft();
                }
                if (rect.getTop() > maxTop) {
                    maxTop = rect.getTop();
                }
                if (rect.getWidth() > maxWidth) {
                    maxWidth = rect.getWidth();
                }
                if (rect.getHeight() > maxHeight) {
                    maxHeight = rect.getHeight();
                }
            }

            int origWidth = origImage.getWidth();
            int origHeight = origImage.getHeight();

            CropSpec cropSpec;
            if (results.size() == 0) {
                cropSpec = getCenterPointCropSpec(origWidth, origHeight);
            }
            else if (results.size() == 1) {
                Rect rect = results.get(0);
                cropSpec = getReferencePointCropSpec(rect, origWidth, origHeight);
            }
            else {
                int width = maxLeft - minLeft + maxWidth;
                int height = maxTop - minTop + maxHeight;
                cropSpec = getReferencePointCropSpec(minLeft, minTop, width, height, origWidth, origHeight);
            }

            double percent = (double) cropSpec.width / (double) Math.min(origWidth, origHeight);
            if (percent < 0.3d) {
                cropSpec = getCenterPointCropSpec(origWidth, origHeight);
                LOGGER.underlyingLogger().info("Falling back to CenterPoint Crop");
            }

            LOGGER.underlyingLogger().info(cropSpec.toString());
            return origImage.getSubimage(cropSpec.left, cropSpec.top, cropSpec.width, cropSpec.height);

        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private static CropSpec getReferencePointCropSpec(Rect refRect, int origWidth, int origHeight) {
        return getReferencePointCropSpec(refRect.getLeft(), refRect.getTop(), refRect.getWidth(), refRect.getHeight(),
                origWidth, origHeight);
    }

    private static CropSpec getReferencePointCropSpec(int refRectLeft, int refRectTop, int refRectWidth, int refRectHeight,
                                                      int origWidth, int origHeight) {
        int refX = refRectLeft + (refRectWidth / 2);
        int refY = refRectTop + (refRectHeight / 2);

        CropSpec cropSpec = new CropSpec();
        if (origWidth > origHeight) {
            cropSpec.top = 0;
            cropSpec.left = Math.max(refX - (origHeight / 2), 0);
            cropSpec.width = origHeight;
            cropSpec.height = origHeight;

            int maxLeft = cropSpec.left + cropSpec.width;
            if (maxLeft > origWidth) {
                cropSpec.left -= (maxLeft - origWidth);
            }
        } else {
            cropSpec.left = 0;
            cropSpec.top = Math.max(refY - (origWidth / 2), 0);
            cropSpec.width = origWidth;
            cropSpec.height = origWidth;

            int maxTop = cropSpec.top + cropSpec.height;
            if (maxTop > origHeight) {
                cropSpec.top -= (maxTop - origHeight);
            }
        }
        return cropSpec;
    }

    private static CropSpec getCenterPointCropSpec(int origWidth, int origHeight) {
        CropSpec cropSpec = new CropSpec();
        if (origWidth > origHeight) {
            cropSpec.top = 0;
            cropSpec.left = (origWidth / 2) - (origHeight / 2);
            cropSpec.width = origHeight;
            cropSpec.height = origHeight;
        } else {
            cropSpec.left = 0;
            cropSpec.top = (origHeight / 2) - (origWidth / 2);
            cropSpec.width = origWidth;
            cropSpec.height = origWidth;
        }
        return cropSpec;
    }

    /**
     * Crop Spec
     */
    private static class CropSpec {
        public int left;
        public int top;
        public int width;
        public int height;

        @Override
        public String toString() {
            return "CropSpec{" +
                    "left=" + left +
                    ", top=" + top +
                    ", width=" + width +
                    ", height=" + height +
                    '}';
        }
    }
}
