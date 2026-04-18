package su.nightexpress.sunlight.api;

import org.jspecify.annotations.NonNull;

public enum PortableContainer {

    ANVIL("anvil"),
    WORKBENCH("workbench"),
    ENCHANTING_TABLE("enchanting"),
    GRINDSTONE("grindstone"),
    LOOM("loom"),
    SMITHING_TABLE("smithing"),
    CARTOGRAPHY_TABLE("cartography"),
    STONECUTTER("stonecutter");

    private final String label;

    private PortableContainer(@NonNull String label) {
        this.label = label;
    }

    @NonNull
    public String label() {
        return this.label;
    }
}
