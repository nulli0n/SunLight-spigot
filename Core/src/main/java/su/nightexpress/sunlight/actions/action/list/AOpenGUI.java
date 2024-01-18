package su.nightexpress.sunlight.actions.action.list;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.actions.action.AbstractActionExecutor;
import su.nightexpress.sunlight.actions.parameter.ParameterId;
import su.nightexpress.sunlight.actions.parameter.ParameterResult;

public class AOpenGUI extends AbstractActionExecutor {

    public AOpenGUI() {
        super("OPEN_GUI");
        this.registerParameter(ParameterId.NAME);
    }

    @Override
    protected void execute(@NotNull Player exe, @NotNull ParameterResult result) {
        //SunLight plugin = SunLight.getInstance();
        /*MenuManager menuManager = plugin.getModuleManager().getMenuManager();
        if (menuManager == null) return;

        String id = (String) result.getValue(ParameterId.NAME);
        if (id == null) return;

        SunMenu menu = menuManager.getMenuById(id);
        if (menu == null) return;

        menu.open(exe, true);*/
    }
}
