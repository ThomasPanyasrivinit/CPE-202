import processing.core.PImage;

import java.util.List;

final class Munchlax extends AnimatedEntity{


    public Munchlax(String id, Point position,
                    List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    @Override
    protected void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        scheduler.scheduleActions(this, world, imageStore);
    }



}