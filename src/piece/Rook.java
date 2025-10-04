package piece;

import module.GamePanel;
import module.Type;

public class Rook extends Piece {

    public Rook(int color, int col, int row) {
        super(color, col, row);
        type = Type.ROOK;

        if (color == GamePanel.WHITE) {
            image = getImage("/piece/w-rook");
        }
        else{
            image = getImage("/piece/b-rook");
        }
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {

        if(isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)){//the rook cannot move to the same square
            // THE ROOOOOOOOK can move either vertically or horizontally
            if(preCol == targetCol || preRow == targetRow){
                if(isValidSquare(targetCol, targetRow) && !pieceIsOnStraightLine(targetCol, targetRow)){
                    return true;
                }
            }
        }
        return false;
    }
}
