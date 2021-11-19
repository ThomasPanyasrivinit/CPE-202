import processing.core.PImage;

import java.util.*;

final class WorldModel {
    private int numRows;
    private int numCols;
    private Background background[][];
    private Entity occupancy[][];
    private Set<Entity> entities;

    private static final int CHARMANDER_REACH = 1;

    private static final int BGND_NUM_PROPERTIES = 4;
    private static final int BGND_ID = 1;
    private static final int BGND_COL = 2;
    private static final int BGND_ROW = 3;

    private static final String BULBASAUR_KEY = "bulbasaur";
    private static final int BULBASAUR_NUM_PROPERTIES = 7;
    private static final int BULBASAUR_ID = 1;
    private static final int BULBASAUR_COL = 2;
    private static final int BULBASAUR_ROW = 3;
    private static final int BULBASAUR_LIMIT = 4;
    private static final int BULBASAUR_ACTION_PERIOD = 5;
    private static final int BULBASAUR_ANIMATION_PERIOD = 6;

    private static final String OBSTACLE_KEY = "obstacle";
    private static final int OBSTACLE_NUM_PROPERTIES = 4;
    private static final int OBSTACLE_ID = 1;
    private static final int OBSTACLE_COL = 2;
    private static final int OBSTACLE_ROW = 3;

    private static final String CHARMANDER_KEY = "charmander";
    private static final int CHARMANDER_NUM_PROPERTIES = 5;
    private static final int CHARMANDER_ID = 1;
    private static final int CHARMANDER_COL = 2;
    private static final int CHARMANDER_ROW = 3;
    private static final int CHARMANDER_ACTION_PERIOD = 4;


    private static final String MUNCHLAX_KEY = "munchlax";
    private static final int MUNCHLAX_NUM_PROPERTIES = 4;
    private static final int MUNCHLAX_ID = 1;
    private static final int MUNCHLAX_COL = 2;
    private static final int MUNCHLAX_ROW = 3;

    private static final String BERRIES_KEY = "berries";
    private static final int BERRIES_NUM_PROPERTIES = 5;
    private static final int BERRIES_ID = 1;
    private static final int BERRIES_COL = 2;
    private static final int BERRIES_ROW = 3;
    private static final int BERRIES_ACTION_PERIOD = 4;

//    private static final String SNORLAX_KEY = "snorlax";
//    private static final int SNORLAX_NUM_PROPERTIES = 4;
//    private static final int SNORLAX_ID = 1;
//    private static final int SNORLAX_COL = 2;
//    private static final int SNORLAX_ROW = 3;

    private static final String RCANDYBGND_KEY = "rareCandy";
    private static final int RCANDYBGND_NUM_PROPERTIES = 4;
    private static final int RCANDYBGND_ID = 1;
    private static final int RCANDYBGND_COL = 2;
    private static final int RCANDYBGND_ROW = 3;

//    private static final String RCANDYCHEF_KEY = "rareCandyChef";

    private static final String PLAYER_KEY = "player";
    private static final int PLAYER_NUM_PROPERTIES = 7;
    private static final int PLAYER_ID = 1;
    private static final int PLAYER_COL = 2;
    private static final int PLAYER_ROW = 3;
//    private static final int PLAYER_LIMIT = 1;
    private static final int PLAYER_ACTION_PERIOD = 5;
    private static final int PLAYER_ANIMATION_PERIOD = 6;

    private static final int PROPERTY_KEY = 0;
    private static final String BGND_KEY = "background";



    public WorldModel(int numRows, int numCols, Background defaultBackground) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.background = new Background[numRows][numCols];
        this.occupancy = new Entity[numRows][numCols];
        this.entities = new HashSet<>();

        for (int row = 0; row < numRows; row++) {
            Arrays.fill(this.background[row], defaultBackground);
        }
    }

    public Optional<Point> findOpenAround(Point pos) {
        for (int dy = -CHARMANDER_REACH; dy <= CHARMANDER_REACH; dy++) {
            for (int dx = -CHARMANDER_REACH; dx <= CHARMANDER_REACH; dx++) {
                Point newPt = new Point(pos.getX() + dx, pos.getY() + dy);
                if (this.withinBounds(newPt) &&
                        !this.isOccupied(newPt)) {
                    return Optional.of(newPt);
                }
            }
        }

        return Optional.empty();
    }

    public boolean parseBackground(String[] properties,
                                   ImageStore imageStore) {
        if (properties.length == BGND_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[BGND_COL]),
                    Integer.parseInt(properties[BGND_ROW]));
            String id = properties[BGND_ID];
            this.setBackground(pt,
                    new Background(id, imageStore.getImageList(id)));
        }

        return properties.length == BGND_NUM_PROPERTIES;
    }

    public boolean parseRareCandyAura(String[] properties,
                                   ImageStore imageStore) {
        if (properties.length == RCANDYBGND_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[RCANDYBGND_COL]),
                    Integer.parseInt(properties[RCANDYBGND_ROW]));
            String id = properties[RCANDYBGND_ID];
            this.setBackground(pt,
                    new RareCandyAura(id, imageStore.getImageList(id)));
        }

        return properties.length == BGND_NUM_PROPERTIES;
    }


    public boolean parseBulbasaur(String[] properties,
                              ImageStore imageStore) {
        if (properties.length == BULBASAUR_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[BULBASAUR_COL]),
                    Integer.parseInt(properties[BULBASAUR_ROW]));
            Entity entity = new BulbasaurNotFull(properties[BULBASAUR_ID],
                    pt,
                    imageStore.getImageList(BULBASAUR_KEY),
                    Integer.parseInt(properties[BULBASAUR_LIMIT]),
                    0,
                    Integer.parseInt(properties[BULBASAUR_ACTION_PERIOD]),
                    Integer.parseInt(properties[BULBASAUR_ANIMATION_PERIOD]));
            tryAddEntity(entity);
        }

        return properties.length == BULBASAUR_NUM_PROPERTIES;
    }

    public boolean parseObstacle(String[] properties,
                                 ImageStore imageStore) {
        if (properties.length == OBSTACLE_NUM_PROPERTIES) {
            Point pt = new Point(
                    Integer.parseInt(properties[OBSTACLE_COL]),
                    Integer.parseInt(properties[OBSTACLE_ROW]));
            Entity entity = new Tree(properties[OBSTACLE_ID],
                    pt, imageStore.getImageList(OBSTACLE_KEY));
            tryAddEntity(entity);
        }

        return properties.length == OBSTACLE_NUM_PROPERTIES;
    }

    public boolean parseCharmander(String[] properties,
                            ImageStore imageStore) {
        if (properties.length == CHARMANDER_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[CHARMANDER_COL]),
                    Integer.parseInt(properties[CHARMANDER_ROW]));
            Entity entity = new Charmander(properties[CHARMANDER_ID],
                    pt, imageStore.getImageList(CHARMANDER_KEY), Integer.parseInt(properties[CHARMANDER_ACTION_PERIOD]));
            tryAddEntity(entity);
        }

        return properties.length == CHARMANDER_NUM_PROPERTIES;
    }

    public boolean parseMunchlax(String[] properties,
                              ImageStore imageStore) {
        if (properties.length == MUNCHLAX_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[BULBASAUR_COL]),
                    Integer.parseInt(properties[MUNCHLAX_ROW]));
            Munchlax munchlax = new Munchlax(properties[MUNCHLAX_ID],pt, imageStore.getImageList(MUNCHLAX_KEY), 5, 6);
            this.tryAddEntity(munchlax);
        }

        return properties.length == MUNCHLAX_NUM_PROPERTIES;
    }

    public boolean parseBerries(String[] properties,
                             ImageStore imageStore) {
        if (properties.length == BERRIES_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[BERRIES_COL]),
                    Integer.parseInt(properties[BERRIES_ROW]));
            Entity entity = new Berries(properties[BERRIES_ID],
                    pt,
                    imageStore.getImageList(BERRIES_KEY),
                    Integer.parseInt(properties[BERRIES_ACTION_PERIOD])
            );
            tryAddEntity(entity);
        }

        return properties.length == BERRIES_NUM_PROPERTIES;
    }
    private boolean parsePlayer(String [] properties, ImageStore imageStore) {
        if (properties.length == PLAYER_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[PLAYER_COL]),
                    Integer.parseInt(properties[PLAYER_ROW]));

            PokemonTrainer player = new PokemonTrainer(properties[PLAYER_ID],
                    pt,
                    Integer.parseInt(properties[PLAYER_ACTION_PERIOD]),
                    Integer.parseInt(properties[PLAYER_ANIMATION_PERIOD]),
                    imageStore.getImageList(PLAYER_KEY),0);
        }
        return properties.length == PLAYER_NUM_PROPERTIES;
    }

    private void tryAddEntity(Entity entity) {
        if (this.isOccupied(entity.getPosition())) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        this.addEntity(entity);
    }

    public boolean withinBounds(Point pos) {
        return pos.getY() >= 0 && pos.getY() < this.numRows &&
                pos.getX() >= 0 && pos.getX() < this.numCols;
    }

    public boolean isOccupied(Point pos) {
        return withinBounds(pos) &&
                this.getOccupancyCell(pos) != null;
    }

    public void moveEntity(Entity entity, Point pos) {
        Point oldPos = entity.getPosition();
        if (this.withinBounds(pos) && !pos.equals(oldPos)) {
            this.setOccupancyCell(oldPos, null);
            this.removeEntityAt(pos);
            this.setOccupancyCell(pos, entity);
            entity.setPosition(pos);
        }
    }

    public void removeEntity(Entity entity) {
        this.removeEntityAt(entity.getPosition());
    }

    public void removeEntityAt(Point pos) {
        if (this.withinBounds(pos)
                && this.getOccupancyCell(pos) != null) {
            Entity entity = this.getOccupancyCell(pos);

            this.entities.remove(entity);
            this.setOccupancyCell(pos, null);
        }
    }

    public Optional<PImage> getBackgroundImage(Point pos) {
        if (this.withinBounds(pos)) {
            return Optional.of(getBackgroundCell(pos).getCurrentImage());
        } else {
            return Optional.empty();
        }
    }

    public void setBackground(Point pos,
                               Background background) {
        if (this.withinBounds(pos)) {
            this.setBackgroundCell(pos, background);
        }
    }

    public Optional<Entity> getOccupant(Point pos) {
        if (this.isOccupied(pos)) {
            return Optional.of(this.getOccupancyCell(pos));
        } else {
            return Optional.empty();
        }
    }

    Entity getOccupancyCell(Point pos) {
        return this.occupancy[pos.getY()][pos.getX()];
    }

    public void setOccupancyCell(Point pos, Entity entity) {
        this.occupancy[pos.getY()][pos.getX()] = entity;
    }

    public Background getBackgroundCell(Point pos) {
        return this.background[pos.getY()][pos.getX()];
    }

    public void setBackgroundCell(Point pos,
                                   Background background) {
        this.background[pos.getY()][pos.getX()] = background;
    }


    public void addEntity(Entity entity) {
        if (this.withinBounds(entity.getPosition())) {
            this.setOccupancyCell(entity.getPosition(), entity);
            this.entities.add(entity);
        }
    }

    public Optional<Entity> findNearest(Point pos, Class kind) {
        List<Entity> ofType = new LinkedList<>();
        for (Entity entity : this.getEntities()) {
            if (kind.isInstance(entity)) {
                ofType.add(entity);
            }
        }

        return pos.nearestEntity(ofType);
    }


    public Set<Entity> getEntities() {
        return this.entities;
    }

    public int getNumRows() {
        return this.numRows;
    }

    public int getNumCols() {
        return this.numCols;
    }

    public void addEntities(Entity entity) {
        this.entities.add(entity);
    }

    boolean processLine(String line, ImageStore imageStore) {
        String[] properties = line.split("\\s");
        if (properties.length > 0) {
            switch (properties[PROPERTY_KEY]) {
                case BGND_KEY:
                    return parseBackground(properties, imageStore);
                case BULBASAUR_KEY:
                    return parseBulbasaur(properties, imageStore);
                case OBSTACLE_KEY:
                    return parseObstacle(properties, imageStore);
                case CHARMANDER_KEY:
                    return parseCharmander(properties, imageStore);
                case MUNCHLAX_KEY:
                    return parseMunchlax(properties, imageStore);
                case BERRIES_KEY:
                    return parseBerries(properties, imageStore);
//                case SNORLAX_KEY:
//                    return world.parseFrozenMUNCHLAX(properties, this);
                case RCANDYBGND_KEY:
                    return parseRareCandyAura(properties, imageStore);
                case PLAYER_KEY:
                    return parsePlayer(properties, imageStore);
            }
            }
        return false;
        }
}
