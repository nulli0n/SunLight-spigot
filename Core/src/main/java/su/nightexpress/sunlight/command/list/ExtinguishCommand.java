package su.nightexpress.sunlight.command.list;

@Deprecated
public class ExtinguishCommand {

//    public static final String NAME = "extinguish";
//
//    public static void load(@NotNull SunLightPlugin plugin) {
//        CommandRegistry.registerDirectExecutor(NAME, (template, config) -> builder(plugin, template, config));
//        CommandRegistry.addTemplate(NAME, CommandTemplate.direct(new String[]{NAME, "ext"}, NAME));
//    }
//
//    public static DirectNodeBuilder builder(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
//        return DirectNode.builder(plugin, template.getAliases())
//            .description(Lang.COMMAND_EXTINGUISH_DESC)
//            .permission(CommandPerms.EXTINGUISH)
//            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.EXTINGUISH_OTHERS))
//            .withFlag(CommandFlags.silent().permission(CommandPerms.EXTINGUISH_OTHERS))
//            .executes((context, arguments) -> execute(plugin, context, arguments))
//            ;
//    }
//
//    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
//        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
//        if (target == null) return false;
//
//        target.setFireTicks(0);
//        if (!target.isOnline()) target.saveData();
//
//        if (context.getSender() != target) {
//            Lang.COMMAND_EXTINGUISH_TARGET.getMessage().replace(Placeholders.forPlayer(target)).send(context.getSender());
//        }
//        if (!arguments.hasFlag(CommandFlags.SILENT)) {
//            Lang.COMMAND_EXTINGUISH_NOTIFY.getMessage().send(target);
//        }
//
//        return true;
//    }
}
