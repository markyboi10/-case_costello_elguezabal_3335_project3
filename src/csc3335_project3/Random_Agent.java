package csc3335_project3;

import csc3335.gipf_game.GipfGame;
import csc3335.gipf_game.GipfPlayable;
import java.time.Instant;
import java.util.Random;

/**
 *
 * @author stuetzlec
 */
public class Random_Agent implements GipfPlayable {

    private String[] edgeSpots;
    private String[] columns;
    private Random rng;

    public Random_Agent(GipfGame g) {
        this.edgeSpots = g.getEdgeSpots();
       // this.columns = g.getColumns();
        //rng = new Random(37);
        rng = new Random(Instant.now().toEpochMilli());
    }

    @Override
    public String makeGipfMove(int curPlayer) {
        String startLocation = edgeSpots[rng.nextInt(edgeSpots.length)];
        Integer dir = rng.nextInt(6);        
        return startLocation + " " + Integer.toString(dir);
        //return "i 1 5";
    }

}
