import processing.core.PImage;

import java.util.List;

final class RareCandyAura extends Background {
    private String id;
    private List<PImage> images;
    private int imageIndex;

    public RareCandyAura(String id, List<PImage> images) {
        super(id, images);
    }
}
