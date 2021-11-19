import processing.core.PApplet;
import processing.core.PImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public final class VirtualWorld
        extends PApplet {
    private static final int TIMER_ACTION_PERIOD = 100;

    private static final int VIEW_WIDTH = 640;
    private static final int VIEW_HEIGHT = 480;
    private static final int TILE_WIDTH = 32;
    private static final int TILE_HEIGHT = 32;
    private static final int WORLD_WIDTH_SCALE = 2;
    private static final int WORLD_HEIGHT_SCALE = 2;

    private static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    private static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
    private static final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
    private static final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;

    private static final String IMAGE_LIST_FILE_NAME = "imagelist";
    private static final String DEFAULT_IMAGE_NAME = "background_default";
    private static final int DEFAULT_IMAGE_COLOR = 0x808080;

    private static final String LOAD_FILE_NAME = "world.sav";

    private static final String FAST_FLAG = "-fast";
    private static final String FASTER_FLAG = "-faster";
    private static final String FASTEST_FLAG = "-fastest";
    private static final double FAST_SCALE = 0.5;
    private static final double FASTER_SCALE = 0.25;
    private static final double FASTEST_SCALE = 0.10;

    private static final int KEYED_IMAGE_MIN = 5;
    private static final int KEYED_RED_IDX = 2;
    private static final int KEYED_GREEN_IDX = 3;
    private static final int KEYED_BLUE_IDX = 4;
    private static final String PLAYER_ID = "player";
    private static double timeScale = 1.0;

    private ImageStore imageStore;
    private WorldModel world;
    private WorldView view;
    private EventScheduler scheduler;
    private PokemonTrainer player;

    private long next_time;
    private boolean start = true;
    private boolean redraw = false;
    private boolean toggleAbility = false;
    private boolean redrawOnce = true;

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        this.imageStore = new ImageStore(
                createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
        this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
                createDefaultBackground(imageStore));
        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world,
                TILE_WIDTH, TILE_HEIGHT);
        this.scheduler = new EventScheduler(timeScale);

        loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
        loadWorld(world, LOAD_FILE_NAME, imageStore);
        this.player = new PokemonTrainer("player", new Point(6, 10), 5, 5, imageStore.getImageList(PLAYER_ID),0);
        world.addEntity(player);
        scheduleActions(world, scheduler, imageStore);

        next_time = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
    }

    public void draw() {
        if (start)
        {
            textSize(40);
            background(100, 100, 200);
            text("How to play:", 200, 50);
            textSize(20);
            text("Press a to evolve the closest Bulbasaur", 40, 130);
            text("Press s to evolve the closest Charmander", 40, 160);
            text("Press d to evolve the closest Munchlax", 40, 190);
            text("Press q to toggle on mouse-click event", 40, 220);
            text("Press w to to toggle off mouse-click event ", 40, 250);
            text("Run into pokemon to catch them! ", 40, 280);
            textSize(15);
            text("*click to continue*", 500, 280);
            textSize(30);
            text("Catch 10 pokemon to win!", 40, 400);
            start= false;
        }
        if(redraw) {
            long time = System.currentTimeMillis();
            if (time >= next_time) {
                scheduler.updateOnTime(time);
                next_time = time + TIMER_ACTION_PERIOD;
            }

            view.drawViewport();
            textSize(20);
            fill(9);
            text("Catch Count: " + player.getCatchCount(), 110, 20);
            if(toggleAbility == false) {
                text("Ability: OFF ", 350, 20);
            }
            if(toggleAbility == true){
                text("Ability: ON " ,350, 20);
            }
            if (player.getCatchCount() >= 10) {
                textSize(50);
                text("You caught 10 Pokemon!", 20, 300);
                text("You win!", 210, 200);
                stop();
            }
        }
    }

    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP:
                    dy = -1;
                    player.turn(1);
                    break;
                case DOWN:
                    dy = 1;
                    player.turn(0);
                    break;
                case LEFT:
                    dx = -1;
                    player.turn(2);
                    break;
                case RIGHT:
                    dx = 1;
                    player.turn(3);
                    break;
            }
            Point pt = new Point(player.getPosition().x + dx, player.getPosition().y + dy);
            player.setImageIndex(0);
            scheduler.scheduleActions(player, world, imageStore);
            if (!world.isOccupied(pt)) {
                world.moveEntity(player, pt);
                view.shiftView(dx, dy);
            }
            else if (world.getOccupancyCell(pt) instanceof ActionEntity) {
                if(((ActionEntity) world.getOccupancyCell(pt)).returnCatchable()) {
                    world.removeEntity(world.getOccupancyCell(pt));
                    world.moveEntity(player, pt);
                    player.increaseCatchCount();
                    System.out.println(player.getCatchCount());
                }
            }


        }
        if (key == 'a') {
            System.out.println("Bulbasaur Evolved!");
            Optional<Entity> bulbasaur = world.findNearest(player.getPosition(), Bulbasaur.class);
            if (bulbasaur.isPresent()) {
                Bulbasaur bulb = ((Bulbasaur)(bulbasaur.get()));
                Movable venusaur = new Venusaur("venusaur", bulb.getPosition(), imageStore.getImageList("venusaur"),bulb.getActionPeriod() * 20, bulb.getAnimationPeriod() * 10, bulb);
                world.removeEntity(bulb);
                scheduler.unscheduleAllEvents(bulb);
                scheduler.scheduleActions(venusaur, world, imageStore);

                world.addEntity(venusaur);
            }
        }
        if (key == 's') {
            System.out.println("Charmander Evolved!");
            Optional<Entity> charman = world.findNearest(player.getPosition(), Charmander.class);
            if (charman.isPresent()) {
                Charmander charmander = ((Charmander)(charman.get()));
                Movable charizard = new Charizard("charizard", charmander.getPosition(), imageStore.getImageList("charizard"),charmander.getActionPeriod() * 20, 50);
                world.removeEntity(charmander);
                scheduler.unscheduleAllEvents(charmander);
                scheduler.scheduleActions(charizard, world, imageStore);

                world.addEntity(charizard);
            }
        }
        if (key == 'd') {
            System.out.println("Munchlax Evolved!");
            Optional<Entity> munch = world.findNearest(player.getPosition(), Munchlax.class);
            if (munch.isPresent()) {
                Munchlax munchlax = ((Munchlax)(munch.get()));
                Movable snorlax = new Charizard("snorlax", munchlax.getPosition(), imageStore.getImageList("snorlax"),munchlax.getActionPeriod() * 20, 50);
                world.removeEntity(snorlax);
                scheduler.unscheduleAllEvents(snorlax);
                scheduler.scheduleActions(snorlax, world, imageStore);

                world.addEntity(snorlax);
            }
        }
        if(key =='q'){
            toggleAbility = true;
        }
        if(key == 'w'){
            toggleAbility = false;
        }

    }

    public void mousePressed()
    {
        if(redrawOnce) {
            redraw = true;
            redrawOnce = false;
        }
        Point location = mouseToPoint();
        if(toggleAbility) {
            System.out.println(location.getX() + ", " + location.getY());
            RareCandyChef chef = new RareCandyChef("rareCandyChef", location, imageStore.getImageList("rareCandyChef"), 5000, 100);
            if (!(world.isOccupied(location))) {
                world.addEntity(chef);
                scheduler.scheduleActions(chef, world, imageStore);
            }

            for (int i = location.x - 1; i < location.x + 2; i++) {
                for (int j = location.y - 1; j < location.y + 2; j++) {
                    if (world.withinBounds(new Point(i, j)))
                        world.setBackgroundCell(new Point(i, j), new RareCandyAura("rareCandy", imageStore.getImageList("rareCandy")));
                }
            }

            Optional<Entity> bulbasaur = world.findNearest(location, Bulbasaur.class);
            if (bulbasaur.isPresent()) {
                Bulbasaur bulb = ((Bulbasaur) (bulbasaur.get()));
                Movable venusaur = new Venusaur("venusaur", bulb.getPosition(), imageStore.getImageList("venusaur"), bulb.getActionPeriod() * 20, bulb.getAnimationPeriod() * 10, bulb);
                world.removeEntity(bulb);
                scheduler.unscheduleAllEvents(bulb);
                scheduler.scheduleActions(venusaur, world, imageStore);

                world.addEntity(venusaur);
            }
        }

    }

    private Point mouseToPoint()
    {
        return view.getViewport().viewportToWorld(mouseX/TILE_WIDTH, mouseY/TILE_HEIGHT);
    }

    private static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME,
                              imageStore.getImageList(DEFAULT_IMAGE_NAME));
    }

    private static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            img.pixels[i] = color;
        }
        img.updatePixels();
        return img;
    }

    private static void loadImages(String filename, ImageStore imageStore,
                                   PApplet screen) {
        try {
            Scanner in = new Scanner(new File(filename));
            VirtualWorld.loadImages(in, imageStore, screen);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void loadWorld(WorldModel world, String filename,
                                  ImageStore imageStore) {
        try {
            Scanner in = new Scanner(new File(filename));
            imageStore.load(in, world);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void scheduleActions(WorldModel world,
                                        EventScheduler scheduler, ImageStore imageStore) {
        for (Entity entity : world.getEntities()) {
            if (entity instanceof ActionEntity)
                scheduler.scheduleActions((ActionEntity) entity, world, imageStore);
        }
    }

    private static void parseCommandLine(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG:
                    timeScale = Math.min(FAST_SCALE, timeScale);
                    break;
                case FASTER_FLAG:
                    timeScale = Math.min(FASTER_SCALE, timeScale);
                    break;
                case FASTEST_FLAG:
                    timeScale = Math.min(FASTEST_SCALE, timeScale);
                    break;
            }
        }
    }

    private static void loadImages(Scanner in, ImageStore imageStore,
                                   PApplet screen) {
        int lineNumber = 0;
        while (in.hasNextLine()) {
            try {
                processImageLine(imageStore.getImageMap(), in.nextLine(), screen);
            } catch (NumberFormatException e) {
                System.out.println(String.format("Image format error on line %d",
                        lineNumber));
            }
            lineNumber++;
        }
    }

    public static void main(String[] args) {
        parseCommandLine(args);
        PApplet.main(VirtualWorld.class);
    }

    private static void processImageLine(Map<String, List<PImage>> images,
                                         String line, PApplet screen) {
        String[] attrs = line.split("\\s");
        if (attrs.length >= 2) {
            String key = attrs[0];
            PImage img = screen.loadImage(attrs[1]);
            if (img != null && img.width != -1) {
                List<PImage> imgs = ImageStore.getImages(images, key);
                imgs.add(img);

                if (attrs.length >= KEYED_IMAGE_MIN) {
                    int r = Integer.parseInt(attrs[KEYED_RED_IDX]);
                    int g = Integer.parseInt(attrs[KEYED_GREEN_IDX]);
                    int b = Integer.parseInt(attrs[KEYED_BLUE_IDX]);
                    ImageStore.setAlpha(img, screen.color(r, g, b), 0);
                }
            }
        }
    }
}
