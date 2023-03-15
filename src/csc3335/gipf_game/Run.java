package csc3335.gipf_game;

/**
 *
 * @author stetz
 */
public class Run {
    
    public final Integer runLength;
    public final Integer playerValue;
    public final Integer startingCol;
    public final Integer startingPos;
    public final Integer direction;
    
    /**
     * The Run class holds a single run of like pieces.
     * @param r The length of the run
     * @param v The player whose pieces make up the run
     * @param c The column the run starts in
     * @param p The position in the column the run starts in
     * @param d The direction of the run
     */
    public Run(Integer r, Integer v, Integer c, Integer p, Integer d){
        runLength = r;
        playerValue = v;
        startingCol = c;
        startingPos = p;
        direction = d;
    }
    
}
