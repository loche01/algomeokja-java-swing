package ui_n_utils;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * 프로젝트 클래스패스의 이미지를 읽고, 누락되면 null을 반환하는 아이콘 로더입니다.
 */
public final class ClasspathIconLoader {
    private ClasspathIconLoader() {
    }

    public static ImageIcon loadScaled(
            Class<?> anchorClass, String resourcePath, int width, int height) {
        if (anchorClass == null || resourcePath == null
                || width <= 0 || height <= 0) {
            return null;
        }

        URL resource = anchorClass.getResource(resourcePath);
        if (resource == null) {
            return null;
        }

        ImageIcon sourceIcon = new ImageIcon(resource);
        if (sourceIcon.getIconWidth() <= 0 || sourceIcon.getIconHeight() <= 0) {
            return null;
        }
        Image scaledImage = sourceIcon.getImage().getScaledInstance(
                width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
}
