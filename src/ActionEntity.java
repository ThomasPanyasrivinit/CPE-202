import processing.core.PImage;

import java.util.LinkedList;
import java.util.List;

public abstract class ActionEntity extends Entity {

    private int actionPeriod;

    private boolean catchable;

    public ActionEntity(String id, Point position,
                        List<PImage> images, int actionPeriod, boolean catchable) {
        super(id, position, images);
        this.actionPeriod = actionPeriod;
        this.catchable = catchable;
    }

    protected abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

    protected int getActionPeriod() {
        return actionPeriod;
    }

    protected void scheduleEvent(EventScheduler scheduler, Action action, long afterPeriod) {
        long time = System.currentTimeMillis() +
                (long) (afterPeriod * scheduler.getTimeScale());
        Event event = new Event(action, time, this);

        scheduler.addToEventQueue(event);

        // update list of pending events for the given entity
        List<Event> pending = scheduler.getPendingEvents().getOrDefault(this,
                new LinkedList<>());
        pending.add(event);
        scheduler.putInPendingEvents(this, pending);
    }

    protected boolean returnCatchable(){
        return catchable;
    }

}
