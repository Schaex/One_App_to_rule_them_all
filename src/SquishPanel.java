import javax.swing.*;
import java.awt.*;

public class SquishPanel extends JPanel {
    /**
     * Utility class for a JPanel container that always displays its component with minimal size.
     */
    public SquishPanel(Component component) {
        this.setLayout(new GridBagLayout());
        this.add(Box.createGlue(), new GridBagConstraints(0, 0, 3, 1, 1, 1, GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(Box.createGlue(), new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(component, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(Box.createGlue(), new GridBagConstraints(2, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(Box.createGlue(), new GridBagConstraints(0, 2, 3, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }
}