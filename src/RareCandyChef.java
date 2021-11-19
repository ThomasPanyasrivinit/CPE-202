import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class RareCandyChef extends Movable {


    private static final String SNORLAX_KEY = "snorlax";

    private PathingStrategy strategy = new AStarPathingStrategy();

    private int steps;

    public RareCandyChef(String id, Point position,
                         List<PImage> images,
                         int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.steps = 0;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> gymLeaderTarget = world.findNearest(getPosition(), Munchlax.class);
        long nextPeriod = getActionPeriod();

        if (gymLeaderTarget.isPresent()) {
            Point tgtPos = gymLeaderTarget.get().getPosition();

            if (moveTo(world, gymLeaderTarget.get(), scheduler)) {
                Entity snorlax = new Snorlax(SNORLAX_KEY, tgtPos,   //to be replaced
                        imageStore.getImageList(SNORLAX_KEY));

                world.addEntity(snorlax);
                nextPeriod += getActionPeriod();
            }
        }

        scheduleEvent(scheduler,
                new Activity(this, world, imageStore),
                nextPeriod);

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
        incrementStep(world);
        if (points.size() != 0) {
//            world.setBackground(points.get(0), world.getBackgroundCell(getPosition()));
            return points.get(0);
        }
        return getPosition();
    }

    private void incrementStep(WorldModel world) {
        if (steps > 30) {
            world.removeEntity(this);
        }
        else
            steps += 1;
    }

}
