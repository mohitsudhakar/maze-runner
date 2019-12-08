import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SolveMaze {


    private Color[] color;          // colors associated with the preceding 5 constants;    
    private int pathIndex;
    private int roomIndex;
    private int visitedIndex;

    GraphicsContext g;  // graphics context for drawing on the canvas

    private int rows;          // number of rows of cells in maze, including a wall around edges
    private int columns ;       // number of columns of cells in maze, including a wall around edges
    private int squareSize ;     // size of each cell
    private int delay ;    // short delay between steps in making and solving maze


    SolveMaze(GraphicsContext graphicsContext, Color[] color, int rows, int columns) {
        this.g = graphicsContext;
        this.color = color;
        this.rows = rows;
        this.columns = columns;
        this.squareSize  = Constants.squareSize;
        this.delay = Constants.delay;

        this.roomIndex = Constants.roomIndex;
        this.visitedIndex = Constants.visitedIndex;
        this.pathIndex = Constants.pathIndex;
        System.out.println(this.rows);
        System.out.println(this.columns);

    }


    boolean solveMaze(int row, int col, int[][] maze) {
        // Try to solve the maze by continuing current path from position
        // (row,col).  Return true if a solution is found.  The maze is
        // considered to be solved if the path reaches the lower right cell.
        if (maze[row][col] == roomIndex) {
            maze[row][col] = pathIndex;      // add this cell to the path
            drawSquare(row,col,pathIndex);
            if (row == rows-2 && col == columns-2) {
                System.out.println("Path has reached goal");
                return true;  // path has reached goal
            }
            try { Thread.sleep(delay); }
            catch (InterruptedException e) { }
            if ( solveMaze(row+1,col, maze)  ||
                    solveMaze(row,col+1, maze)  ||
                    solveMaze(row-1,col, maze)  ||
                    solveMaze(row,col-1, maze) )
                return true;
            // backtrack
            maze[row][col] = visitedIndex;   // mark visited
            drawSquare(row,col,visitedIndex);
            synchronized(this) {
                try { wait(delay); }
                catch (InterruptedException e) { }
            }
        }
        return false;
    }


    void drawSquare( int row, int column, int colorIndex ) {
        // Fill specified square of the grid with the
        // color specified by colorIndex, which has to be
        // one of the constants roomIndex, wallIndex, etc.
        Platform.runLater( () -> {
            g.setFill( color[colorIndex] );
            int x = squareSize * column;
            int y = squareSize * row;
            g.fillRect(x,y,squareSize,squareSize);
        });
    }


}