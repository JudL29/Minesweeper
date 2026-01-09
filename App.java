
public class App {
    public static void main(String[] args) throws Exception {
        // Game game = new Game();
        
        Computer computer = new Computer();

        while (true) { 
            computer.solveOneStep();
            Thread.sleep(25); // > 25 for no buggy restarts
        }
    }
}
