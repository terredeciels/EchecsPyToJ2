package jv;


import java.util.ArrayList;
import java.util.Arrays;

public class Board {

    public Piece[] cases;
    public boolean white_can_castle_63;
    public boolean white_can_castle_56;
    public boolean black_can_castle_7;
    public boolean black_can_castle_0;
    public int ep;
    ArrayList<MoveHistory> history;
    String[] coord = {

            "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
            "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
            "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
            "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
            "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
            "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
            "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
            "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1",
    };
    String side2move;
    int ply;
    private Piece pieceDeplacee;
    private Piece piecePrise;
    private boolean isEp;
    private int histEp;
    private boolean hist_roque_63;
    private boolean hist_roque_56;
    private boolean hist_roque_0;
    private boolean hist_roque_7;
    private boolean flagViderEp;

    public Board() {
        //cases = new Piece[64];
        cases = new Piece[]{
                new Piece("TOUR", "noir"),
                new Piece("CAVALIER", "noir"),
                new Piece("FOU", "noir"),
                new Piece("DAME", "noir"),
                new Piece("ROI", "noir"),
                new Piece("FOU", "noir"),
                new Piece("CAVALIER", "noir"),
                new Piece("TOUR", "noir"),

                new Piece("PION", "noir"), new Piece("PION", "noir"), new Piece("PION", "noir"), new Piece("PION", "noir"), new Piece("PION", "noir"), new Piece("PION", "noir"), new Piece("PION", "noir"), new Piece("PION", "noir"),
                new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(),
                new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(),
                new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(),
                new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(),
                new Piece("PION", "blanc"), new Piece("PION", "blanc"), new Piece("PION", "blanc"), new Piece("PION", "blanc"), new Piece("PION", "blanc"), new Piece("PION", "blanc"), new Piece("PION", "blanc"), new Piece("PION", "blanc"),
                new Piece("TOUR", "blanc"), new Piece("CAVALIER", "blanc"), new Piece("FOU", "blanc"), new Piece("DAME", "blanc"), new Piece("ROI", "blanc"), new Piece("FOU", "blanc"), new Piece("CAVALIER", "blanc"), new Piece("TOUR", "blanc")
        };


        side2move = "blanc";
        ep = -1; // the number of the square where to take 'en pasant'
        history = new ArrayList<>();
        ply = 0; // half-move number since the start

        // Castle rights
        white_can_castle_56 = true;
        white_can_castle_63 = true;
        black_can_castle_0 = true;
        black_can_castle_7 = true;
    }

    public String oppColor(String c) {
        // "Returns the opposite color of the "c" color given"

        if (c == "blanc")
            return "noir";
        else
            return "blanc";
    }

    public boolean is_attacked(int pos, String couleur) {
//        """Returns TRUE or FALSE if the square number "pos" is a
//        destination square for the color "couleur".
//        If so we can say that "pos" is attacked by this side.
//        This function is used for "in check" and for castle moves."""

        ArrayList<Move> mList = gen_moves_list(couleur, true);

        for (Move m : mList) {
            if (m.pos2 == pos)
                return true;
        }
        return false;
    }

    ArrayList<Move> gen_moves_list(String color, boolean dontCallIsAttacked) {

//        """Returns all possible moves for the requested color.
//        If color is not given, it is considered as the side to move.
//        dontCallIsAttacked is a boolean flag to avoid recursive calls,
//        due to the actually wrotten is_attacked() function calling
//        this gen_moves_list() function.
//        A move is defined as it :
//        - the number of the starting square (pos1)
//        - the number of the destination square (pos2)
//        - the name of the piece to promote "","q","r","b","n"
//          (queen, rook, bishop, knight)
//        """


        if (color.equals("")) color = side2move;
        ArrayList<Move> mList = new ArrayList<>();

        // For each "piece" on the board(pos1 = 0to 63)
        for (int pos1 = 0; pos1 < 64; pos1++) {
            Piece piece = cases[pos1];
            //Piece(or empty square) color is not the wanted ? pass
            if (!piece.couleur.equals(color))
                continue;

            if (piece.nom == "ROI") { //#KING
                mList.addAll(piece.pos2_roi(pos1, oppColor(color), this, dontCallIsAttacked));
                continue;
            } else if (piece.nom == "DAME") {// QUEEN = ROOK + BISHOP moves !
                mList.addAll(piece.pos2_tour(pos1, oppColor(color), this));
                mList.addAll(piece.pos2_fou(pos1, oppColor(color), this));
                continue;
            } else if (piece.nom == "TOUR") { // ROOK
                mList.addAll(piece.pos2_tour(pos1, oppColor(color), this));
                continue;
            } else if (piece.nom == "CAVALIER") { // KNIGHT
                mList.addAll(piece.pos2_cavalier(pos1, oppColor(color), this));
                continue;
            } else if (piece.nom == "FOU") { // BISHOP
                mList.addAll(piece.pos2_fou(pos1, oppColor(color), this));
                continue;
            }
            if (piece.nom == "PION") { // PAWN
                mList.addAll(piece.pos2_pion(pos1, piece.couleur, this));
                continue;
            }
        }
        return mList;
    }

    public int ROW(int x) {
        return (x >> 3);
    }

    boolean domove(int depart, int arrivee, String promote) {

//        """Move a piece on the board from the square numbers
//        "depart" to "arrivee" (0..63) respecting rules :
//        - prise en passant
//        - promote and under-promote
//        - castle rights
//        Returns :
//        - TRUE if the move do not let king in check
//        - FALSE otherwise and undomove is done.
//        """

//        # Debugging tests
//        #if(self.cases[depart].isEmpty()):
//        #    print("domove() ERROR : asked for an empty square move : ",depart,arrivee,promote)
//        #    return
//        #if(int(depart)<0 or int(depart)>63):
//        #    print("domove() ERROR : incorrect FROM square number : ",depart)
//        #    return
//        #if(int(arrivee)<0 or int(arrivee)>63):
//        #    print("domove() ERROR : incorrect TO square number : ",arrivee)
//        #    return
//        #if(not(promote=="" or promote=="q" or promote=="r" or promote=="n" or promote=="b")):
//        #    print("domove() ERROR : incorrect promote : ",promote)
//        #    return

        // Informations to save in the history moves
        pieceDeplacee = cases[depart]; // moved piece
        piecePrise = cases[arrivee]; // taken piece, can be null : Piece()
        isEp = false; // will be used to undo a ep move
        histEp = ep; // saving the actual ep square (-1 or square number TO)
        hist_roque_56 = white_can_castle_56;
        hist_roque_63 = white_can_castle_63;
        hist_roque_0 = black_can_castle_0;
        hist_roque_7 = black_can_castle_7;
        flagViderEp = true; // flag to erase ep or not : if the pawn moved is not taken directly, it can"t be taken later

        // Moving piece
        cases[arrivee] = cases[depart];
        cases[depart] = new Piece();

        ply += 1;

        // a PAWN has been moved -------------------------------------
        if (pieceDeplacee.nom.equals("PION")) {

            //White PAWN
            if (pieceDeplacee.couleur.equals("blanc")) {

                //If the move is "en passant"
                if (ep == arrivee) {
                    piecePrise = cases[arrivee + 8]; //take black pawn
                    cases[arrivee + 8] = new Piece();
                    isEp = true;
                }
                //The white pawn moves 2 squares from starting square
                //then blacks can take "en passant" next move
                else if (ROW(depart) == 6 && ROW(arrivee) == 4) {
                    ep = arrivee + 8;
                    flagViderEp = false;
                }
            }
            //Black PAWN
            else {

                if (ep == arrivee) {
                    piecePrise = cases[arrivee - 8];
                    cases[arrivee - 8] = new Piece();
                    isEp = true;
                } else if (ROW(depart) == 1 && ROW(arrivee) == 3) {
                    ep = arrivee - 8;
                    flagViderEp = false;
                }
            }
        }
        // a ROOK has been moved--------------------------------------
        // update castle rights

        else if (pieceDeplacee.nom.equals("TOUR")) {

            // White ROOK
            if (pieceDeplacee.couleur.equals("blanc")) {
                if (depart == 56)
                    white_can_castle_56 = false;
                else if (depart == 63)
                    white_can_castle_63 = false;
            }
            // Black ROOK
            else {
                if (depart == 0)
                    black_can_castle_0 = false;
                else if (depart == 7)
                    black_can_castle_7 = false;
            }
            // a KING has been moved-----------------------------------------
        } else if (pieceDeplacee.nom.equals("ROI")) {

            // White KING
            if (pieceDeplacee.couleur.equals("blanc")) {

                // moving from starting square
                if (depart == 60) {
                    // update castle rights
                    white_can_castle_56 = false;
                    white_can_castle_63 = false;

                    // If castling, move the rook
                    if (arrivee == 58) {
                        cases[56] = new Piece();
                        cases[59] = new Piece("TOUR", "blanc");
                    } else if (arrivee == 62) {
                        cases[63] = new Piece();
                        cases[61] = new Piece("TOUR", "blanc");
                    }
                }
            }
            // Black KING
            else {

                if (depart == 4) {
                    black_can_castle_0 = false;
                    black_can_castle_7 = false;

                    if (arrivee == 6) {
                        cases[7] = new Piece();
                        cases[5] = new Piece("TOUR", "noir");
                    } else if (arrivee == 2) {
                        cases[0] = new Piece();
                        cases[3] = new Piece("TOUR", "noir");
                    }
                }
            }
        }

        // End pieces cases-----------------------------------------------

        // Any move cancels the ep move
        if (flagViderEp)
            ep = -1;

        // Promote : the pawn is changed to requested piece
        if (!promote.equals("")) {
            if (promote.equals("q"))
                cases[arrivee] = new Piece("DAME", side2move);
            else if (promote.equals("r"))
                cases[arrivee] = new Piece("TOUR", side2move);
            else if (promote.equals("n"))
                cases[arrivee] = new Piece("CAVALIER", side2move);
            else if (promote.equals("b"))
                cases[arrivee] = new Piece("FOU", side2move);
        }
        // Change side to move
        changeTrait();

        // Save move to the history list

        history.add(new MoveHistory(depart, arrivee, pieceDeplacee, piecePrise, isEp, histEp, promote,
                hist_roque_56, hist_roque_63, hist_roque_0, hist_roque_7));

        // If the move lets king in check, undo it and return false
        if (in_check(oppColor(side2move))) {
            undomove();
            return false;
        }
        return true;

    }

    void changeTrait() {

        //  "Change the side to move"

        if (side2move.equals("blanc"))
            side2move = "noir";
        else
            side2move = "blanc";
    }

    boolean in_check(String couleur) {
        //  """Returns TRUE or FALSE
        // if the KING of the given "color" is in check"""

        // Looking for the id square where is the king
        // sure, we can code better to avoid this and win kn/s...
        int pos = 0;// ??
        for (int i = 0; i < 64; i++) {
            if (cases[i].nom.equals("ROI") && cases[i].couleur == couleur) {
                pos = i;
                break;
            }
        }
        return is_attacked(pos, oppColor(couleur));
    }

    void undomove() {
        // "Undo the last move in history"

        if (history.isEmpty()) {
            System.out.println("No move played");
            return;
        }

        // The last move in history is : self.historique[-1]
//        MoveHistory(int depart, int arrivee, Piece pieceDeplacee, Piece piecePrise,
//        boolean isEp, int histEp, String promote,
//        boolean hist_roque_56, boolean hist_roque_63, boolean hist_roque_0, boolean hist_roque_7)
        MoveHistory lastmove = history.get(history.size() - 1); // ??

        int pos1 = lastmove.getDepart();
        int pos2 = lastmove.getArrivee();
        Piece piece_deplacee = lastmove.getPieceDeplacee();
        Piece piece_prise = lastmove.getPiecePrise();
        boolean isEp = lastmove.isEp();
        int ep = lastmove.getHistEp();
        String promote = lastmove.getPromote();
        white_can_castle_56 = lastmove.isHist_roque_56();
        white_can_castle_63 = lastmove.isHist_roque_63();
        black_can_castle_0 = lastmove.isHist_roque_0();
        black_can_castle_7 = lastmove.isHist_roque_7();

        ply -= 1;

        // Change side to move
        changeTrait();

        // Replacing piece on square number "pos1"
        cases[pos1] = cases[pos2];

        // Square where we can take "en pasant"
        this.ep = ep;

        // If undoing a promote, the piece was a pawn
        if (!promote.equals(""))
            cases[pos1] = new Piece("PION", side2move);

        // Replacing capture piece on square "pos2"
        cases[pos2] = piece_prise;

        // Switch the piece we have replaced to "pos1"-------------------
        if (cases[pos1].nom.equals("PION")) {
            // If a pawn has been taken "en passant", replace it
            if (isEp) {
                cases[pos2] = new Piece();
                if (cases[pos1].couleur.equals("noir"))
                    cases[pos2 - 8] = new Piece("PION", "blanc");
                else
                    cases[pos2 + 8] = new Piece("PION", "noir");
            }
        }
        // Replacing KING -----------------------------------------------
        else if (cases[pos1].nom.equals("ROI")) {

            // White KING
            if (cases[pos1].couleur.equals("blanc")) {
                // Replacing on initial square
                if (pos1 == 60) {
                    // If the move was castle, replace ROOK
                    if (pos2 == 58) {
                        cases[56] = new Piece("TOUR", "blanc");
                        cases[59] = new Piece();
                    } else if (pos2 == 62) {
                        cases[63] = new Piece("TOUR", "blanc");
                        cases[61] = new Piece();
                    }
                }
            }
            //Black KING
            else {
                if (pos1 == 4) {
                    if (pos2 == 2) {
                        cases[0] = new Piece("TOUR", "noir");
                        cases[3] = new Piece();
                    } else if (pos2 == 6) {
                        cases[7] = new Piece("TOUR", "noir");
                        cases[5] = new Piece();
                    }
                }
            }
        }
        // End switch piece----------------------------------------------

        // Delete the last move from history
        history.remove(history.size() - 1);

    }

    public String caseInt2Str(int i) {
//        """Given in argument : an integer between 0 and 63
//        Returns a string like "e2""""

        String err =
                "Square number must be in 0 to 63";

//        char letters;
//        letters=("a","b","c","d","e","f","g","h";
//        char numbers;
//        numbers=("1","2","3","4","5","6","7","8");

        if (i < 0 || i > 63)
            System.out.println(err);
        //return;

        return coord[i];
    }

    public int caseStr2Int(String c) {
//        """'c' given in argument is a square name like 'e2'
//        "This functino returns a square number like 52"""

        String[] err = {
                "The square name must be 2 caracters i.e. e2,e4,b1...",
                "Incorrect square name. Please enter i.e. e2,e4,b1..."
        };
        String letters = "abcdefgh";
        String numbers = "12345678";

        if (c.length() != 2) {
            System.out.print(err[0]);
            return -1;
        }
        if (!letters.contains(Character.toString(c.charAt(0)))) {
            System.out.print(err[1]);
            return -1;
        }
        if (!numbers.contains(Character.toString(c.charAt(1)))) {
            System.out.print(err[1]);
            return -1;
        }
        int ret = Arrays.asList(coord).indexOf(c);
        return ret;

    }

    int evaluer() {

//        """A wonderful evaluate() function
//        returning actually only the material score"""

        int WhiteScore = 0;
        int BlackScore = 0;

        // Parsing the board squares from 0 to 63

        for (int pos1 = 0; pos1 < 64; pos1++) {

            Piece piece = cases[pos1];
            //Material score
            final int valeur = piece.valeur;
            if (piece.couleur.equals("blanc"))
                WhiteScore += valeur;
            else
                // NB:here is for black piece or empty square
                BlackScore += valeur;
        }
        if (side2move.equals("blanc"))
            return WhiteScore - BlackScore;
        else
            return BlackScore - WhiteScore;

    }

    void render() {
        // System.out.println("Side to move : " + side2move);
        showHistory();
    }

    void showHistory() {

        // "Displays the history of the moves played"

        if (history.size() == 0)
            return;

        //System.out.println();
        //cpt, aff = 1.0, True
//        for (depart, \
//    arrivee, \
//    pieceDeplacee, \
//    piecePrise, \
//    isEp, \
//    histEp, \
//    promote, \
//    roque56, \
//    roque63, \
//    roque0, \
//    roque7) in self.history:
        String a, b;
        double cpt = 0.0;
        boolean aff = false;
        for (MoveHistory h : history) {
            final int depart = h.getDepart();
            a = caseInt2Str(depart);
            final int arrivee = h.getArrivee();
            b = caseInt2Str(arrivee);
            System.out.print(" " + a + "," + b);
//            if (!piecePrise.isEmpty())
//                a = a + "x";
//            if (!h.getPromote().equals(""))
//                b = b + h.getPromote();
//
//            if (aff) {
//                //print("{}.{}{} ".format(int(cpt), a, b),end = ' ')
//                System.out.println((int) (cpt) + a  + b +" ");
//                aff = false;
//            } else {
//               System.out.println(a  + b + " ");
//                aff = true;
//            }
//            cpt += 0.5;
        }

        System.out.println();
        //System.out.println();
    }
}



