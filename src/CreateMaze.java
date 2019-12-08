import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CreateMaze {

    private Color[] color;          // colors associated with the preceding 5 constants;    
    private int wallIndex;
    private int roomIndex;
    private int[][] maze;

    private GraphicsContext g;  // graphics context for drawing on the canvas

    private int rows;          // number of rows of cells in maze, including a wall around edges
    private int columns ;       // number of columns of cells in maze, including a wall around edges
    private int squareSize ;     // size of each cell
    private int delay ;    // short delay between steps in making and solving maze


    CreateMaze(GraphicsContext graphicsContext, Color[] color, int rows, int cols) {
        this.g = graphicsContext;
        this.color = color;
        this.rows = rows;
        this.columns = cols;
        this.squareSize  = Constants.squareSize;
        this.delay = Constants.delay;

        this.roomIndex = Constants.roomIndex;
        this.wallIndex = Constants.wallIndex;


        this.maze = new int[this.rows][this.columns];
//        maze[this.rows-1][this.columns-1] = roomIndex;
//        drawSquare(this.rows-1, this.columns-1, roomIndex);

    }

    int[][] makeMaze() {
        // Create a random maze.  The strategy is to start with
        // a grid of disconnected "rooms" separated by walls,
        // then look at each of the separating walls, in a random
        // order.  If tearing down a wall would not create a loop
        // in the maze, then tear it down.  Otherwise, leave it in place.
        int i,j;
        int emptyCt = 0; // number of rooms
        int wallCt = 0;  // number of walls
        int[] wallrow = new int[(rows*columns)/2];  // position of walls between rooms
        int[] wallcol = new int[(rows*columns)/2];
        for (i = 0; i<rows; i++)  // start with everything being a wall
            for (j = 0; j < columns; j++)
                maze[i][j] = wallIndex;
        for (i = 1; i<rows-1; i += 2)  { // make a grid of empty rooms
            for (j = 1; j<columns-1; j += 2) {
                emptyCt++;
                maze[i][j] = -emptyCt;  // each room is represented by a different negative number
                if (i < rows-2) {  // record info about wall below this room
                    wallrow[wallCt] = i+1;
                    wallcol[wallCt] = j;
                    wallCt++;
                }
                if (j < columns-2) {  // record info about wall to right of this room
                    wallrow[wallCt] = i;
                    wallcol[wallCt] = j+1;
                    wallCt++;
                }
            }
        }

        System.out.println(wallrow);System.out.println(wallcol);
        Platform.runLater( () -> {
            g.setFill( color[roomIndex] );
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < columns; c++) {
                    if (maze[r][c] < 0)
                        g.fillRect( c*squareSize, r*squareSize, squareSize, squareSize );
                }
            }
        });

        synchronized(this) {
            try { wait(1000); }
            catch (InterruptedException e) { }
        }
        int r;
        for (i=wallCt-1; i>0; i--) {
            r = (int)(Math.random() * i);  // choose a wall randomly and maybe tear it down
            removeWall(wallrow[r],wallcol[r]);
            wallrow[r] = wallrow[i];
            wallcol[r] = wallcol[i];
        }
        for (i=1; i<rows-1; i++)  // replace negative values in maze[][] with roomIndex
            for (j=1; j<columns-1; j++)
                if (maze[i][j] < 0)
                    maze[i][j] = roomIndex;
        synchronized(this) {
            try { wait(1000); }
            catch (InterruptedException e) { }
        }

        return maze;
    }

    void removeWall(int row, int col) {
        // Tear down a wall, unless doing so will form a loop.  Tearing down a wall
        // joins two "rooms" into one "room".  (Rooms begin to look like corridors
        // as they grow.)  When a wall is torn down, the room codes on one side are
        // converted to match those on the other side, so all the cells in a room
        // have the same code.  Note that if the room codes on both sides of a
        // wall already have the same code, then tearing down that wall would
        // create a loop, so the wall is left in place.
        if (row % 2 == 1 && maze[row][col-1] != maze[row][col+1]) {
            // row is odd; wall separates rooms horizontally
            fill(row, col-1, maze[row][col-1], maze[row][col+1]);
            maze[row][col] = maze[row][col+1];
            drawSquare(row,col,roomIndex);
            synchronized(this) {
                try { wait(delay); }
                catch (InterruptedException e) { }
            }
        }
        else if (row % 2 == 0 && maze[row-1][col] != maze[row+1][col]) {
            // row is even; wall separates rooms vertically
            fill(row-1, col, maze[row-1][col], maze[row+1][col]);
            maze[row][col] = maze[row+1][col];
            drawSquare(row,col,roomIndex);
            synchronized(this) {
                try { wait(delay); }
                catch (InterruptedException e) { }
            }
        }
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


    void fill(int row, int col, int replace, int replaceWith) {
        // called by removeWall() to change "room codes".
        // (My algorithm really should have used the standard
        //  union/find data structure.)
        if (maze[row][col] == replace) {
            maze[row][col] = replaceWith;
            fill(row+1,col,replace,replaceWith);
            fill(row-1,col,replace,replaceWith);
            fill(row,col+1,replace,replaceWith);
            fill(row,col-1,replace,replaceWith);
        }
    }
}