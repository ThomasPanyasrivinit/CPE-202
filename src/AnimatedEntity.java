import processing.core.PImage;

import java.util.List;

public abstract class AnimatedEntity extends ActionEntity {

    private int animationPeriod;

    public AnimatedEntity(String id, Point position,
                          List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, true);
        this.animationPeriod = animationPeriod;
    }

    protected void nextImage() {
        setImageIndex(((getImageIndex()) + 1) % getImages().size());
    }

    protected int getAnimationPeriod() {
        return animationPeriod;
    }

}
