package jv;

public class Main {


    public static void main(String[] args) {
        Board b = new Board();
        Engine e = new Engine();
        e.legalmoves(b);
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
        e.perft(4, b);
    }
}
