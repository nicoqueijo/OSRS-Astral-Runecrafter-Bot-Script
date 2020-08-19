import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Timer;

import java.awt.Color;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.dreambot.api.methods.Calculations.random;

@ScriptManifest(
        author = "Nico",
        description = "Runecrafts astral runes",
        category = Category.RUNECRAFTING,
        version = 1.0,
        name = "Astral Runecrafter"
)
public class AstralRunecrafter extends AbstractScript {
    /*
      Equipment: Graceful hood, Graceful cape, Amulet of fury, Mystic dust staff, Graceful top,
      Graceful legs, Dragonfire shield, Graceful gloves, Graceful boots, Brimstone ring
      <p>
      Inventory: Stamina potion, Giant pouch, Large pouch, Medium pouch, Small pouch
      Rune pouch (Law runes, Cosmic runes, Astral runes)
      <p>
      Spellbook: Lunar
     */

    /*
      Flow:
      -Open bank
      -Withdraw stamina pot if no have one
      -Withdraw all pure essence
      -Close bank (ESC)
      -Fill giant and large pouches
      -Repair pouches if needed
      -Open bank
      -Withdraw all pure essence
      -Close bank (ESC)
      -Fill medium and small pouches
      -Open bank
      -Withdraw all pure essence
      -Close bank (ESC)
      -Run to altar
      -Drink a dose of stamina if energy falls below 55
      -Crafts runes at altar
      -Empty giant and large pouches
      -Crafts runes at altar
      -Empty medium and small pouches
      -Crafts runes at altar
      -Tele to lunar isle
      -Run to bank
     */

    public enum State {
        BANKING, RUNNING_TO_ALTAR, RUNECRAFTING, RUNNING_TO_BANK
    }

    public State state = State.BANKING;

    public Timer timer = new Timer();
    private final long timeToStop = (long) 60 * 1000 * random(225, 240); // Almost 4 hours
    private List<Node> nodes = new ArrayList<>();
    private int beginningXp;

    private final int LVL_91_XP = 5902831;

    public boolean areBigPouchesFull = false;
    public boolean areSmallPouchesFull = false;

    private final int PURE_ESSENCE_PRICE = 2;
    private final int STAMINA_POTION_4_PRICE = 5333;
    private final int LAW_RUNE_PRICE = 143;
    private final int COSMIC_RUNE_PRICE = 138;
    private final int ASTRAL_RUNE_PRICE = 161;

    public int astralRunesProduced = 0;
    public int pureEssenceConsumed = 0;
    public int staminaPotionsConsumed = 0;
    public int lawRunesConsumed = 0;
    public int cosmicRunesConsumed = 0;
    public int astralRunesConsumed = 0;

    @Override
    public void onPaint(Graphics graphics) {
        super.onPaint(graphics);
        graphics.setColor(Color.BLACK);
        graphics.drawString("Time running: " + timer.formatTime(), 330, 363);
        graphics.drawString("Total XP gained: " + formatInt(calculateXP()), 330, 381);
        graphics.drawString("Hourly XP rate: " + formatInt(timer.getHourlyRate(calculateXP())), 330, 399);
        graphics.drawString("Total profit made: " + formatInt(calculateProfit()), 330, 417);
        graphics.drawString("Hourly profit rate: " + formatInt(timer.getHourlyRate(calculateProfit())), 330, 435);
        graphics.drawString("XP until level 91: " + formatInt(remainingXPuntil91()), 330, 453);
        graphics.drawString("Hours until level 91: " + decimalFormat(remainingXPuntil91() / (double) timer.getHourlyRate(calculateXP())), 330, 471);
    }

    @Override
    public void onStart() {
        // Assure run on
        // Assure bank is set on quantity all
        super.onStart();
        beginningXp = getSkills().getExperience(Skill.RUNECRAFTING);
        nodes.add(new RepairPouchesNode(this));
        nodes.add(new WithdrawStaminaPotion(this));
        nodes.add(new WithdrawPureEssence(this));
        nodes.add(new FillBigPouchesNode(this));
        nodes.add(new WithdrawPureEssence(this));
        nodes.add(new FillSmallPouchesNode(this));
        nodes.add(new WithdrawPureEssence(this));
        nodes.add(new RunToAltarNode(this));
        nodes.add(new RunecraftNode(this));
        nodes.add(new RunToBankNode(this));
    }

    @Override
    public int onLoop() {
        for (Node node : nodes) {
            /*log("Validating: " + node.getClass().getSimpleName());*/
            if (node.validate()) {
                /*log("Executing: " + node.getClass().getSimpleName());*/
                return node.execute();
            }
            checkStop();
        }
        return sleepTime();
    }

    public int sleepTime() {
        return random(25, 50);
    }

    private void checkStop() {
        if (!getClient().isLoggedIn()) {
            logInfo("Stopping script. Player has logged out.");
            stop();
        } else if (timer.elapsed() > timeToStop) {
            logInfo("Stopping script. Reached time limit.");
            stop();
        } else if (getSkills().getBoostedLevels(Skill.HITPOINTS) < 30) {
            logInfo("Stopping script. Low health.");
            stop();
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        generatePerformanceReport();
    }

    private String formatInt(int num) {
        return NumberFormat.getNumberInstance(Locale.US).format(num);
    }

    private String decimalFormat(double num) {
        return new DecimalFormat("0.00").format(num);
    }

    private int calculateXP() {
        int currentXp = getSkills().getExperience(Skill.RUNECRAFTING);
        return currentXp - beginningXp;
    }

    private int remainingXPuntil91() {
        return LVL_91_XP - getSkills().getExperience(Skill.RUNECRAFTING);
    }

    private int calculateProfit() {
        return (astralRunesProduced * ASTRAL_RUNE_PRICE) -
                (pureEssenceConsumed * PURE_ESSENCE_PRICE) -
                (staminaPotionsConsumed * STAMINA_POTION_4_PRICE) -
                (lawRunesConsumed * LAW_RUNE_PRICE) -
                (cosmicRunesConsumed * COSMIC_RUNE_PRICE) -
                (astralRunesConsumed * ASTRAL_RUNE_PRICE);
    }

    private void generatePerformanceReport() {
        logInfo("------------------------");
        logInfo("Time ran: " + timer.formatTime());
        logInfo("Total XP gained: " + formatInt(calculateXP()));
        logInfo("Hourly XP rate: " + formatInt(timer.getHourlyRate(calculateXP())));
        logInfo("Total profit made: " + formatInt(calculateProfit()));
        logInfo("Hourly profit rate: " + formatInt(timer.getHourlyRate(calculateProfit())));
        logInfo("------------------------");
        logInfo("Astral runes produced: " + astralRunesProduced);
        logInfo("Pure essence consumed: " + pureEssenceConsumed);
        logInfo("Stamina potions consumed: " + staminaPotionsConsumed);
        logInfo("Law runes consumed: " + lawRunesConsumed);
        logInfo("Cosmic runes consumed: " + cosmicRunesConsumed);
        logInfo("Astral runes consumed: " + astralRunesConsumed);
        logInfo("------------------------");

    }
}
