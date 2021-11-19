import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class Charmander extends ActionEntity {

    private static final Random rand = new Random();

    private static final String CHARIZARD_KEY = "charizard";
    private static final String CHARIZARD_ID_SUFFIX = " -- charizard";
    private static final int CHARIZARD_PERIOD_SCALE = 4;
    private static final int CHARIZARD_ANIMATION_MIN = 50;
    private static final int CHARIZARD_ANIMATION_MAX = 150;

    public Charmander(String id, Point position,
                      List<PImage> images,
                      int actionPeriod) {
        super(id, position, images, actionPeriod, true);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Point pos = getPosition();  // store current position before removing

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        AnimatedEntity charizard = new Charizard(getId() + CHARIZARD_ID_SUFFIX,
                pos, imageStore.getImageList(CHARIZARD_KEY),
                getActionPeriod() / CHARIZARD_PERIOD_SCALE, CHARIZARD_ANIMATION_MIN +
                rand.nextInt(CHARIZARD_ANIMATION_MAX - CHARIZARD_ANIMATION_MIN)
        );

        world.addEntity(charizard);
        scheduler.scheduleActions(charizard, world, imageStore);
    }

}
