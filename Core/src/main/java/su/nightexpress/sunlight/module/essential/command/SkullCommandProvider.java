package su.nightexpress.sunlight.module.essential.command;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.bridge.wrap.NightProfile;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.bridge.Software;
import su.nightexpress.nightcore.util.profile.CachedProfile;
import su.nightexpress.nightcore.util.profile.PlayerProfiles;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.essential.EssentialModule;
import su.nightexpress.sunlight.module.essential.EssentialPerms;
import su.nightexpress.sunlight.utils.FutureUtils;

import java.util.Base64;
import java.util.Optional;
import java.util.regex.Pattern;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.PLAYER_NAME;

public class SkullCommandProvider extends AbstractCommandProvider {

    private static final Permission PERMISSION        = EssentialPerms.COMMAND.permission("skull");
    private static final Permission PERMISSION_OTHERS = EssentialPerms.COMMAND.permission("skull.others");
    private static final Permission PERMISSION_CUSTOM = EssentialPerms.COMMAND.permission("skull.custom");

    private static final TextLocale DESCRIPTION = LangEntry.builder("Command.Skull.Custom.Desc").text("Get player's head.");

    private static final MessageLocale MESSAGE_GET_OWN_NOTIFY = LangEntry.builder("Command.Skull.GetOwn").chatMessage(
        GRAY.wrap("You have got a copy of your head.")
    );

    private static final MessageLocale MESSAGE_GET_OTHERS_NOTIFY = LangEntry.builder("Command.Skull.GetOther").chatMessage(
        GRAY.wrap("You have got " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s head.")
    );

    private static final MessageLocale MESSAGE_GET_CUSTOM_NOTIFY = LangEntry.builder("Command.Skull.GetCustom").chatMessage(
        GRAY.wrap("You have got custom " + SOFT_YELLOW.wrap(PLAYER_NAME) + " head.")
    );

    private static final MessageLocale MESSAGE_INVALID_SKULL_DATA = LangEntry.builder("Command.Skull.InvalidData").chatMessage(
        SOFT_RED.wrap("Invalid skull data provided! You must provide either: " + SOFT_YELLOW.wrap("Player name") + ", a " + SOFT_YELLOW.wrap("URL") + " or " + SOFT_YELLOW.wrap("Base 64") + " value.")
    );

    private static final Pattern NAME_PATTERN      = Pattern.compile("^[A-Za-z0-9_]+$");
    private static final Pattern URL_VALUE_PATTERN = Pattern.compile("^[0-9a-fA-F]{64}$");
    private static final Pattern BASE_64_PATTERN   = Pattern.compile("^[A-Za-z0-9+/=]{180}$");

    private final EssentialModule module;

    public SkullCommandProvider(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module) {
        super(plugin);
        this.module = module;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("skull", true, new String[]{"skull", "playerhead", "customhead"}, builder -> builder
            .playerOnly()
            .description(DESCRIPTION)
            .permission(PERMISSION)
            .withArguments(Arguments.string(CommandArguments.VALUE).localized(Lang.COMMAND_ARGUMENT_NAME_OWNER).permission(PERMISSION_OTHERS).optional())
            .executes(this::createSkull)
        );
    }

    private boolean createSkull(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();

        NightProfile profile;
        MessageLocale locale;

        if (arguments.contains(CommandArguments.VALUE)) {

            String input = arguments.getString(CommandArguments.VALUE);

            if (BASE_64_PATTERN.matcher(input).matches()) {
                if (!player.hasPermission(PERMISSION_CUSTOM)) {
                    context.errorBadPlayer();
                    return false;
                }

                String decoded = new String(Base64.getDecoder().decode(input));
                JsonObject jsonObject = JsonParser.parseString(decoded).getAsJsonObject();

                String url = Optional.ofNullable(jsonObject)
                    .flatMap(obj -> Optional.ofNullable(obj.getAsJsonObject("textures")))
                    .flatMap(textures -> Optional.ofNullable(textures.getAsJsonObject("SKIN")))
                    .flatMap(skin -> Optional.ofNullable(skin.get("url")))
                    .map(JsonElement::getAsString)
                    .orElse(null);

                if (url == null) {
                    context.send(MESSAGE_INVALID_SKULL_DATA);
                    return false;
                }

                input = url.substring(url.lastIndexOf("/") + 1);
            }

            if (URL_VALUE_PATTERN.matcher(input).matches()) {
                if (!player.hasPermission(PERMISSION_CUSTOM)) {
                    context.errorBadPlayer();
                    return false;
                }

                profile = Optional.ofNullable(PlayerProfiles.createProfileBySkinURL(input)).map(CachedProfile::queryNoUpdate).orElse(null);
                locale = MESSAGE_GET_CUSTOM_NOTIFY;
            }
            else if (input.length() <= 16 && NAME_PATTERN.matcher(input).matches()) {
                profile = Software.get().createProfile(null, input);
                locale = MESSAGE_GET_OTHERS_NOTIFY;
            }
            else {
                context.send(MESSAGE_INVALID_SKULL_DATA);
                return false;
            }
        }
        else {
            profile = PlayerProfiles.getProfile(player).query();
            locale = MESSAGE_GET_OWN_NOTIFY;
        }

        if (profile == null) return false;

        profile.update().thenAcceptAsync(updated -> {
            ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
            ItemUtil.editMeta(itemStack, SkullMeta.class, profile::apply);
            Players.addItem(player, itemStack);
            this.module.sendPrefixed(locale, player, builder -> builder.with(PLAYER_NAME, () -> String.valueOf(updated.getName())));

        }, this.plugin::runTask).whenComplete(FutureUtils::printStacktrace);

        return true;
    }
}
