import module.GamePanel;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame("Chess Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // also shuts down the program when closing it
        window.setResizable(false);

        GamePanel gp = new GamePanel();
        window.add(gp);
        window.pack(); // causes the window to extend to fit the preferred size and layouts of its subcomponents

        window.setLocationRelativeTo(null); // window in the center
        window.setVisible(true);

        gp.launchGame();
    }
}