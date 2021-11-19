import processing.core.PImage;

import java.util.List;

public class Venusaur extends Movable {
    private int steps;
    private ActionEntity bulbasaur;

    private PathingStrategy strategy = new SingleStepPathingStrategy();

    public Venusaur(String id, Point position,
                    List<PImage> images,
                    int actionPeriod, int animationPeriod, ActionEntity bulbasaur) {
        super(id, position, images, actionPeriod, animationPeriod);

        this.bulbasaur = bulbasaur;
        this.steps = 0;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);
        world.addEntity(bulbasaur);
        scheduler.scheduleActions(bulbasaur, world, imageStore);

    }


    public void _moveToHelper(WorldModel world, Entity target, EventScheduler scheduler) {
        world.removeEntity(target);
        scheduler.unscheduleAllEvents(target);
    }

    public Point nextPosition(WorldModel world,
                              Point destPos) {
        List<Point> points;
        points = strategy.computePath(getPosition(), destPos,
                p -> world.withinBounds(p) && !world.isOccupied(p),
                Point::adjacent,
                PathingStrategy.CARDINAL_NEIGHBORS);
        if (points.size() != 0) {
            world.setBackground(points.get(0), world.getBackgroundCell(getPosition()));
            return points.get(0);
        }
        return getPosition();
    }


}
