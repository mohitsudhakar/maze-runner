import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Creates a random maze, then solves it by finding a path from the
 * upper left corner to the lower right corner.  After doing
 * one maze, it waits a while then starts over by creating a
 * new random maze.  The point of the program is to visualize
 * the process.
 */
public class RunMaze extends Application implements Runnable {

    public static void main(String[] args) {
        launch(args);
    }
    //------------------------------------------------------------------------

    int[][] maze;   // Description of state of maze.  The value of maze[i][j]
    // is one of the constants wallIndex, pathcode, roomIndex,
    // or visitedIndex.  (Value can also be negative, temporarily,
    // inside createMaze().)
    //    A maze is made up of walls and corridors.  maze[i][j]
    // is either part of a wall or part of a corridor.  A cell
    // that is part of a corridor is represented by pathIndex
    // if it is part of the current path through the maze, by
    // visitedIndex if it has already been explored without finding
    // a solution, and by roomIndex if it has not yet been explored.

    Canvas canvas;      // the canvas where the maze is drawn and which fills the whole window
    GraphicsContext context;  // graphics context for drawing on the canvas

    Color[] color;          // colors associated with the preceding 5 constants;
    
    public void start(Stage stage) {
        color = new Color[] {
                Color.rgb(0,0,0),
                Color.rgb(0,0,0),
                Color.rgb(255,80,80),
                Color.WHITE,
                Color.rgb(200,150,150)
        };


        int rows = Constants.rows;
        int cols = Constants.columns;
        rows = rows % 2 == 0 ? rows + 1 : rows;
        cols = cols % 2 == 0 ? cols + 1 : cols;

        canvas = new Canvas(cols*Constants.squareSize, rows*Constants.squareSize);
        context = canvas.getGraphicsContext2D();
        context.setFill(color[Constants.backgroundIndex]);
        context.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        Pane root = new Pane(canvas);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Maze Generator/Solve");
        stage.show();
        Thread runner = new Thread(this);
        runner.setDaemon(true);  // so thread won't stop program from ending
        runner.start();
    }


    public void run() {
        int rows = Constants.rows;
        int cols = Constants.columns;
        rows = rows % 2 == 0 ? rows + 1 : rows;
        cols = cols % 2 == 0 ? cols + 1 : cols;

        CreateMaze mazeCreator = new CreateMaze(context, color, rows, cols);
        SolveMaze mazeSolver = new SolveMaze(context, color, rows, cols);

        try { Thread.sleep(1000); }
        catch (InterruptedException e) { }

        maze = mazeCreator.makeMaze();
        System.out.println("Maze generated");

        try { Thread.sleep(1000); }
        catch (InterruptedException e) { }

        mazeSolver.solveMaze(1, 1, maze);
        System.out.println("Maze solved");

    }

}
