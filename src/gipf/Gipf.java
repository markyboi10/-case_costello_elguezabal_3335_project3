package gipf;


import csc3335.gipf_game.GipfGame;
import csc3335_project2.Random_Agent;
import gipf.intelligence.Agent;
import lombok.Getter;

import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Mark Case
 */
public class Gipf {

    @Getter
    private static GipfGame gipfGame;

    private static Agent agent;

    public static void main(String[] args) {

        // Initialize a new GiphGame
        gipfGame = new GipfGame(ThreadLocalRandom.current().nextInt(1,100));

        // Initialize the Agent
        agent = new Agent(gipfGame);

        gipfGame.playGame(agent, new Random_Agent(gipfGame));

    }
    
}
