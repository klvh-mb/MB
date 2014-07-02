package common.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * Date: 28/6/14
 * Time: 10:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageFileUtil {

    public static void writeFileWithImage(File file, BufferedImage image) throws IOException {
        String ext = "jpg";
        if (file.getName().toLowerCase().endsWith("png")) {
            ext = "png";
        } else if (file.getName().toLowerCase().endsWith("gif")) {
            ext = "gif";
        }
        ImageIO.write(image, ext, file);
    }

}
