package module;

import piece.*;

import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable {
    public static final int SCREEN_WIDTH = 1100;
    public static final int SCREEN_HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    //pieces
    public static ArrayList<Piece> pieces = new ArrayList<>();  //back-up list
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    public static ArrayList<Piece> promoPieces = new ArrayList<>();
    Piece activePiece, checkingPiece;
    public static Piece castlingPiece;

    //color
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;  //game starts from white

    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameover;
    boolean staleMate;

    public GamePanel() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);

        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        setPieces();
        //testPromotion();
        //testIllegal();

        copyPieces(pieces, simPieces);
    }

    public void launchGame(){
        gameThread = new Thread(this);  //intantiate the thread
        gameThread.start();   // call the run method
    }

    public void setPieces(){
        //White team
        pieces.add(new Pawn(WHITE, 0, 6));
        pieces.add(new Pawn(WHITE, 1, 6));
        pieces.add(new Pawn(WHITE, 2, 6));
        pieces.add(new Pawn(WHITE, 3, 6));
        pieces.add(new Pawn(WHITE, 4, 6));
        pieces.add(new Pawn(WHITE, 5, 6));
        pieces.add(new Pawn(WHITE, 6, 6));
        pieces.add(new Pawn(WHITE, 7, 6));

        pieces.add(new Rook(WHITE, 0, 7));
        pieces.add(new Rook(WHITE, 7, 7));

        pieces.add(new Knight(WHITE, 1, 7));
        pieces.add(new Knight(WHITE, 6, 7));

        pieces.add(new Bishop(WHITE, 2, 7));
        pieces.add(new Bishop(WHITE, 5, 7));

        pieces.add(new Queen(WHITE, 3, 7));
        pieces.add(new King(WHITE, 4, 7));


        //Black team
        pieces.add(new Pawn(BLACK, 0, 1));
        pieces.add(new Pawn(BLACK, 1, 1));
        pieces.add(new Pawn(BLACK, 2, 1));
        pieces.add(new Pawn(BLACK, 3, 1));
        pieces.add(new Pawn(BLACK, 4, 1));
        pieces.add(new Pawn(BLACK, 5, 1));
        pieces.add(new Pawn(BLACK, 6, 1));
        pieces.add(new Pawn(BLACK, 7, 1));

        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Rook(BLACK, 7, 0));

        pieces.add(new Knight(BLACK, 1, 0));
        pieces.add(new Knight(BLACK, 6, 0));

        pieces.add(new Bishop(BLACK, 2, 0));
        pieces.add(new Bishop(BLACK, 5, 0));

        pieces.add(new Queen(BLACK, 3, 0));
        pieces.add(new King(BLACK, 4, 0));

    }

    public void testPromotion(){
        pieces.add(new Pawn(BLACK, 0, 5));
        pieces.add(new Pawn(BLACK, 5, 6));
        pieces.add(new Pawn(WHITE, 5, 2));
    }

    public void testIllegal(){
        pieces.add(new Pawn(WHITE, 7, 6));
        pieces.add(new King(WHITE, 3, 7));
        pieces.add(new King(BLACK, 0, 3));
        pieces.add(new Bishop(BLACK, 1, 4));
        pieces.add(new Queen(BLACK, 4, 5));
    }

    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target){
        target.clear();
        for (Piece piece : source) {
            target.add(piece);
        }
    }

    @Override
    public void run() {  // we create a game loop
        double drawInterval = (double) 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread!=null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if(delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update(){

        if(promotion){
            promoting();
        }else if(!gameover && !staleMate){
            //mouse button pressed
            if(mouse.pressed){
                if(activePiece == null){ //the player is not holding a piece
                    // if activePiece is null, check if you can pick up a piece
                    for (Piece piece : simPieces) {
                        // if the mouse is on an available piece, pick it up as activePiece
                        if(piece.color == currentColor &&
                                piece.col == mouse.x/Board.SQUARE_SIZE &&
                                piece.row == mouse.y/Board.SQUARE_SIZE){
                            activePiece = piece;
                        }
                    }
                }
                else{  //the player is already holding a piece, then simulate the move
                    simulate();
                }
            }

            //mouse button released
            if(!mouse.pressed){

                if(activePiece != null){ //if holding a piece
                    if(validSquare){
                        //move confirmed

                        //update the piece list in case a piece has been captured
                        copyPieces(simPieces, pieces);
                        activePiece.updatePosition();

                        if(castlingPiece!=null){
                            castlingPiece.updatePosition();
                        }

                        if(isKingInCheck() && isCheckmate()){
                            gameover = true;
                        }
                        else if(isStaleMate()){
                            staleMate = true; // gameover - draw
                        }
                        else{ //the game is still going on
                            if(canPromote()) {
                                promotion = true;
                            }
                            else{
                                changePlayer();
                            }
                        }
                    }
                    else {
                        //the move is not valid, reset everything
                        copyPieces(pieces, simPieces);//restore the original list
                        activePiece.resetPosition();
                        activePiece = null;
                    }
                }
            }
        }
    }

    private void simulate(){

        canMove = false;
        validSquare = false;

        //reset the pieces list in every loop - during the simulation, every piece touched by the mouse disappears
        copyPieces(pieces, simPieces);

        // reset the castling piece position
        if(castlingPiece!=null){
            castlingPiece.col = castlingPiece.preCol;
            castlingPiece.x = castlingPiece.getX(castlingPiece.col);
            castlingPiece = null;
        }

        // if piece is being held, update its position
        activePiece.x = mouse.x - Board.SQUARE_SIZE/2;
        activePiece.y = mouse.y - Board.SQUARE_SIZE/2;

        activePiece.col = activePiece.getCol(activePiece.x);
        activePiece.row = activePiece.getRow(activePiece.y);

        if(activePiece.canMove(activePiece.col, activePiece.row)){
            canMove = true;

            // if hitting a piece, remove it from the list
            if(activePiece.hittingPiece!=null){
                simPieces.remove(activePiece.hittingPiece.getIndex());
            }
            checkCastling();

            if(!isIllegal(activePiece) && !opponentCanCaptureKing()) {
                validSquare = true;
            }
        }
    }

    public boolean isKingInCheck(){

        Piece king = getKing(true); //get the opponent king
        if(activePiece.canMove(king.col, king.row)){
            checkingPiece = activePiece;
            return true;
        }
        else{
            checkingPiece = null;
        }
        return false;
    }

    public Piece getKing(boolean opponent){ // true - opponent king, false - your own king

        Piece king = null;

        for (Piece piece : pieces) {
            if(opponent){
                if(piece.type == Type.KING && piece.color != currentColor){
                    king = piece;
                }
            }
            else{
                if(piece.type == Type.KING && piece.color == currentColor){
                    king = piece;
                }
            }
        }

        return king;
    }

    public boolean isCheckmate(){

        Piece king = getKing(true); //opponent king

        if(kingCanMove(king)){
            return false; //not checkmate
        }
        else{
            //there is no square that the king can move to
            // check if player can block the attack

            //check the position of the checking piece and the king in check
            int colDiff = Math.abs(checkingPiece.col - king.col);
            int rowDiff = Math.abs(checkingPiece.row - king.row);

            if(colDiff == 0){
                //the checking piece is attacking vertically
                if(checkingPiece.row < king.row){
                    //the checking piece is above the king
                    for (int row = checkingPiece.row; row < king.row; row++) {
                        for (Piece piece : simPieces) {
                            if(piece != king && piece.color != currentColor && piece.canMove(checkingPiece.col, row)){
                                return false;
                            }
                        }
                    }
                }
                if(checkingPiece.row > king.row){
                    //the checking piece is below the king
                    for (int row = checkingPiece.row; row > king.row; row--) {
                        for (Piece piece : simPieces) {
                            if(piece != king && piece.color != currentColor && piece.canMove(checkingPiece.col, row)){
                                return false;
                            }
                        }
                    }
                }
            }
            else if(rowDiff == 0) {
                //the checking piece is to the left
                if (checkingPiece.col < king.col) {
                    //the checking piece is below the king
                    for (int col = checkingPiece.col; col < king.col; col++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, checkingPiece.row)) {
                                return false;
                            }
                        }
                    }
                }
                if (checkingPiece.col > king.col) {
                    //the checking piece is to the right
                    for (int col = checkingPiece.col; col > king.col; col--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, checkingPiece.row)) {
                                return false;
                            }
                        }
                    }
                }
            }
            else if(colDiff == rowDiff){
                // The checking piece is attacking diagonally
                if (checkingPiece.row < king.row) {
                    // The checking piece is above the king
                    if (checkingPiece.col < king.col) {
                        // The checking piece is in the upper left
                        for (int col = checkingPiece.col, row = checkingPiece.row; col < king.col; col++, row++) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor &&
                                        piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }

                    if (checkingPiece.col > king.col) {
                        // The checking piece is in the upper right
                        for (int col = checkingPiece.col, row = checkingPiece.row; col > king.col; col--, row++) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor &&
                                        piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                }

                if (checkingPiece.row > king.row) {
                    // The checking piece is below the king
                    if (checkingPiece.col < king.col) {
                        // The checking piece is in the lower left
                        for (int col = checkingPiece.col, row = checkingPiece.row; col < king.col; col++, row--) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor &&
                                        piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }

                    if (checkingPiece.col > king.col) {
                        // The checking piece is in the lower right
                        for (int col = checkingPiece.col, row = checkingPiece.row; col > king.col; col--, row--) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor &&
                                        piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
            else {
                //the checking piece is knight
                //knight's atack cannot be blocked
            }
        }
        return true;
    }

    public boolean kingCanMove( Piece king){

        //simulate if there is any square where the king can move to

        if(isValidMove(king, -1, -1)) return true;
        if(isValidMove(king, 0, -1)) return true;
        if(isValidMove(king, 1, -1)) return true;
        if(isValidMove(king, -1, 0)) return true;
        if(isValidMove(king, 1, 0)) return true;
        if(isValidMove(king, -1, 1)) return true;
        if(isValidMove(king, 0, 1)) return true;
        if(isValidMove(king, 1, 1)) return true;

        return false; // there is no square that the king can move
    }

    public boolean isValidMove(Piece king, int colPlus, int rowPlus){

        boolean isValidMove = false;

        //update the king's position for a second
        king.col += colPlus;
        king.row += rowPlus;

        if(king.canMove(king.col, king.row)){
            if(king.hittingPiece != null){
                simPieces.remove(king.hittingPiece.getIndex());
            }
            if(!isIllegal(king)){
                isValidMove = true;
            }
        }

        //reset the king's position and restore the removed piece
        king.resetPosition();
        copyPieces(pieces, simPieces); //we need to simulate all directions

        return isValidMove;
    }

    public void checkCastling(){
        if(castlingPiece != null){
            if(castlingPiece.col == 0){
                castlingPiece.col += 3;
            }
            else if(castlingPiece.col == 7){
                castlingPiece.col -= 2;
            }
            castlingPiece.x = castlingPiece.getX(castlingPiece.col);
        }
    }

    public boolean isIllegal(Piece king){

        if(king.type == Type.KING){
            for (Piece piece : simPieces) {
                if(piece != king && piece.color != king.color && piece.canMove(king.col, king.row)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean opponentCanCaptureKing(){
        Piece king = getKing(false); //current color king

        for (Piece piece : simPieces) {
            if(piece.color != king.color && piece.canMove(king.col, king.row)){
                return true;
            }
        }
        return false;
    }

    public void changePlayer(){
        if(currentColor == WHITE){
            currentColor = BLACK;
            //reset black's two stepped status
            for (Piece piece : pieces) {
                if(piece.color == BLACK){
                    piece.twoStepped = false;
                }
            }
        }
        else {
            currentColor = WHITE;
            //reset white's two stepped status
            for (Piece piece : pieces) {
                if(piece.color == WHITE){
                    piece.twoStepped = false;
                }
            }
        }
        activePiece = null;
    }

    public boolean canPromote(){
        if(activePiece.type == Type.PAWN){
            if(currentColor == WHITE && activePiece.row == 0 || currentColor == BLACK && activePiece.row == 7){
                promoPieces.clear();
                promoPieces.add(new Rook(currentColor, 9, 2));
                promoPieces.add(new Knight(currentColor, 9, 3));
                promoPieces.add(new Bishop(currentColor, 9, 4));
                promoPieces.add(new Queen(currentColor, 9, 5));
                return true;
            }
        }
        return false;
    }

    public void promoting(){
        if(mouse.pressed){
            for (Piece piece : promoPieces) {
                if(piece.col == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE){
                    switch (piece.type){
                        case ROOK: simPieces.add(new Rook(currentColor, activePiece.col, activePiece.row)); break;
                        case KNIGHT: simPieces.add(new Knight(currentColor, activePiece.col, activePiece.row)); break;
                        case BISHOP: simPieces.add(new Bishop(currentColor, activePiece.col, activePiece.row)); break;
                        case QUEEN: simPieces.add(new Queen(currentColor, activePiece.col, activePiece.row)); break;
                        default: break;
                    }
                    simPieces.remove(activePiece.getIndex()); // remove pawn
                    copyPieces(simPieces,pieces); //update backup list
                    activePiece = null;
                    promotion = false;
                    changePlayer();
                }
            }
        }
    }

    public boolean isStaleMate(){
        int count = 0;
        //Count the number of pieces
        for (Piece piece : pieces) {
            if(piece.color != currentColor){ //count the nr of pieces of the opponent, stalemate can happen when only the king is left
                count++;
            }
        }

        //if only the king piece is left
        if(count == 1){
            if(!kingCanMove(getKing(true))) {
                return true;  //stalemate
            }
        }
        return false;
    }

    public void paintComponent(Graphics g){   //drawing
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        //BOARD
        board.draw(g2);

        //PIECES
        for (Piece piece : pieces) {
            piece.draw(g2);
        }

        if(activePiece != null){
            if(canMove) {
                if(isIllegal(activePiece) || opponentCanCaptureKing()){
                    g2.setColor(Color.gray); //set color
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f)); //change opacity
                    g2.fillRect(activePiece.col*Board.SQUARE_SIZE, activePiece.row*Board.SQUARE_SIZE,
                            Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }else{
                    // the color of the square where activePiece is currently on changes
                    g2.setColor(Color.white); //set color
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f)); //change opacity
                    g2.fillRect(activePiece.col*Board.SQUARE_SIZE, activePiece.row*Board.SQUARE_SIZE,
                            Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }
            }
            // draw the active piece in the end so it won't be hidden by the board or the colored square
            activePiece.draw(g2);
        }

        // status message
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON); // gives the text a smoother appearance
        g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
        g2.setColor(Color.white);

        if(promotion) {
            g2.drawString("Promote to: ", 840, 150);
            for (Piece piece : promoPieces) {
                g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row), Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
            }
        }
        else{
            if(currentColor == BLACK){
                g2.drawString("Black's turn", 840, 250);
                if(checkingPiece != null && checkingPiece.color == WHITE){
                    g2.setColor(Color.red);
                    g2.drawString("The king", 840, 100);
                    g2.drawString("is in check", 840, 150);
                }
            }
            else{
                g2.drawString("White's turn", 840, 550);
                if(checkingPiece != null && checkingPiece.color == BLACK){
                    g2.setColor(Color.red);
                    g2.drawString("The king", 840, 650);
                    g2.drawString("is in check", 840, 700);
                }
            }
        }

        if(gameover){
            String s = "";
            if(currentColor == WHITE){
                s = "White wins";
            }
            else{
                s = "Black wins";
            }
            g2.setFont(new Font("Arial", Font.PLAIN, 90));
            g2.setColor(Color.green);
            g2.drawString(s, 200, 420);
        }

        if(staleMate){
            g2.setFont(new Font("Arial", Font.PLAIN, 90));
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString("DRAW", 200, 420);
        }
    }
}
