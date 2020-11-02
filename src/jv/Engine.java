package jv;

import java.util.ArrayList;

public class Engine {

    private final int init_depth;
    private boolean endgame;

    public Engine() {
        endgame = false;
        init_depth = 4; // search in fixed depth
        //nodes = 0  // number of nodes
        //clear_pv();
    }

    void legalmoves(Board b) {

        //  "Show legal moves for side to move"

        ArrayList<Move> mList = b.gen_moves_list("", false);

        int cpt = 1;
        for (Move m : mList) {
            if (!b.domove(m.pos1, m.pos2, m.s)) continue;
            System.out.println("move #" + cpt + ":" + b.caseInt2Str(m.pos1) + b.caseInt2Str(m.pos2) + m.s);
            b.undomove();
            cpt += 1;
        }
    }

    void perft(int depth, Board b) {

//        """PERFformance Test :
//        This is a debugging function through the move generation tree
//        for the current board until depth [x].
//        'c' is the command line written by user : perft [x]
//        """

        // Checking the requested depth
        // cmd = c.split()
        // cmd[0]='perft'
//
//        try:
//            d = int(cmd[1])
//        except ValueError:
//            print('Please type an integer as depth i.e. : perft 5')
//            return

//        if (d < 1 or d > self.MAX_PLY):
//            print('Depth must be between 1 and', self.MAX_PLY)
//            return

        // System.out.print("Depth\tNodes\tCaptures\tE.p.\tCastles\tPromotions\tChecks\tCheckmates");

        //time1 = get_ms();
        for (int i = 2; i <= depth + 1; i++) {
            int total = perftoption(0, i - 1, b);
            //System.out.print("{}\t{}".format(i, total));
            System.out.println("depth " + i + ":" + total);
        }
        // time2 = =get_ms();
        // timeDiff = round((time2 - time1) / 1000, 2)
        // print('Done in', timeDiff, 's')
    }

    int perftoption(int prof, int limit, Board b) {
        int cpt = 0;

        if (prof > limit) return 0;

        ArrayList<Move> l = b.gen_moves_list("", false);

        for (int i = 0; i < l.size(); i++) {
            Move m = l.get(i);
            if (!b.domove(m.pos1, m.pos2, m.s))
                continue;

            cpt += perftoption(prof + 1, limit, b);

            if (limit == prof)
                cpt += 1;

            b.undomove();
        }
        return cpt;
    }

    public void undomove(Board b) {
        // "The user requested a 'undomove' in command line"

        b.undomove();
        endgame = false;
    }

    public void usermove(Board b, String c) {
//
//        """Move a piece for the side to move, asked in command line.
//        The command 'c' in argument is like 'e2e4' or 'b7b8q'.
//        Argument 'b' is the chessboard.
//        """

        if (endgame) {
            print_result(b);
            return;
        }

        // Testing the command 'c'. Exit if incorrect.
        String chk = chkCmd(c);
        if (!chk.equals("")) {
            System.out.print(chk);
            return;
        }
        // Convert cases names to int, ex : e3 -> 44
        int pos1 = b.caseStr2Int(c.charAt(0) + Character.toString(c.charAt(1)));
        int pos2 = b.caseStr2Int(c.charAt(2) + Character.toString(c.charAt(3)));

        // Promotion asked ?
        String promote = "";
        if (c.length() > 4) {
            promote = Character.toString(c.charAt(4));
            if (promote.equals("q"))
                promote = "q";
            else if (promote.equals("r"))
                promote = "r";
            else if (promote.equals("n"))
                promote = "n";
            else if (promote.equals("b"))
                promote = "b";
        }
        // Generate moves list to check
        // if the given move (pos1,pos2,promote) is correct
        ArrayList<Move> mList = b.gen_moves_list("", false);

        // The move is not in list ? or let the king in check ?
        Move m = new Move(pos1, pos2, promote);
        //boolean b1 = !mList.contains(m);
        boolean b1 = false;
        for (Move mv : mList) {
            if (mv.pos1 == m.pos1 && mv.pos2 == m.pos2 && mv.s.equals(m.s)) {
                b1 = true;
                break;
            }
        }
        final boolean b2 = !b.domove(pos1, pos2, promote);
        if (!b1 || b2) {
            System.out.print("\n" + c + " : incorrect move or let king in check" + "\n");
            return;
        }
        // Display the chess board
        // b.render();

        // Check if game is over
        print_result(b);

        // Let the engine play
        // self.search(b)
    }

    private void print_result(Board b) {

        //  "Check if the game is over and print the result"

        // Is there at least one legal move left ?
        boolean f = false;

        for (Move m : b.gen_moves_list("", false)) {
            if (b.domove(m.pos1, m.pos2, m.s)) {
                b.undomove();
                f = true;  //yes, a move can be done
                break;
            }
        }
        // No legal move left, print result
        if (!f) {
            if (b.in_check(b.side2move)) {
                if (b.side2move.equals("blanc"))
                    System.out.print("0-1 {Black mates}");
                else
                    System.out.print("1-0 {White mates}");
            } else {
                System.out.print("1/2-1/2 {Stalemate}");
                endgame = true;
            }
        }
//        # TODO
//        # 3 reps
//        # 50 moves rule
    }

    private String chkCmd(String c) {
//      """Check if the command 'c' typed by user is like a move,
//        i.e. 'e2e4','b7b8n'...
//        Returns '' if correct.
//        Returns a string error if not.
//        """

        String[] err = {
                "The move must be 4 or 5 letters : e2e4, b1c3, e7e8q...", "Incorrect move."};

        String letters = "abcdefgh";
        String numbers = "12345678";

        if (c.length() < 4 || c.length() > 5)
            return err[0];

        if (!letters.contains(Character.toString(c.charAt(0))))
            return err[1];

        if (!numbers.contains(Character.toString(c.charAt(1))))
            return err[1];

        if (!letters.contains(Character.toString(c.charAt(2))))
            return err[1];

        if (!numbers.contains(Character.toString(c.charAt(3))))
            return err[1];

        return "";

    }
//
//    public void setboard(Board b, String c) {
////        """Set the chessboard to the FEN position given by user with
////        the command line 'setboard ...'.
////        'c' in argument is for example :
////        'setboard 8/5k2/5P2/8/8/5K2/8/8 w - - 0 0'
////        """
//
//        String[] cmd = c.split(" "); //  # split command with spaces
//        cmd.pop(0); //  # drop the word 'setboard' written by user
//
//
//
//        // set the FEN position on board
//        if (b.setboard(' '.join(cmd)))
//        endgame = false; //  # success, so no endgame
//    }

//    int get_ms() {
//        return  round(time.time() * 1000);
//    }
}
