import java.awt.*;
import java.util.*;

public final class Computer extends Game {

    int ticker = 0;

    public void solveOneStep() {
        if (!gameOver) {
            findBombs();
            clearTiles();
            if (ticker % 4 == 3){
                setTheories();
            }
            ticker++;
        }
    }

    private void findBombs(){
        for (int row = 0; row < rows; row++){
            for(int col = 0; col < cols; col++){
                if (grid[row][col].isRevealed()) {
                    findBomb(row, col);
                }
            }
        }
    }
    
    private void findBomb(int x, int y){
        int iMin = Math.max(0, x - 1);
        int iMax = Math.min(rows - 1, x + 1);
        int jMin = Math.max(0, y - 1);
        int jMax = Math.min(cols - 1, y + 1);
        int unclearedTiles = 0;
            for (int i = iMin; i <= iMax; i++) {
                for (int j = jMin; j <= jMax; j++) {
                    if (!grid[i][j].isRevealed()){
                        unclearedTiles++;
                    }
                }
            }
            if (grid[x][y].getNearbyMines() == unclearedTiles){
                for (int i = iMin; i <= iMax; i++) {
                    for (int j = jMin; j <= jMax; j++) {
                        if (!grid[i][j].isRevealed() && !grid[i][j].isFlagged()){
                            flagTile(grid[i][j]);
                        }
                    }
                }
            }
    }
    
    private void clearTiles(){
        massUnflagIncorrect();
        for (int row = 0; row < rows; row++){
            for(int col = 0; col < cols; col++){
                if (grid[row][col].isRevealed() && clearTile(row, col));
            }
        }
    }
    
    private boolean clearTile(int x, int y){
        int iMin = Math.max(0, x - 1);
        int iMax = Math.min(rows - 1, x + 1);
        int jMin = Math.max(0, y - 1);
        int jMax = Math.min(cols - 1, y + 1);
        int minesFlagged = 0;
        int unclearedTiles = 0;
        Grid tile = grid[x][y];
        for (int i = iMin; i <= iMax; i++) {
            for(int j = jMin; j <= jMax; j++) {
                if (!grid[i][j].isRevealed()){
                    unclearedTiles++;
                }
                if (grid[i][j].isFlagged()) {
                    minesFlagged++;
                }
            }
        }
        if (minesFlagged > tile.getNearbyMines()) {
            unflagIncorrectAroundTile(tile);
            return false;
        }
        else if (minesFlagged == tile.getNearbyMines() && unclearedTiles > tile.getNearbyMines()) {
            for (int i = iMin; i <= iMax; i++) {
                for(int j = jMin; j <= jMax; j++) {
                    if (!grid[i][j].isRevealed() && !grid[i][j].isFlagged()) {
                        removeTile(i, j);
                    }
                }
            }
        }
        return true;
    }

    private void setTheories(){
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                setTheory(row, col);
            }
        }
    }

    private void setTheory(int row, int col){
        if (grid[row][col].isRevealed() && grid[row][col].getNearbyMines() != 0) {
            int localMineOriginal = grid[row][col].getNearbyMines();

            ArrayList<Grid> possiblePair = new ArrayList<>();

            ArrayList<Grid> originalGroup = new ArrayList<>();

            for (Grid directions : grid[row][col].getCardinalDirections()) {
                if (directions != null) {
                    if (!directions.isRevealed() && !directions.isFlagged()){
                        originalGroup.add(directions);
                    }
                    if (directions.isRevealed() && directions.getNearbyMines() != 0){
                        possiblePair.add(directions);
                    }
                }
                if (directions != null && directions.isFlagged()){
                    localMineOriginal--;
                }
            }

            if (originalGroup.isEmpty() || possiblePair.isEmpty() || originalGroup.isEmpty()){
                return;
            }

            for (int i = 0; i < possiblePair.size(); i++){
                ArrayList<Grid> pairGroup = new ArrayList<>();
                int localMinePair = possiblePair.get(i).getNearbyMines();

                ArrayList<Grid> originalExclusiveGroup = new ArrayList<>();
                ArrayList<Grid> pairExclusiveGroup = new ArrayList<>();
                
                for (Grid directions : possiblePair.get(i).getCardinalDirections()) {
                    if (directions != null && !directions.isRevealed() && !directions.isFlagged()) {
                        pairGroup.add(directions);
                        if (!originalGroup.contains(directions)){
                            pairExclusiveGroup.add(directions);
                        }
                    }
                    if (directions != null && directions.isFlagged()){
                        localMinePair--;
                    }
                }

                for (Grid oUnrevealed : originalGroup){
                    if (!pairGroup.contains(oUnrevealed)){
                        originalExclusiveGroup.add(oUnrevealed);
                    }
                }
                
                if (localMineOriginal > localMinePair){
                    if (localMineOriginal - localMinePair == originalExclusiveGroup.size() && !originalExclusiveGroup.isEmpty()) {
                        for (Grid revealThis : pairExclusiveGroup){
                            removeTile(revealThis.getXCoord(), revealThis.getYCoord());
                        }
                        for (Grid flagThat : originalExclusiveGroup){
                            if (!flagThat.isFlagged()) {
                                flagTile(flagThat);
                                flagThat.setBackground(Color.PINK);
                            }
                        }
                    }
                }
                else if (localMineOriginal < localMinePair) {
                    if (localMinePair - localMineOriginal == pairExclusiveGroup.size() && !pairExclusiveGroup.isEmpty()) {
                        for (Grid revealThis : originalExclusiveGroup){
                            removeTile(revealThis.getXCoord(), revealThis.getYCoord());
                        }
                        for (Grid flagThat : pairExclusiveGroup){
                            if (!flagThat.isFlagged()) {
                                flagTile(flagThat);
                                flagThat.setBackground(Color.PINK);
                            }
                            
                        }
                    }
                }

            }
        
        }
    }

    private void unflagIncorrectAroundTile(Grid tile){
        for (Grid directions : tile.getCardinalDirections()) {
            if (directions != null && directions.isFlagged()){
                flagTile(tile);
            }
        }
    } 

    private void massUnflagIncorrect(){
        for(int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                if (grid[i][j].isRevealed()){
                    int flagAmount = 0;
                    ArrayList<Grid> flaggedTiles = new ArrayList<>();
                    for (Grid directions : grid[i][j].getCardinalDirections()) {
                        if (directions != null && directions.isFlagged()){
                            flagAmount++;
                            flaggedTiles.add(directions);
                        }
                    }
                    if (flagAmount > grid[i][j].getNearbyMines()) {
                        for (Grid flagged : flaggedTiles){
                            flagged.setFlagged();
                        }
                    }
                }
            }
        }
    } 
    
    private void probability(){
        
    }
}