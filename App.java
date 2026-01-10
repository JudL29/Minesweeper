
public class App {
    public static void main(String[] args) throws Exception {
        // Game game = new Game();
        
        Computer computer = new Computer();

        while (true) { 
            computer.runThroughBothOnce();
            Thread.sleep(30); // > 25 for smaller chance of buggy restarts
        }
    }
}
