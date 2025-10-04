package piece;

import module.GamePanel;
import module.Type;

public class Pawn extends Piece {

    public Pawn(int color, int col, int row) {
        super(color, col, row);
        type = Type.PAWN;

        if(color == GamePanel.WHITE){
            image = getImage("/piece/w-pawn");
        }
    else {
            image = getImage("/piece/b-pawn");
        }
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if(isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)){

            //define the direction of movement based on pawn's color
            int moveValue;
            if(color == GamePanel.WHITE){
                moveValue = -1; // pawn moves up
            }
            else { moveValue = 1;} // pawn moves down

            // check the hitting piece
            hittingPiece = getHittingPiece(targetCol, targetRow);

            // 1 square movement
            if(targetCol == preCol && targetRow == preRow + moveValue && hittingPiece == null){
                return true;
            }

            // 2 square movement
            if(targetCol == preCol && targetRow == preRow + 2 * moveValue && hittingPiece == null && !moved && !pieceIsOnStraightLine(targetCol, targetRow)){
                return true;
            }

            // capture piece diagonally
            if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue && hittingPiece != null && hittingPiece.color != color){
                return true;
            }

            //en passant
            if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue){
                for (Piece piece : GamePanel.simPieces) {
                    if(piece.col == targetCol && piece.row == preRow && piece.twoStepped){
                        hittingPiece = piece;
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
