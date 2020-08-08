import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.interactive.GameObject;

import static org.dreambot.api.methods.Calculations.random;
import static org.dreambot.api.methods.MethodProvider.logInfo;
import static org.dreambot.api.methods.MethodProvider.sleep;

public class RunToAltarNode extends Node {

    private final Area ALTAR_AREA = new Area(new Tile(2156, 3865, 0), new Tile(2155, 3863, 0));

    public RunToAltarNode(AstralRunecrafter main) {
        super(main);
    }

    @Override
    public boolean validate() {
        return main.state == AstralRunecrafter.State.RUNNING_TO_ALTAR;
    }

    @Override
    public int execute() {
        maybeTakeABreak();
        while (!isAltarReachable()) {
            main.getWalking().walk(ALTAR_AREA.getRandomTile());
            sleep(150, 300);
            drinkStaminaPotion();
            sleep(1350, 1700);
        }
        main.state = AstralRunecrafter.State.RUNECRAFTING;
        return main.sleepTime();
    }

    private boolean isAltarReachable() {
        final int ALTAR_ID = 34771;
        GameObject altar = main.getGameObjects().closest(ALTAR_ID);
        return altar != null && altar.isOnScreen() && altar.distance() <= 9;
    }

    private void drinkStaminaPotion() {
        int random = random(3); // So we don't always click exactly when energy reaches 55
        boolean isEnergyLow = main.getWalking().getRunEnergy() <= 55;
        if (isEnergyLow && random == 0) {
            final int STAMINA_POTION_4_ID = 12625;
            final int STAMINA_POTION_3_ID = 12627;
            final int STAMINA_POTION_2_ID = 12629;
            final int STAMINA_POTION_1_ID = 12631;
            main.getInventory().interact(item -> {
                int id = item.getID();
                return (id == STAMINA_POTION_4_ID || id == STAMINA_POTION_3_ID ||
                        id == STAMINA_POTION_2_ID || id == STAMINA_POTION_1_ID);
            }, "Drink");
        }
    }

    /**
     * After analysis it was determined that this Node executes every ~68 seconds.
     * Therefore, giving it a 1/40 chance of taking a break it should take a
     * break every ~45 minutes.
     */
    private void maybeTakeABreak() {
        int random = random(40);
        if (random == 0) {
            int breakLength = random(180, 240) * 1000; // 3-4 minutes
            logInfo("Taking a break at " + main.timer.formatTime());
            sleep(3000, 7000);
            main.getMouse().moveMouseOutsideScreen();
            sleep(breakLength);
        }
    }
}
