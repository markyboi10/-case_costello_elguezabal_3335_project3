package csc3335_project3;

import csc3335.gipf_game.GipfGame;
import gipf.intelligence.SuperDuperGipfWinner5000;

/**
 *
 * @author stuetzlec
 */
public class CSC3335_Project3 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Initialize the game with a random seed
        GipfGame g = new GipfGame( 85 );
        // Play the game with two agents
        int gamesPlayed = 0;
        int numWins = 0;
        int starveWins = 0;
        int gipfWins = 0;
        int numLosses = 0;
        int starveLosses = 0;
        int gipfLosses = 0;
        while(gamesPlayed < 10) {
            int result = g.playGame(new SuperDuperGipfWinner5000(g), new Random_Agent(g));

            System.out.println("Result: " + result);

            if(result == 1 || result == 3) {
                numWins++;
                if(result == 1) starveWins++; else gipfWins++;
            }
            else {
                numLosses++;
                if(result == 2) starveLosses++; else gipfLosses++;
            }
            gamesPlayed++;
        }
        //}
        System.out.println("Number of Wins: " + numWins);
        System.out.println("Gipf Wins: " + gipfWins);
        System.out.println("Starve Wins: " + starveWins);
        System.out.println("Number of Losses: " + numLosses);
        System.out.println("Gipf Losses: " + gipfLosses);
        System.out.println("Starve Losses: " + starveLosses);
        
        // testing copy constructor
        // GipfGame g2 = new GipfGame(g);
    }
    
}
