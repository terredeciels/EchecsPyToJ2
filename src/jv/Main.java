package jv;

public class Main {


    public static void main(String[] args) {
        Board board = new Board();
        Engine engine = new Engine();
        engine.legalmoves(board);
        // python
//        1 20
//        2	400
//        3	8902
//        4	197281
//        5	4865609
        // java
//        depth 2:400
//        depth 3:8902
//        depth 4:197281
//        depth 5:4865609
        engine.perft(3, board);
    }
}
