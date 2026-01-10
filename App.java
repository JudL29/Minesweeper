
public class App {
    public static void main(String[] args) throws Exception {
        // Game game = new Game();
        
        Computer computer = new Computer();

        while (true) { 
            computer.runThroughBothOnce();
            Thread.sleep(100); //To make restarts less buggy, need to incorporate wait into restart later
        }
    }
}
