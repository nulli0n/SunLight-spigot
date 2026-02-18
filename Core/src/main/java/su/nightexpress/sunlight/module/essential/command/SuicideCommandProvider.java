package su.nightexpress.sunlight.module.essential.command;

import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.essential.EssentialModule;
import su.nightexpress.sunlight.module.essential.EssentialPerms;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.GRAY;

public class SuicideCommandProvider extends AbstractCommandProvider {

    private static final Permission PERMISSION = EssentialPerms.COMMAND.permission("suicide");

    private static final TextLocale DESCRIPTION = LangEntry.builder("Command.Suicide.Desc").text("Commit suicide.");

    private static final MessageLocale MESSAGE_SUICIDE_NOTIFY = LangEntry.builder("Command.Suicide.Notify").chatMessage(
        GRAY.wrap("You have commited suicide.")
    );

    private final EssentialModule module;

    public SuicideCommandProvider(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module) {
        super(plugin);
        this.module = module;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("suicide", true, new String[]{"suicide"}, builder -> builder
            .playerOnly()
            .description(DESCRIPTION)
            .permission(PERMISSION)
            .executes(this::commitSuicide)
        );
    }

    private boolean commitSuicide(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = context.getPlayerOrThrow();
        DamageSource source = DamageSource.builder(DamageType.GENERIC_KILL).withDirectEntity(target).withCausingEntity(target).build();
        target.damage(Integer.MAX_VALUE, source);
        target.setHealth(0);
        this.module.sendPrefixed(MESSAGE_SUICIDE_NOTIFY, target, replacer -> replacer.with(CommonPlaceholders.PLAYER.resolver(target)));
        return true;
    }
}
