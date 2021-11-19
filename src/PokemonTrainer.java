import processing.core.PImage;

import java.util.LinkedList;
import java.util.List;

public class PokemonTrainer extends AnimatedEntity{
    private int direction = 0;
    /*  0 = down
        1 = up
        2 = left
        3 = right
     */
    private int catchCount;
    private List<PImage> fullList = super.getImages();

    public PokemonTrainer(String id, Point position, int actionPeriod, int animationPeriod, List<PImage> images, int pokemonCount) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.catchCount = pokemonCount;
    }

    @Override
    protected void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

    }

    public void turn(int n){
        direction = n;
        setImages(Images(n));
    }

    public List<PImage> Images(int n){
            List<PImage> move = new LinkedList<PImage>();
            move.add(fullList.get(n));
            return move;
    }
    public void increaseCatchCount(){
        catchCount++;
    }
    public int getCatchCount(){
        return catchCount;
    }

}
