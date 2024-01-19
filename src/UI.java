import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UI {
    /**
     * Utility class that encapsulates the creation of UI and taking care of Threads.
     * <p></p>
     * Container for the UI.
     */
    private final JFrame frame = new JFrame();

    /**
     * An ExecutorService is acquired in order to keep the application responsive during longer computations.
     */
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * JButton for new jobs and the output area for the results.
     */
    private final JButton redoButton = new JButton("Create new ruler");
    private final JPanel resultPane = new JPanel();
    private final JScrollPane resultScrollPane = new JScrollPane(resultPane);

    /**
     * Components of the dialog.
     */
    private final JPanel dialogPanel = new JPanel(new GridLayout(3, 2));
    private final JTextField dialogMaxCM = new JTextField();
    private final JTextField dialogScale = new JTextField();
    private final JCheckBox dialogShowFives = new JCheckBox();

    /**
     * Is called by main method on startup and takes care of everything regarding building and displaying the UI as well as Threads.
     */
    public static void init() {
        SwingUtilities.invokeLater(() -> {
            UI ui = new UI();
            ui.preparation();

            ui.displayDialogBox(true);

            ui.frame.setVisible(true);

            Runtime.getRuntime().addShutdownHook(new Thread(ui.executorService::shutdown));
        });
    }

    /**
     * Utility method to make init() method less cluttered.
     */
    private void preparation() {
        frame.setSize(400, (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 50));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JScrollBar scrollBarV = resultScrollPane.getVerticalScrollBar();
        scrollBarV.setUnitIncrement(100);

        JPanel auxPanel = new JPanel(new GridBagLayout());
        auxPanel.add(redoButton, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        auxPanel.add(resultScrollPane, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        frame.add(auxPanel);

        redoButton.addActionListener(e -> displayDialogBox(false));


        dialogPanel.add(new JLabel("Upper limit of your ruler in cm:"));
        dialogPanel.add(dialogMaxCM);
        dialogPanel.add(new JLabel("Number of pixels between each mm:  "));
        dialogPanel.add(dialogScale);
        dialogPanel.add(new JLabel("Indicators every five mm"));
        dialogPanel.add(dialogShowFives);
    }

    /**
     * @param isStartup Is only true on startup and makes this method behave differently.
     */
    private void displayDialogBox(boolean isStartup) {
        int choice = JOptionPane.showConfirmDialog(null, dialogPanel, "Input", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (choice != JOptionPane.OK_OPTION && isStartup) {
            System.exit(0);
        } else if (choice == JOptionPane.OK_OPTION) {
            int maxCM;
            int scale;
            boolean showFives = dialogShowFives.isSelected();

            try {
                maxCM = Integer.parseInt(dialogMaxCM.getText());
                scale = Integer.parseInt(dialogScale.getText());
            } catch (Exception ex) {
                displayDialogBox(isStartup);
                return;
            }

            if (maxCM < 0 || scale < 0) {
                if (isStartup) {
                    displayDialogBox(true);
                }
                return;
            }

            executorService.submit(() -> {
                frame.setTitle("Waiting...");
                resultPane.removeAll();
                resultPane.revalidate();
                resultPane.repaint();

                JPanel ruler = getRuler(maxCM, scale, showFives);

                resultPane.add(ruler);

                frame.setTitle("Ruler from 0 to " + maxCM + " cm with scale " + scale);
                resultPane.revalidate();
                resultPane.repaint();
            });
        }
    }

    /**
     * @param endCM The range of the ruler is [0, endCM] in cm.
     * @param vSize Number of pixels of space between each mm mark.
     * @param showFives Determines whether multiples of 5 (5, 15, 25,...) should be highlighted.
     * @return JPanel containing a vertical ruler.
     */
    public static JPanel getRuler(int endCM, int vSize, boolean showFives) {
        JPanel rulerStripesPanel = new JPanel();
        rulerStripesPanel.setLayout(new BoxLayout(rulerStripesPanel, BoxLayout.Y_AXIS));
        SquishPanel rulerStripesSquishPanel = new SquishPanel(rulerStripesPanel);

        JPanel rulerLabelPanel = new JPanel();
        rulerLabelPanel.setLayout(new BoxLayout(rulerLabelPanel, BoxLayout.Y_AXIS));
        SquishPanel labelSquishPanel = new SquishPanel(rulerLabelPanel);

        JPanel rulerPanel = new JPanel();
        rulerPanel.setLayout(new BoxLayout(rulerPanel, BoxLayout.X_AXIS));
        rulerPanel.add(rulerStripesSquishPanel);
        rulerPanel.add(labelSquishPanel);

        rulerStripesPanel.add(Box.createVerticalStrut(10));

        // Instantiation of constants.
        CustomBorder full = new CustomBorder(1d, true);
        CustomBorder threeQuarters = new CustomBorder(0.75d, true);
        CustomBorder half = new CustomBorder(0.5d, true);

        int dim2WidthFactor = String.valueOf(endCM).length();
        int endMM = endCM * 10;
        Dimension dim = new Dimension(30, vSize);
        Dimension dim2 = new Dimension(dim2WidthFactor * 10, vSize * 10);

        // Ruler in range [0, endCM).
        for (int currentMM = 0; currentMM < endMM; currentMM++) {
            boolean multipleOf10 = currentMM % 10 == 0;

            CustomBorder graduation;

            if (showFives) {
                graduation = !(currentMM % 5 == 0) ? half : (multipleOf10 ? full : threeQuarters);
            }
            else {
                graduation = multipleOf10 ? full : half;
            }

            JPanel stripePanel = new JPanel();
            stripePanel.setPreferredSize(dim);
            stripePanel.setBorder(graduation);

            rulerStripesPanel.add(stripePanel);

            if (multipleOf10) {
                JPanel labelPanel = new JPanel();
                labelPanel.setPreferredSize(dim2);
                labelPanel.add(new JLabel(" " + (currentMM / 10)));

                rulerLabelPanel.add(labelPanel);
            }
        }

        JPanel lastStripePanel = new JPanel();
        lastStripePanel.setPreferredSize(new Dimension(1, 10));
        lastStripePanel.setBorder(new CustomBorder(1d, false));

        rulerStripesPanel.add(lastStripePanel);

        JPanel lastLabelPanel = new JPanel();
        lastLabelPanel.setPreferredSize(new Dimension(30, 27));
        lastLabelPanel.add(new JLabel(" " + endCM));

        rulerLabelPanel.add(lastLabelPanel);

        return rulerPanel;
    }
}