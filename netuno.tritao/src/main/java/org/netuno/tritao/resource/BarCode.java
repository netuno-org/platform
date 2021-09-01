package org.netuno.tritao.resource;

import org.netuno.proteu.Proteu;
import org.netuno.tritao.config.Hili;
import uk.org.okapibarcode.backend.Code128;
import uk.org.okapibarcode.backend.HumanReadableLocation;
import uk.org.okapibarcode.output.Java2DRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Bar Code - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
//@Resource(name = "barcode")
public class BarCode extends ResourceBase {

    public BarCode(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    public void test() throws IOException {
        Code128 barcode = new Code128();
        barcode.setFontName("Monospaced");
        barcode.setFontSize(16);
        barcode.setModuleWidth(2);
        barcode.setBarHeight(50);
        barcode.setHumanReadableLocation(HumanReadableLocation.BOTTOM);
        barcode.setContent("123456789");

        int width = barcode.getWidth();
        int height = barcode.getHeight();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = image.createGraphics();
        Java2DRenderer renderer = new Java2DRenderer(g2d, 1, Color.WHITE, Color.BLACK);
        renderer.render(barcode);

        ImageIO.write(image, "png", new java.io.File("code128.png"));
    }
}
