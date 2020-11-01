package jv;

public class Main {


    public static void main(String[] args) {
        Board b = new Board();
        Engine e = new Engine();
        e.legalmoves(b);
        e.perft(4, b);
    }
}
