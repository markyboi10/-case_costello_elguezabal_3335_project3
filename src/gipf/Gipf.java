package gipf;


import csc3335.gipf_game.GipfGame;
import csc3335_project3.Random_Agent;
import gipf.intelligence.State;
import gipf.intelligence.SuperDuperGipfWinner5000;
import lombok.Getter;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Mark Case
 */
public class Gipf {

    @Getter
    private static GipfGame gipfGame;

    private static SuperDuperGipfWinner5000 agent;

    public static void main(String[] args) {

        // Initialize a new GiphGame
        gipfGame = new GipfGame(ThreadLocalRandom.current().nextInt(1,100));

        // Initialize the Agent
        agent = new SuperDuperGipfWinner5000(gipfGame);

        // Play the game
        gipfGame.playGame(agent, new Random_Agent(gipfGame));
    }
    
}
