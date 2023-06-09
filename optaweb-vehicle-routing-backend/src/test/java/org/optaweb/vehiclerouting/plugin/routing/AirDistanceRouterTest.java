package org.optaweb.vehiclerouting.plugin.routing;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.service.region.BoundingBox;

class AirDistanceRouterTest {

    @Test
    void travel_time_should_be_distance_divided_by_speed() {
        AirDistanceRouter router = new AirDistanceRouter();
        Coordinates from = Coordinates.of(0, 0);
        Coordinates to = Coordinates.of(3, 4); // √(3² + 4²) = 5
        long travelTimeMillis = router.travelTimeMillis(from, to);
        assertThat(travelTimeMillis).isEqualTo((long) (5
                * AirDistanceRouter.KILOMETERS_PER_DEGREE
                / AirDistanceRouter.TRAVEL_SPEED_KPH
                * AirDistanceRouter.MILLIS_IN_ONE_HOUR));
    }

    @Test
    void bounding_box_is_the_whole_globe() {
        BoundingBox bounds = new AirDistanceRouter().getBounds();
        assertThat(bounds.getSouthWest()).isEqualTo(Coordinates.of(-90, -180));
        assertThat(bounds.getNorthEast()).isEqualTo(Coordinates.of(90, 180));
    }

    @Test
    void path_from_a_to_b_should_be_the_line_ab() {
        AirDistanceRouter router = new AirDistanceRouter();
        Coordinates from = Coordinates.of(0, 0);
        Coordinates to = Coordinates.of(3, 4);
        assertThat(router.getPath(from, to)).containsExactly(from, to);
    }
}
