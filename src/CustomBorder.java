import javax.swing.border.AbstractBorder;
import java.awt.*;

public class CustomBorder extends AbstractBorder {
    /**
     * Utility class for a custom Border object whose top line only traverses a specific fraction of the component's width.
     * A line on the left side is optional. As such, this border can be used to draw vertical scales.
     */

    double fractionIn;
    boolean leftLine;

    /**
     * The only constructor.
     * @param fractionIn Dictates how far the upper line should go. Values should be in range [0, 1]
     * @param leftLine Dictates whether the left line should be drawn or not.
     */
    public CustomBorder(double fractionIn, boolean leftLine) {
        this.fractionIn = fractionIn;
        this.leftLine = leftLine;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);

        g2d.drawLine(x, y, x + (int) (width * fractionIn), y);

        if (leftLine) {
            g2d.drawLine(x, y, x, y + height);
        }
    }
}