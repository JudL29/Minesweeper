import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;

public class Game {
    boolean start = false;
    boolean gameOver = false;
    double elapsedTime = 0;

    int totalMines = 99;
    int totalFlags = 99;

    final int rows  = 20;
    final int cols = 24;
    
    final int tileSize = 30;
    
    //int totalMines = 20;
    //int totalFlags = 20;

    //final int rows  = 10;
    //final int cols = 10;


    JFrame frame = new JFrame("MineSweeper");
    JPanel timerPanel = new JPanel();
    JPanel explanationPanel = new JPanel();
    JLabel explanationLabel = new JLabel("Click on a tile to start the game!");
    JButton restartButton = new JButton("Restart");
    JPanel boardPanel = new JPanel();

    Grid[][] grid = new Grid[rows][cols];
    
    Timer gameTimer;
    JLabel timerLabel = new JLabel("Time: 0 seconds");
    Game() {

        // ===== Timer Panel =====
        timerPanel.setBackground(Color.lightGray);
        timerPanel.setPreferredSize(new Dimension(cols * tileSize, 50));
        timerLabel.setFont(new Font("Arial", Font.BOLD, 40));
        timerLabel.setForeground(Color.white);
        timerPanel.add(timerLabel);

        // ===== Explanation Panel =====
        explanationPanel.setBackground(Color.lightGray);
        explanationPanel.setPreferredSize(new Dimension(cols * tileSize, 150));
        explanationLabel.setFont(new Font("Arial", Font.BOLD, 20));
        explanationLabel.setForeground(Color.white);
        explanationPanel.add(explanationLabel);

        // ===== Game Grid Setup =====
        explanationPanel.add(restartButton);
        restartButton.setFont(new Font("Arial", Font.BOLD, 20));
        restartButton.setVisible(false);

        restartButton.addActionListener((ActionEvent e) -> {
            // Reset game state
            start = false;
            gameOver = false;
            elapsedTime = 0;
            totalFlags = totalMines;
            timerLabel.setText("Time: 0 seconds");
            explanationLabel.setText("Click on a tile to start the game!");
            restartButton.setVisible(false);
            gameTimer.stop();
            
            // Reset grid and tiles
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    grid[i][j].reset();
                    grid[i][j].setEnabled(true);
                    grid[i][j].setText("");
                    grid[i][j].setBackground(determineTileColor(i, j, false));
                }
            }
        });

        boardPanel.setLayout(new GridLayout(rows, cols));

        // Initialize grid logic cells
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Grid();
            }
        }

        // Initialize tiles (buttons)
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Grid tile = new Grid();
                grid[i][j] = tile;

                tile.setXCoord(i);
                tile.setYCoord(j);
                tile.setFont(new Font("Arial", Font.BOLD, 16));
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFocusPainted(false);
                tile.setBorder(BorderFactory.createLineBorder(new Color(60, 120, 60), 1));
                tile.setContentAreaFilled(true);
                tile.setOpaque(true);
                tile.setBackground(determineTileColor(i, j, false));

                boardPanel.add(tile);

                int row = i;
                int col = j;

                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        addTileListener(row, col, e);
                    }
                });
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i-1 >= 0){
                    grid[i][j].setNorth(grid[i-1][j]);
                    if (j-1 >= 0) {
                        grid[i][j].setNorthWest(grid[i-1][j-1]);
                    }
                    if (j+1 < cols) {
                        grid[i][j].setNorthEast(grid[i-1][j+1]);
                    }
                }
                if (i+1 < rows){
                    grid[i][j].setSouth(grid[i+1][j]);
                    if (j-1 >= 0) {
                        grid[i][j].setSouthWest(grid[i+1][j-1]);
                    }
                    if(j+1 < cols){
                        grid[i][j].setSouthEast(grid[i+1][j+1]);
                    }
                }
                
                if (j-1 >= 0){
                    grid[i][j].setWest(grid[i][j-1]);
                }
                
                if (j + 1 < cols) {
                    grid[i][j].setEast(grid[i][j+1]);
                }
                
 
                
            }
        }

        // ===== Timer Logic =====
        gameTimer = new Timer(100, (ActionEvent e) -> {
            if (!gameOver) {
                elapsedTime += .1;
                timerLabel.setText("Time: " + (int) elapsedTime + " seconds");
                explanationLabel.setText("Flags remaining: " + totalFlags);
                
            }
        });
        
        // ===== Frame Setup =====
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        frame.add(timerPanel, BorderLayout.NORTH);
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(explanationPanel, BorderLayout.SOUTH);

        frame.setSize(cols * tileSize, rows * tileSize + 200); // Automatically size to fit components
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setResizable(false);
        frame.setVisible(true); // FINAL step (prevents squished layout)
    }

    // ===== Place Mines After First Click =====
    private void placeMines(int startRow, int startCol) {
        int minesPlaced = 0;

        while (minesPlaced < totalMines) {
            int x = (int) (Math.random() * rows);
            int y = (int) (Math.random() * cols);
            ArrayList<Integer> startRows = new ArrayList<> (Arrays.asList(startRow-2, startRow-1, startRow, startRow+1, startRow+2));
            ArrayList<Integer> startCols = new ArrayList<> (Arrays.asList(startCol-2, startCol-1, startCol, startCol+1, startCol+2));
            
            // Skip tiles around starting tiles and already mined ones
            if (!(startRows.contains(x) && startCols.contains(y)) && !grid[x][y].isMine()) {
                grid[x][y].setMine();
                //grid[x][y].setBackground(Color.RED); // For testing purposes, show where mines are placed
                minesPlaced++;
                int iMin = Math.max(0, x - 1);
                int iMax = Math.min(rows - 1, x + 1);
                int jMin = Math.max(0, y - 1);
                int jMax = Math.min(cols - 1, y + 1);
                    for (int i = iMin; i <= iMax; i++) {
                        for (int j = jMin; j <= jMax; j++) {
                            grid[i][j].setNearbyMines(grid[i][j].getNearbyMines() + 1);
                            //grid[i][j].setText(Integer.toString(grid[i][j].getNearbyMines()));
                            if(grid[i][j].getNearbyMines() > 0){
                                //grid[i][j].setText(Integer.toString(grid[i][j].getNearbyMines()));
                                grid[i][j].setForeground(determineFontColor(grid[i][j].getNearbyMines()));
                            }
                        }
                    }
            for (int i = 0; i < rows; i++) {
                for(int j = 0; j < cols; j++){
                    if (grid[i][j].isMine()) {
                        grid[i][j].setNearbyMines(-1);
                        grid[i][j].setForeground(Color.white);
                        grid[i][j].setText("");
                    }
                }
            }
        }
    }
}

    //Method checks if the user wins
    private void revealMines(){
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                if(grid[i][j].isMine()){
                    grid[i][j].setBackground(Color.RED);
                    grid[i][j].setText("M");
                }
            }
        }
    }

    protected void flagTile(Grid tile){
        if(!tile.isFlagged()){
            tile.setFlagged();
            tile.setBackground(Color.BLUE);
            totalFlags--;
        }
        else {
            tile.setOriginalColor();
            tile.setFlagged();
            totalFlags++;
        }
    }

    private boolean win(){
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                if(!grid[i][j].isMine() && !grid[i][j].isRevealed()){
                    return false;
                }
            }
        }
        return true;
    }

    private Color determineTileColor(int x, int y, boolean revealed){
        Color green = new Color(167, 216, 161);
        Color tan = new Color(220, 190, 150);

        Color darkTan = new Color(200, 170, 130);
        Color darkGreen = new Color(76, 175, 80);
        if ((x + y) % 2 == 0) {
            if(revealed) return tan;

            return green;
        }
        else {
            if(revealed) return darkTan;

            return darkGreen;
        }
    }

    private Color determineFontColor(int mines){
        switch (mines) {
            case 1: return Color.BLUE;
            case 2: return new Color(0, 105, 0); // Dark green
            case 3: return Color.RED;
            case 4: return new Color(0, 0, 128); // Dark Blue
            case 5: return new Color(178, 34, 34); //Red-ish
            case 6: return new Color(72, 209, 204); // Teal
            case 7: return Color.GRAY;
            case 8: return Color.DARK_GRAY;
            default: return Color.BLACK;
        }
    }

    private void addTileListener(int row, int col, MouseEvent e){
        if (SwingUtilities.isLeftMouseButton(e) && !e.isControlDown()) {
            if (!start) {
                start = true;
                placeMines(row, col);
                gameTimer.start();
            }
            if(!gameOver && !grid[row][col].isFlagged()){
                removeTile(row, col);
            }
        }
        if (SwingUtilities.isRightMouseButton(e) || SwingUtilities.isLeftMouseButton(e) && e.isControlDown()){
            if(grid[row][col].isRevealed() == false && !gameOver){
                flagTile(grid[row][col]);
            }
        }
    }

    protected void removeTile(int x, int y){
        Grid tile = grid[x][y];
        if(testEndCondition(tile));
        else {
            tile.setRevealed();
            tile.setBackground(determineTileColor(x, y, true));
            if (grid[x][y].getNearbyMines() > 0) {
                grid[x][y].setText(Integer.toString(grid[x][y].getNearbyMines()));
            }
            int iMin = Math.max(0, x - 1);
            int iMax = Math.min(rows - 1, x + 1);
            int jMin = Math.max(0, y - 1);
            int jMax = Math.min(cols - 1, y + 1);
            for (int i = iMin; i <= iMax; i++) {
                for (int j = jMin; j <= jMax; j++) {
                    if(grid[i][j] != null && grid[i][j].isRevealed() == false && tile.getNearbyMines() == 0){
                        if(grid[i][j].isFlagged()){
                            flagTile(grid[i][j]);
                        }
                        removeTile(i, j);
                    }
                }
            }
        }
    }

    private boolean testEndCondition(Grid tile) {
        if(tile.isMine()){
            gameTimer.stop();
            gameOver = true;
            explanationLabel.setText("You Lose! Time: " + (int) elapsedTime + " seconds");
            restartButton.setVisible(true);
            revealMines();
            return true;
        }
        else if(win()){
            gameTimer.stop();
            gameOver = true;
            explanationLabel.setText("You Win! Time: " + (int) elapsedTime + " seconds");
            restartButton.setVisible(true);
            revealMines();
            return true;
        }
        return false;
    }
}
