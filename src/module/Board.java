package module;

import java.awt.*;

public class Board {
    final int MAX_COL = 8;
    final int MAX_ROW = 8;
    public static final int SQUARE_SIZE = 100;  //100X100 pixels
    public static final int HALF_SQUARE_SIZE= SQUARE_SIZE/2;

    public void draw(Graphics2D g2) {
        int c=0;
        for (int row = 0; row < MAX_ROW; row++) {
            for (int col = 0; col < MAX_COL; col++) {

                if(row % 2 == 0 && col % 2 == 1 || row % 2 == 1 && col % 2 == 0) {
                    g2.setColor(new Color(177, 104, 48));
                }
                else g2.setColor(new Color(241, 191, 147));

                g2.fillRect(col*SQUARE_SIZE, row*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }
}
