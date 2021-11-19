import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Berries extends ActionEntity {

    private static final Random rand = new Random();

    private static final String CHARMANDER_ID_PREFIX = "charmander -- ";
    private static final int CHARMANDER_CORRUPT_MIN = 20000;
    private static final int CHARMANDER_CORRUPT_MAX = 30000;

    private static final String CHARMANDER_KEY = "charmander";

    public Berries(String id, Point position,
                   List<PImage> images, int actionPeriod) {
        super(id, position, images, actionPeriod, false);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Point> openPt = world.findOpenAround(getPosition());

        if (openPt.isPresent()) {
            ActionEntity charmander = new Charmander(CHARMANDER_ID_PREFIX + getId(),
                    openPt.get(), imageStore.getImageList(CHARMANDER_KEY),
                    CHARMANDER_CORRUPT_MIN + rand.nextInt(CHARMANDER_CORRUPT_MAX - CHARMANDER_CORRUPT_MIN));
            world.addEntity(charmander);
            scheduler.scheduleActions(charmander, world, imageStore);
        }

        scheduleEvent(scheduler,
                new Activity(this, world, imageStore),
                getActionPeriod());
    }

}
