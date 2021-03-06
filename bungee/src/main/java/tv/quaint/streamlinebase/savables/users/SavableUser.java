package tv.quaint.streamlinebase.savables.users;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tv.quaint.streamlinebase.StreamlineBase;
import tv.quaint.streamlinebase.savables.SavableAdapter;
import tv.quaint.streamlinebase.savables.SavableFile;
import tv.quaint.streamlinebase.utils.BaseMessaging;
import tv.quaint.streamlinebase.utils.BasePlayerHandler;
import tv.quaint.streamlinebase.utils.FileUtils;
import tv.quaint.streamlinebase.utils.SavableHandler;

import java.io.File;
import java.util.List;
import java.util.Optional;

public abstract class SavableUser extends SavableFile {
    private SavableUser savableUser;

    public File file;
    public String latestName;
    public String displayName;
    public List<String> tagList;
    public double points;
    public String latestVersion;
//    public String latestServer;
    public boolean online;
    public String latestServer;

    public SavableUser getSavableUser() {
        return this.savableUser;
    }

    public String findServer() {
        if (this.uuid.equals("%")) {
            return StreamlineBase.SAVABLES.consoleServer;
        } else {
            try {
                ProxiedPlayer player = BasePlayerHandler.getPlayerByUUID(this.uuid);

                if (player == null) return StreamlineBase.MESSAGES.nullString;

                if (player.getServer() == null) return StreamlineBase.MESSAGES.nullString;

                return player.getServer().getInfo().getName();
            } catch (Exception e) {
                return StreamlineBase.MESSAGES.nullString;
            }
        }
    }

    public Optional<CommandSender> findSenderOptional() {
        if (this.uuid == null) return Optional.empty();
        if (this.uuid.equals("")) return Optional.empty();
        if (this.uuid.equals("%")) return Optional.ofNullable(StreamlineBase.SERVER.getConsole());
        else return Optional.ofNullable(BasePlayerHandler.getPlayerByUUID(this.uuid));
    }

    public CommandSender findSender() {
        if (findSenderOptional().isPresent()) {
            return findSenderOptional().get();
        } else {
            BasePlayerHandler.getPlayerByUUID(this.identifier);
        }
        return null;
    }

    public String grabServer() {
        if (this.uuid.equals("%")) return StreamlineBase.SAVABLES.consoleServer;
        else {
            if (findSenderOptional().isPresent()) {
                ProxiedPlayer player = (ProxiedPlayer) (findSenderOptional().get());
                if (player.getServer() != null) {
                    return player.getServer().getInfo().getName();
                }
            }
        }

        return StreamlineBase.MESSAGES.nullString;
    }

    public String getLatestVersion() {
        if (this instanceof SavableConsole) return /* TODO: ConfigUtils.consoleVersion() */ StreamlineBase.MESSAGES.nullString;
//        if (! StreamLine.viaHolder.isPresent()) return StreamlineBase.MESSAGES.nullString;

//        return StreamLine.viaHolder.getProtocol(UUID.fromString(this.uuid)).getName();
        return StreamlineBase.MESSAGES.nullString;
    }

    public boolean updateOnline() {
        if (uuid.equals("%")) this.online = false;

        this.online = BasePlayerHandler.getPlayerByUUID(this.uuid) != null;
        return this.online;
    }

    public SavableUser(CommandSender sender, SavableAdapter.SavableType type) {
        this((sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getUniqueId().toString() : "%", type);
    }

    public SavableUser(String uuid, SavableAdapter.SavableType type) {
        super(uuid, type, FileUtils.getCorrectSavableTypeFile(uuid, type));

        if (this.uuid == null) return;
        if (this.uuid.equals("")) return;

        populateDefaults();

        this.savableUser = this;
        this.latestVersion = getLatestVersion();
    }

    public void populateDefaults() {
        this.uuid = this.identifier;
        // Profile.
        latestName = getOrSetDefault("profile.latest.name", BasePlayerHandler.getSourceName(findSender()));
        latestVersion = getOrSetDefault("profile.latest.version", getLatestVersion());
        latestServer = getOrSetDefault("profile.latest.server", StreamlineBase.MESSAGES.nullString);
        displayName = getOrSetDefault("profile.display-name", latestName);
        tagList = getOrSetDefault("profile.tags", getTagsFromConfig());
        points = getOrSetDefault("profile.points", StreamlineBase.SAVABLES.pointsDefault);

        populateMoreDefaults();
    }

    abstract public List<String> getTagsFromConfig();

    abstract public void populateMoreDefaults();

    @Override
    public void loadValues() {
        // Profile.
        latestName = getOrSetDefault("profile.latest.name", latestName);
        latestVersion = getOrSetDefault("profile.latest.version", latestVersion);
        latestServer = getOrSetDefault("profile.latest.server", latestServer);
        displayName = getOrSetDefault("profile.display-name", displayName);
        tagList = getOrSetDefault("profile.tags", tagList);
        points = getOrSetDefault("profile.points", points);
        // Online.
        online = updateOnline();
        // More.
        loadMoreValues();
    }

    abstract public void loadMoreValues();

    public void saveAll() {
        try {
            // Profile.
            set("profile.latest.name", latestName);
            set("profile.latest.version", latestVersion);
            set("profile.latest.server", latestServer);
            set("profile.display-name", latestName);
            set("profile.tags", tagList);
            set("profile.points", points);
            // More.
            saveMore();
        } catch (Exception e) {
            // do nothing
        }
    }

    abstract public void saveMore();

    public void addTag(String tag) {
        if (tagList.contains(tag)) return;

        tagList.add(tag);
        saveAll();
    }

    public void removeTag(String tag) {
        if (! tagList.contains(tag)) return;

        tagList.remove(tag);
        saveAll();
    }

    public void setPoints(double amount) {
//        //        loadValues();
        points = amount;
        saveAll();
    }

    public void addPoints(double amount) {
        //        loadValues();
        setPoints(points + amount);
    }

    public void removePoints(double amount) {
        //        loadValues();
        setPoints(points - amount);
    }

    public void setLatestServer(String server) {
        //this.latestServer = server;
        latestServer = server;
        saveAll();
    }

    public void setLatestName(String name) {
        latestName = name;
    }

    public void setDisplayName(String name) {
//        loadValues();
        displayName = name;
        saveAll();
    }

    public String getName() {
        return latestName;
    }

    public boolean hasPermission(String permission) {
        return findSender().hasPermission(permission);
    }

    public void setPermission(String permission, boolean value) {
        findSender().setPermission(permission, value);
    }

    public void sendMessage(BaseComponent message) {
        if (this.findSenderOptional().isPresent()) {
            this.findSenderOptional().get().sendMessage(message);
        }
    }

    public void sendMessage(String message) {
        if (this.findSenderOptional().isPresent()) {
            this.findSenderOptional().get().sendMessage(BaseMessaging.codedText(message));
        }
    }

    public void dispose() throws Throwable {
        try {
            SavableHandler.removeUser(this);
            this.uuid = null;
            this.file.delete();
        } finally {
            super.finalize();
        }
    }
}
