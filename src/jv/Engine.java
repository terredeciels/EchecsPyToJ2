package jv;

import java.util.ArrayList;

public class Engine {

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
//        cmd = c.split()
//        // cmd[0]='perft'
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
//    int get_ms() {
//        return  round(time.time() * 1000);
//    }
}
