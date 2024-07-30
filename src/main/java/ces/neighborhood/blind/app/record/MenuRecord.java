package ces.neighborhood.blind.app.record;

public record MenuRecord(
        String depth,
        String menuId,
        String parentMenuId,
        String menuDesc
) {}
