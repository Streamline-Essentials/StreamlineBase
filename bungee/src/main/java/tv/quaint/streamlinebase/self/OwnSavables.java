package tv.quaint.streamlinebase.self;

import tv.quaint.streamlinebase.StreamlineBase;
import tv.quaint.streamlinebase.configs.ConfigPage;
import tv.quaint.streamlinebase.configs.SettingArgument;
import tv.quaint.streamlinebase.utils.obj.AppendableList;

import java.util.List;

public class OwnSavables extends ConfigPage {
    public double pointsDefault;

    public String consoleNameRegular;
    public String consoleNameDisplay;
    public String consoleServer;
    public List<String> consoleTagsDefault;

    public int playerLevelingXPStart;
    public int playerLevelingLevelStart;
    public String playerLevelingLevelEquation;
    public List<String> playerTagsDefault;
    public int playerXPGiveMillisecondsTill;
    public int playerXPGiveAmount;

    public OwnSavables() {
        super(StreamlineBase.EXPANSION, "savables", true, new AppendableList<>());
    }

    @Override
    public void onReload(AppendableList<SettingArgument<?>> settingArguments) {
        pointsDefault = config.getOrSetDefault("points.default", 0d);

        consoleServer = config.getOrSetDefault("console.server", "CONSOLE");
        consoleNameRegular = config.getOrSetDefault("console.name.regular", "Console");
        consoleNameDisplay = config.getOrSetDefault("console.name.display", "&c&lConsole&r");
        consoleTagsDefault = config.getOrSetDefault("console.tags.default", List.of("console"));

        playerLevelingXPStart = config.getOrSetDefault("players.leveling.xp.start", 0);
        playerXPGiveMillisecondsTill = config.getOrSetDefault("players.leveling.xp.give.milliseconds-till", 6000);
        playerXPGiveAmount = config.getOrSetDefault("players.leveling.xp.give.amount", 1);
        playerLevelingLevelStart = config.getOrSetDefault("players.leveling.level.start", 1);
        playerLevelingLevelEquation = config.getOrSetDefault("players.leveling.level.equation", "2500 + (2500 * (%player_level% - 1))");
        playerTagsDefault = config.getOrSetDefault("players.tags.default", List.of("player"));
    }
}
