package su.nightexpress.sunlight.actions;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.EngineUtils;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.SunLightAPI;
import su.nightexpress.sunlight.actions.action.AbstractActionExecutor;
import su.nightexpress.sunlight.actions.condition.AbstractConditionValidator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class ActionManipulator {

    private static final SunLight ENGINE = SunLightAPI.PLUGIN;

    private final Map<String, ActionSection> actions;

    public ActionManipulator() {
        this.actions = new LinkedHashMap<>();
    }

    public ActionManipulator(@NotNull ActionManipulator copy) {
        this();
        for (Map.Entry<String, ActionSection> en : copy.getActions().entrySet()) {
            this.actions.put(en.getKey(), new ActionSection(en.getValue()));
        }
    }

    public ActionManipulator(@NotNull JYML cfg, @NotNull String path) {
        this();
        for (String id : cfg.getSection(path)) {
            String path2 = path + "." + id + ".";

            List<String> conditionList = cfg.getStringList(path2 + "Conditions.List");
            String conditionActionOnFail = cfg.getString(path2 + "Conditions.Fail_Actions", "");
            List<String> actionExecutors = cfg.getStringList(path2 + "Action_Executors");

            ActionSection engine = new ActionSection(conditionList, conditionActionOnFail, actionExecutors);
            this.actions.put(id.toLowerCase(), engine);
        }
    }

    public static boolean processConditions(@NotNull Player player, @NotNull List<String> conditionsRaw) {
        for (String conditionRaw : conditionsRaw) {
            if (EngineUtils.hasPlaceholderAPI()) {
                conditionRaw = PlaceholderAPI.setPlaceholders(player, conditionRaw);
            }

            String key = conditionRaw.split(" ")[0].replace("[", "").replace("]", "");
            AbstractConditionValidator validator = ENGINE.getActionsManager().getConditionValidator(key);
            if (validator == null) {
                ENGINE.error("Invalid condition validator '" + key + "' in '" + conditionRaw + "' !");
                continue;
            }
            if (!validator.process(player, conditionRaw)) {
                return false;
            }
        }
        return true;
    }

    @NotNull
    public ActionManipulator replace(@NotNull UnaryOperator<String> func) {
        ActionManipulator manipulatorCopy = new ActionManipulator(this);

        for (ActionSection copyEngine : manipulatorCopy.getActions().values()) {
            copyEngine.getActionExecutors().replaceAll(func);
            copyEngine.getConditions().replaceAll(func);
        }

        return manipulatorCopy;
    }

    @NotNull
    public Map<String, ActionSection> getActions() {
        return this.actions;
    }

    public void process(@NotNull Player player) {
        if (this.actions.isEmpty()) return;
        String id = new ArrayList<>(this.actions.keySet()).get(0);
        this.process(player, id);
    }

    public void process(@NotNull Player player, @NotNull String sectionId) {
        ActionSection actionSection = this.actions.get(sectionId.toLowerCase());
        if (actionSection == null || !processConditions(player, actionSection.getConditions())) return;

        // Run actions
        for (String actionRaw : actionSection.getActionExecutors()) {
            if (EngineUtils.hasPlaceholderAPI()) {
                actionRaw = PlaceholderAPI.setPlaceholders(player, actionRaw);
            }

            String key = actionRaw.split(" ")[0].replace("[", "").replace("]", "");
            AbstractActionExecutor executor = ENGINE.getActionsManager().getActionExecutor(key);
            if (executor == null) {
                ENGINE.error("Invalid action executor '" + key + "' in '" + actionRaw + "' !");
                continue;
            }

            executor.process(player, actionRaw, this);
        }
    }
}
