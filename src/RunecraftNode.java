import org.dreambot.api.methods.magic.Lunar;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.tabs.Tab;

import java.awt.Rectangle;

import static org.dreambot.api.methods.Calculations.random;
import static org.dreambot.api.methods.MethodProvider.logInfo;
import static org.dreambot.api.methods.MethodProvider.sleep;
import static org.dreambot.api.methods.MethodProvider.sleepUntil;

public class RunecraftNode extends Node {

    public RunecraftNode(AstralRunecrafter main) {
        super(main);
    }

    private final int GIANT_POUCH_ID = 5514;
    private final int LARGE_POUCH_ID = 5512;
    private final int MEDIUM_POUCH_ID = 5510;
    private final int SMALL_POUCH_ID = 5509;
    private final int ALTAR_ID = 34771;
    private final Rectangle TELE_GROUP_MOONCLAN_BOUNDS = new Rectangle(654, 251, 8, 8);
    private final int TELE_ANIMATION = 1816;
    private final int ASTRAL_RUNE_ID = 9075;

    private final Rectangle GIANT_POUCH_RECT = main.getInventory().slotBounds(0);
    private final Rectangle LARGE_POUCH_RECT = main.getInventory().slotBounds(4);
    private final Rectangle MEDIUM_POUCH_RECT = main.getInventory().slotBounds(1);
    private final Rectangle SMALL_POUCH_RECT = main.getInventory().slotBounds(5);

    private final Rectangle GIANT_POUCH_SUB_RECT =
            new Rectangle(GIANT_POUCH_RECT.x + 10, GIANT_POUCH_RECT.y + 5, 10, 10);

    private final Rectangle LARGE_POUCH_SUB_RECT =
            new Rectangle(LARGE_POUCH_RECT.x + 10, LARGE_POUCH_RECT.y + 5, 10, 10);

    private final Rectangle MEDIUM_POUCH_SUB_RECT =
            new Rectangle(MEDIUM_POUCH_RECT.x + 10, MEDIUM_POUCH_RECT.y + 5, 10, 10);

    private final Rectangle SMALL_POUCH_SUB_RECT =
            new Rectangle(SMALL_POUCH_RECT.x + 10, SMALL_POUCH_RECT.y + 5, 10, 10);

    @Override
    public boolean validate() {
        return main.state == AstralRunecrafter.State.RUNECRAFTING;
    }

    @Override
    public int execute() {
        int startCount = main.getInventory().count(ASTRAL_RUNE_ID);

        final int preEmptySlotCount1 = main.getInventory().getEmptySlots();
        main.getGameObjects().closest(ALTAR_ID).interact();
        main.getMouse().move(GIANT_POUCH_SUB_RECT);
        sleepUntil(() -> main.getInventory().getEmptySlots() > preEmptySlotCount1, random(2500, 3000));
        sleep(1000, 1250);

        final int preEmptySlotCount2 = main.getInventory().getEmptySlots();
        main.getKeyboard().pressShift();
        int loops1 = 0;
        while (main.getInventory().getEmptySlots() >= preEmptySlotCount2) {
            if (loops1 > 10) {
                logInfo("Stopping. Infinite spam-click loop.");
                main.stop();
            }
            spamClickPouches(GIANT_POUCH_SUB_RECT, LARGE_POUCH_SUB_RECT);
            loops1++;
        }
        main.getKeyboard().releaseShift();
        main.areBigPouchesFull = false;

        final int preEmptySlotCount3 = main.getInventory().getEmptySlots();
        main.getGameObjects().closest(ALTAR_ID).interact();
        main.getMouse().move(MEDIUM_POUCH_SUB_RECT);
        sleepUntil(() -> main.getInventory().getEmptySlots() > preEmptySlotCount3, random(2500, 3000));
        sleep(1000, 1250);

        final int preEmptySlotCount4 = main.getInventory().getEmptySlots();
        main.getKeyboard().pressShift();
        int loops2 = 0;
        while (main.getInventory().getEmptySlots() >= preEmptySlotCount4) {
            if (loops2 > 10) {
                logInfo("Stopping. Infinite spam-click loop.");
                main.stop();
            }
            spamClickPouches(MEDIUM_POUCH_SUB_RECT, SMALL_POUCH_SUB_RECT);
            loops2++;
        }
        main.getKeyboard().releaseShift();
        main.areSmallPouchesFull = false;

        final int preEmptySlotCount5 = main.getInventory().getEmptySlots();
        main.getGameObjects().closest(ALTAR_ID).interact();
        main.getTabs().openWithFKey(Tab.MAGIC);
        main.getMouse().move(TELE_GROUP_MOONCLAN_BOUNDS);

        int magicXp = main.getSkills().getExperience(Skill.MAGIC);
        sleepUntil(() -> preEmptySlotCount5 < main.getInventory().getEmptySlots(), random(3500, 4000));
        int endCount = main.getInventory().count(ASTRAL_RUNE_ID);
        accountProducedAstralRunes(startCount, endCount);

        while (magicXp == main.getSkills().getExperience(Skill.MAGIC)) {
            spamClickTele();
        }

        accountConsumedTeleRunes();
        sleepUntil(() -> main.getLocalPlayer().getAnimation() == TELE_ANIMATION, random(1000, 1500));
        sleep(200, 300);
        main.getTabs().openWithFKey(Tab.INVENTORY);
        sleep(200, 300);
        Rectangle bankMinimapArea = new Rectangle(588, 88, 60, 50);
        main.getMouse().move(bankMinimapArea);

        final Area MOONCLAN_TELEPORT_AREA = new Area(new Tile(2115, 3917, 0), new Tile(2106, 3912, 0));
        sleepUntil(() -> MOONCLAN_TELEPORT_AREA.
                        contains(main.getLocalPlayer().getTile()),
                random(3500, 4000));

        boolean isPlayerInTeleArea = MOONCLAN_TELEPORT_AREA.
                contains(main.getLocalPlayer().getTile());
        if (isPlayerInTeleArea) {
            main.state = AstralRunecrafter.State.RUNNING_TO_BANK;
        }
        return main.sleepTime();
    }

    private void spamClickPouches(Rectangle pouchA, Rectangle pouchB) {
        main.getMouse().click(pouchA);
        sleep(80, 100);
        main.getMouse().click(pouchB);
        sleep(80, 100);
    }

    private void spamClickTele() {
        main.getMagic().castSpell(Lunar.TELE_GROUP_MOONCLAN);
        sleep(80, 100);
    }

    private void accountConsumedTeleRunes() {
        main.astralRunesConsumed += 2;
        main.lawRunesConsumed++;
    }

    private void accountProducedAstralRunes(int startCount, int endCount) {
        main.astralRunesProduced += (endCount - startCount);
    }
}
