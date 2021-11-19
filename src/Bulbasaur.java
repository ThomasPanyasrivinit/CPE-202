import processing.core.PImage;

import java.util.List;

public abstract class Bulbasaur extends Movable {

    private PathingStrategy strategy = new AStarPathingStrategy();

    private int resourceLimit;

    public Bulbasaur(String id, Point position,
                     List<PImage> images, int actionPeriod, int animationPeriod, int resourceLimit) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.resourceLimit = resourceLimit;
    }

    protected int getResourceLimit() { return resourceLimit; }

    protected abstract int getResourceCount();


}
