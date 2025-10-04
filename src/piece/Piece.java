package piece;

import module.Board;
import module.GamePanel;
import module.Type;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Piece {

    public Type type;
    public BufferedImage image;
    public int x;
    public int y;
    public int col, row, preCol, preRow;
    public int color;
    public Piece hittingPiece;
    public boolean moved, twoStepped;

    public Piece(int color, int col, int row){
        this.color = color;
        this.col = col;
        this.row = row;
        x = getX(col);
        y = getY(row);
        preCol = col;
        preRow = row;
    }

    public BufferedImage getImage(String imagePath) {
        BufferedImage image = null;

        try{
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
        }catch(IOException e){
            e.printStackTrace();
        }
        return image;
    }

    public int getX(int col){
        return col * Board.SQUARE_SIZE;
    }
    public int getY(int row){
        return row * Board.SQUARE_SIZE;
    }

    public int getCol(int col){
    /*
      x + HALF_SQUARE_SIZE because the program is considering the top left corner of the piece field
    when choosing a position, but we want to consider the position of the cursor, which is in the
    middle of the piece field
    */
        return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    public int getRow(int row){
        return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    public int getIndex(){
        for(int index = 0; index < GamePanel.simPieces.size(); index++){
            if(GamePanel.simPieces.get(index) == this)
                return index;
        }
        return 0;
    }

    public void updatePosition(){

        // check En Passant
        if(type == Type.PAWN){
            if(Math.abs(row - preRow) == 2){
                twoStepped = true;
            }
        }

        x = getX(col);;
        y = getY(row);
        preCol = getCol(x); // update the previous col and row as the move has been confirmed, the piece has moved to a new square
        preRow = getRow(y);
        moved = true;
    }

    public boolean canMove(int targetCol, int targetRow){
        return false;
    }

    public boolean isWithinBoard(int targetCol, int targetRow){
        return targetCol >= 0 && targetRow >= 0 && targetRow <= 7 && targetCol <= 7;
    }

    public void resetPosition(){
        col = preCol;
        row = preRow;
        x = getX(col);
        y = getY(row);
    }

    public Piece getHittingPiece(int targetCol, int targetRow){
        for(Piece piece : GamePanel.simPieces) {
            if(piece.col == targetCol && piece.row == targetRow && piece != this){
                return piece;
            }
        }
        return null;
    }

    public boolean isValidSquare(int targetCol, int targetRow){
        hittingPiece = getHittingPiece(targetCol, targetRow);
        if(hittingPiece == null){ //the square is valid
            return true;
        }
        else{ //the square is occupied
            if(hittingPiece.color != this.color){  // if the color is different, the piece can be captured
                return true;
            }
            else{
                hittingPiece = null;
            }
        }
        return false;
    }

    public boolean isSameSquare(int targetCol, int targetRow){
        if(preCol == targetCol && preRow == targetRow){
            return true;
        }
        return false;
    }

    public boolean pieceIsOnStraightLine(int targetCol, int targetRow){
        // returns true if there is a piece on the same line with the active piece

        // when the piece is moving to the left
        for(int c = preCol - 1; c > targetCol; c--){
            for(Piece piece : GamePanel.simPieces){
                if(piece.col == c && piece.row == targetRow && piece != this){
                    hittingPiece = piece;
                    return true;
                }
            }
        }
        // when the piece is moving to the right
        for(int c = preCol + 1; c < targetCol; c++){
            for(Piece piece : GamePanel.simPieces){
                if(piece.col == c && piece.row == targetRow && piece != this){
                    hittingPiece = piece;
                    return true;
                }
            }
        }
        // when the piece is moving up
        for(int r = preRow - 1; r > targetRow; r--){
            for(Piece piece : GamePanel.simPieces){
                if(piece.row == r && piece.col == targetCol && piece != this){
                    hittingPiece = piece;
                    return true;
                }
            }
        }
        // when the piece is moving down
        for(int r = preRow + 1; r < targetRow; r++){
            for(Piece piece : GamePanel.simPieces){
                if(piece.row == r && piece.col == targetCol && piece != this){
                    hittingPiece = piece;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow){

        if(targetRow < preRow){
            // up left
            for(int c = preCol - 1; c > targetCol; c--){
                for(Piece piece : GamePanel.simPieces){
                    if(piece.col == c && piece.row == preRow - Math.abs(c - preCol)){
                        hittingPiece = piece;
                        return true;
                    }
                }
            }
            // up right
            for(int c = preCol + 1; c < targetCol; c++){
                for(Piece piece : GamePanel.simPieces){
                    if(piece.col == c && piece.row == preRow - Math.abs(c - preCol)){
                        hittingPiece = piece;
                        return true;
                    }
                }
            }
        }

        if(targetRow > preRow){
            // down left
            for(int c = preCol - 1; c > targetCol; c--){
                for(Piece piece : GamePanel.simPieces){
                    if(piece.col == c && piece.row == preRow + Math.abs(c - preCol)){
                        hittingPiece = piece;
                        return true;
                    }
                }
            }

            // down right
            for(int c = preCol + 1; c < targetCol; c++){
                for(Piece piece : GamePanel.simPieces){
                    if(piece.col == c && piece.row == preRow + Math.abs(c - preCol)){
                        hittingPiece = piece;
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void draw(Graphics2D g2){
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }
}
