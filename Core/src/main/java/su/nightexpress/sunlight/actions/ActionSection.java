package su.nightexpress.sunlight.actions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ActionSection {

    private       List<String> conditionList;
    private final String       conditionActionOnFail;
    private       List<String> actionExecutors;

    public ActionSection(@NotNull List<String> conditionList, @NotNull String conditionActionOnFail, @NotNull List<String> actionExecutors) {
        this.conditionList = conditionList;
        this.conditionActionOnFail = conditionActionOnFail;
        this.actionExecutors = actionExecutors;
    }

    public ActionSection(@NotNull ActionSection from) {
        this.conditionList = new ArrayList<>(from.getConditions()); // New list
        this.actionExecutors = new ArrayList<>(from.getActionExecutors()); // New list
        this.conditionActionOnFail = from.conditionActionOnFail;
    }

    @NotNull
    public List<String> getConditions() {
        return this.conditionList;
    }

    public void setConditions(@NotNull List<String> conditionsList) {
        this.conditionList = conditionsList;
    }

    @NotNull
    public String getConditionFailActions() {
        return this.conditionActionOnFail;
    }

    @NotNull
    public List<String> getActionExecutors() {
        return this.actionExecutors;
    }

    public void setActionExecutors(@NotNull List<String> actionExecutors) {
        this.actionExecutors = actionExecutors;
    }
}
