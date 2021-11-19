import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class BulbasaurNotFull extends Bulbasaur {
    private int resourceCount;

    public BulbasaurNotFull(String id, Point position,
                            List<PImage> images, int resourceLimit, int resourceCount,
                            int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod, resourceLimit);

        this.resourceCount = resourceCount;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> notFullTarget = world.findNearest(getPosition(),
                Charmander.class);

        if (!notFullTarget.isPresent() ||
                !moveTo(world, notFullTarget.get(), scheduler) ||
                !transform(world, scheduler, imageStore)) {
            scheduleEvent(scheduler,
                    new Activity(this, world, imageStore),
                    getActionPeriod());
        }
    }

    public boolean transform(WorldModel world,
                             EventScheduler scheduler, ImageStore imageStore) {
        if (resourceCount >= getResourceLimit()) {
            Movable bulbasaurFull = new BulbasaurFull(getId(), getPosition(), getImages(),
                    getResourceLimit(), getActionPeriod(), getAnimationPeriod());

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(bulbasaurFull);
            scheduler.scheduleActions(bulbasaurFull, world, imageStore);

            return true;
        }

        return false;
    }


    public void _moveToHelper(WorldModel world, Entity target, EventScheduler scheduler) {
        this.resourceCount += 1;
        world.removeEntity(target);
        scheduler.unscheduleAllEvents(target);
    }

    public int getResourceCount() {
        return resourceCount;
    }
}
