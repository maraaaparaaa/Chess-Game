package piece;

import module.GamePanel;
import module.Type;

public class Bishop extends Piece {

    public Bishop(int color, int col, int row) {
        super(color, col, row);
        type = Type.BISHOP;

        if (color == GamePanel.WHITE) {
            image = getImage("/piece/w-bishop");
        }
        else{
            image = getImage("/piece/b-bishop");
        }
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {

        if(isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
            // bishop can move diagonally, so the difference between the starting row and target row is
            // equal to the difference between the starting col and target col
            if(Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)) {
                if(isValidSquare(targetCol, targetRow) && !pieceIsOnDiagonalLine(targetCol, targetRow)) {
                    return true;
                }
            }
        }
        return false;
    }
}
