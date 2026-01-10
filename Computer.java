import java.awt.*;
import java.util.*;

public final class Computer extends Game {

    public void runThroughBothOnce() {
        runThroughEasyTillNoChange();

        runThroughHardTillNoChange();
    }

    public void runThroughEasyTillNoChange() {
        boolean change;
        do {
            change = false;
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    Grid tile = grid[row][col];
                    if (!tile.isRevealed()) continue;

                    change |= findBomb(row, col);
                    change |= clearTile(row, col);
                }
            }
        }
        while (change);
    }

    public void runThroughHardTillNoChange() {
        boolean change;
        do {
            change = false;
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    Grid tile = grid[row][col];
                    if (!tile.isRevealed()) continue;

                    change |= setTheory(row, col);
                }
            }
        }
        while (change);
    }
    
    private boolean findBomb(int x, int y){
        int mines = grid[x][y].getNearbyMines();
        if (mines == 0) return false;

        int iMin = Math.max(0, x - 1);
        int iMax = Math.min(rows - 1, x + 1);
        int jMin = Math.max(0, y - 1);
        int jMax = Math.min(cols - 1, y + 1);
        int unclearedTiles = 0;
        boolean flaggedTile = false;

        for (int i = iMin; i <= iMax; i++) {
            for (int j = jMin; j <= jMax; j++) {
                if (!grid[i][j].isRevealed()) unclearedTiles++;
            }
        }

        if (mines == unclearedTiles){
            for (int i = iMin; i <= iMax; i++) {
                for (int j = jMin; j <= jMax; j++) {
                    if (!grid[i][j].isRevealed() && !grid[i][j].isFlagged()){
                        flagTile(grid[i][j]);
                        flaggedTile = true;
                    }
                }
            }
        }
        return flaggedTile;
    }
    
    private boolean clearTile(int x, int y){
        int iMin = Math.max(0, x - 1);
        int iMax = Math.min(rows - 1, x + 1);
        int jMin = Math.max(0, y - 1);
        int jMax = Math.min(cols - 1, y + 1);
        int minesFlagged = 0;
        int unclearedTiles = 0;
        Grid tile = grid[x][y];
        boolean clearedTile = false;

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
        if (minesFlagged == tile.getNearbyMines() && unclearedTiles > tile.getNearbyMines()) {
            for (int i = iMin; i <= iMax; i++) {
                for(int j = jMin; j <= jMax; j++) {
                    if (!grid[i][j].isRevealed() && !grid[i][j].isFlagged()) {
                        removeTile(i, j);
                        clearedTile = true;
                    }
                }
            }
        }
        return clearedTile;
    }

    private boolean setTheory(int row, int col){
        Grid tile = grid[row][col];
        if (!tile.isRevealed() || tile.getNearbyMines() == 0) return false;

        int localMineOriginal = tile.getNearbyMines();
        Set<Grid> originalGroup = new HashSet<>();
        ArrayList<Grid> possiblePair = new ArrayList<>();

        for (Grid originNeighbor : tile.getCardinalDirections()) {
            if (originNeighbor == null) continue;

            if (originNeighbor.isFlagged()){
                localMineOriginal--;
            }
            else if (!originNeighbor.isRevealed()) {
                originalGroup.add(originNeighbor);
            }
            
            if (originNeighbor.isRevealed() && originNeighbor.getNearbyMines() != 0){
                possiblePair.add(originNeighbor);
            }
            
        }

        if (localMineOriginal == 0) {
            for (Grid revealThis : originalGroup){
                removeTile(revealThis.getXCoord(), revealThis.getYCoord());
            }
            return true;
        }
        
        if (localMineOriginal == originalGroup.size()) {
            for (Grid flagThis : originalGroup){
                flagTile(flagThis);
                flagThis.setBackground(Color.PINK);
            }
            return true;
        }
        
        for (Grid pair : possiblePair) {
            int localMinePair = pair.getNearbyMines();
            Set<Grid> pairGroup = new HashSet<>();
                
            for (Grid pairNeighbor : pair.getCardinalDirections()) {
                if (pairNeighbor == null) continue;

                if (pairNeighbor.isFlagged()){
                    localMinePair--;
                }
                else if (!pairNeighbor.isRevealed()) {
                    pairGroup.add(pairNeighbor);
                }
            }

            int diff = localMineOriginal - localMinePair;

            // Exclusive sets
            Set<Grid> originalExclusive = new HashSet<>(originalGroup);
            originalExclusive.removeAll(pairGroup);

            Set<Grid> pairExclusive = new HashSet<>(pairGroup);
            pairExclusive.removeAll(originalGroup);

            if (diff == 0) {
                if (originalExclusive.isEmpty() && !pairExclusive.isEmpty()) {
                    for (Grid removeExact : pairExclusive) removeTile(removeExact.getXCoord(), removeExact.getYCoord());
                    return true;
                }

                if (!originalExclusive.isEmpty() && pairExclusive.isEmpty()) {
                    for (Grid removeExact : originalExclusive) removeTile(removeExact.getXCoord(), removeExact.getYCoord());
                    return true;
                }
            }
            else if (diff > 0 && diff == originalExclusive.size()){
                removeSetTheory(pairExclusive, originalExclusive);
                return true;
            }
            else if (-diff == pairExclusive.size()){
                removeSetTheory(originalExclusive, pairExclusive);
                return true;
            }
        }
        return false;
    }

    private void removeSetTheory(Set<Grid> reveal, Set<Grid> flag) {
        for (Grid revealThat : reveal){
            removeTile(revealThat.getXCoord(), revealThat.getYCoord());
        }
        for (Grid flagThat : flag){
            if (!flagThat.isFlagged()) {
                flagTile(flagThat);
                flagThat.setBackground(Color.PINK);
            }
        }
    }

    private void probability(){
        
    }
/*
    Depreciated methods

    int ticker = 0;

    public void solveOnePass() {
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

    private void clearTiles(){
        for (int row = 0; row < rows; row++){
            for(int col = 0; col < cols; col++){
                if (grid[row][col].isRevealed()) clearTile(row, col);
            }
        }
    }
    private void setTheories(){
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                setTheory(row, col);
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
*/
}