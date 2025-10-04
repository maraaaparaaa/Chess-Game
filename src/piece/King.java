package piece;

import module.GamePanel;
import module.Type;

public class King extends Piece {

    public King(int color, int col, int row) {
        super(color, col, row);
        type = Type.KING;

        if (color == GamePanel.WHITE) {
            image = getImage("/piece/w-king");
        }
        else{
            image = getImage("/piece/b-king");
        }
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {

        if(isWithinBoard(targetCol, targetRow)){
            if(Math.abs(targetCol-preCol) + Math.abs(targetRow-preRow) == 1 ||
                    Math.abs(targetCol-preCol) * Math.abs(targetRow-preRow) == 1){
                if(isValidSquare(targetCol, targetRow)){
                    return true;
                }
            }

            // castling
            if(!moved) {

                // right castling
                if(targetCol == preCol + 2 && targetRow == preRow && !pieceIsOnStraightLine(targetCol, targetRow)){
                    for(Piece piece: GamePanel.simPieces) {
                        if (piece.col == preCol + 3 && piece.row == preRow && !piece.moved) {
                            GamePanel.castlingPiece = piece;
                            return true;
                        }
                    }
                }

                boolean ok = true;
                // left castling
                if(targetCol == preCol - 2 && targetRow == preRow && !pieceIsOnStraightLine(targetCol, targetRow)){
                    for(Piece piece: GamePanel.simPieces) {

                        if(piece.col == preCol - 3 && piece.row == preRow) {
                            ok = false;
                        }

                        if (piece.col == preCol - 4 && piece.row == preRow && !piece.moved) {
                            GamePanel.castlingPiece = piece;
                        }
                    }

                    return ok;
                }

            }
        }
        return false;
    }
}
