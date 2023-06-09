package org.optaweb.vehiclerouting.domain;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class RoutingPlanTest {

    private final Distance distance = Distance.ofMillis(1);
    private final Vehicle vehicle = VehicleFactory.testVehicle(1);
    private final List<Vehicle> vehicles = singletonList(vehicle);
    private final Location depot = new Location(1, Coordinates.of(5, 5));
    private final Location visit = new Location(2, Coordinates.of(3, 3));
    private final RouteWithTrack emptyRoute = new RouteWithTrack(new Route(vehicle, depot, emptyList()), emptyList());
    // Track is not important (we don't check if track starts and ends in the depot and goes through all visits)
    private final List<List<Coordinates>> nonEmptyTrack = singletonList(singletonList(Coordinates.of(5, 5)));

    @Test
    void constructor_args_not_null() {
        assertThatNullPointerException()
                .isThrownBy(() -> new RoutingPlan(null, vehicles, depot, emptyList(), emptyList()));
        assertThatNullPointerException()
                .isThrownBy(() -> new RoutingPlan(distance, null, depot, emptyList(), emptyList()));
        assertThatNullPointerException()
                .isThrownBy(() -> new RoutingPlan(distance, vehicles, depot, null, emptyList()));
        assertThatNullPointerException()
                .isThrownBy(() -> new RoutingPlan(distance, vehicles, depot, emptyList(), null));
        // depot can be null
        // TODO create a factory that will prevent passing a null depot accidentally
    }

    @Test
    void no_visits_without_a_depot() {
        List<List<Coordinates>> track = singletonList(singletonList(visit.coordinates()));
        RouteWithTrack routeWithTrack = new RouteWithTrack(new Route(vehicle, depot, singletonList(visit)), track);
        assertThatIllegalArgumentException().isThrownBy(() -> new RoutingPlan(
                distance, vehicles, null, singletonList(visit), singletonList(routeWithTrack)));
    }

    @Test
    void no_routes_without_a_depot() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new RoutingPlan(distance, vehicles, null, emptyList(), singletonList(emptyRoute)));
    }

    @Test
    void there_must_be_one_route_per_vehicle_when_there_is_a_depot() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new RoutingPlan(distance, vehicles, depot, emptyList(), emptyList()))
                .withMessageContaining("Vehicles (1): [")
                .withMessageContaining("Routes' vehicleIds (0): []");
    }

    @Test
    void routes_vehicle_references_must_be_consistent_with_vehicles_in_routing_plan() {
        List<Vehicle> unexpectedVehicles = singletonList(VehicleFactory.testVehicle(vehicle.id() + 1));
        List<RouteWithTrack> routes = singletonList(emptyRoute);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new RoutingPlan(distance, unexpectedVehicles, depot, emptyList(), routes))
                .withMessageContaining("Vehicles (1): [")
                .withMessageContaining("Routes' vehicleIds (1): [" + vehicle.id() + "]");
    }

    @Test
    void routes_visit_references_must_be_consistent_with_visits_in_routing_plan() {
        Vehicle vehicle1 = VehicleFactory.testVehicle(1);
        Vehicle vehicle2 = VehicleFactory.testVehicle(2);

        Location depot = new Location(100, Coordinates.of(0, 0), "depot");
        Location visit1 = new Location(101, Coordinates.of(1, 1), "visit1");
        Location visit2 = new Location(102, Coordinates.of(2, 2), "visit2");
        Location visit3 = new Location(103, Coordinates.of(3, 3), "visit3");

        assertThatCode(() -> new RoutingPlan(
                distance,
                emptyList(), // no vehicles
                depot,
                singletonList(visit1),
                emptyList() // => no routes (no visits visited)
        )).doesNotThrowAnyException();

        assertThatIllegalArgumentException().isThrownBy(() -> new RoutingPlan(
                distance,
                singletonList(vehicle1),
                depot,
                asList(visit1, visit2),
                singletonList(
                        // visit3 extra
                        new RouteWithTrack(new Route(vehicle1, depot, asList(visit1, visit2, visit3)), nonEmptyTrack))))
                .withMessageContaining(visit3.toString());

        Location visit4 = new Location(104, Coordinates.of(4, 4), "visit4");
        assertThatIllegalArgumentException().isThrownBy(() -> new RoutingPlan(
                distance,
                asList(vehicle1, vehicle2),
                depot,
                asList(visit1, visit2, visit3),
                // visit3 missing, visit4 extra
                asList(
                        new RouteWithTrack(new Route(vehicle1, depot, asList(visit1, visit4)), nonEmptyTrack),
                        new RouteWithTrack(new Route(vehicle2, depot, singletonList(visit2)), nonEmptyTrack))))
                .withMessageContaining(visit4.toString());
    }

    @Test
    void cannot_modify_collections_externally() {
        // Use modifiable collections as the input
        ArrayList<Vehicle> vehicles = new ArrayList<>();
        ArrayList<Location> visits = new ArrayList<>();
        ArrayList<RouteWithTrack> routes = new ArrayList<>();
        vehicles.add(vehicle);
        visits.add(visit);
        routes.add(new RouteWithTrack(new Route(vehicle, depot, singletonList(visit)), nonEmptyTrack));

        RoutingPlan routingPlan = new RoutingPlan(distance, vehicles, depot, visits, routes);
        List<Vehicle> planVehicles = routingPlan.vehicles();
        List<Location> planVisits = routingPlan.visits();
        List<RouteWithTrack> planRoutes = routingPlan.routes();

        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(planVehicles::clear);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(planVisits::clear);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(planRoutes::clear);
    }

    @Test
    void empty_routing_plan_should_be_empty() {
        RoutingPlan empty = RoutingPlan.empty();
        assertThat(empty.distance()).isEqualTo(Distance.ZERO);
        assertThat(empty.vehicles()).isEmpty();
        assertThat(empty.depot()).isEmpty();
        assertThat(empty.visits()).isEmpty();
        assertThat(empty.routes()).isEmpty();
    }

    @Test
    void isEmpty() {
        assertThat(RoutingPlan.empty().isEmpty()).isTrue();
        assertThat(new RoutingPlan(Distance.ZERO, emptyList(), depot, emptyList(), emptyList()).isEmpty()).isFalse();
        assertThat(new RoutingPlan(Distance.ZERO, vehicles, null, emptyList(), emptyList()).isEmpty()).isFalse();
        assertThat(new RoutingPlan(Distance.ZERO, vehicles, depot, emptyList(), singletonList(emptyRoute)).isEmpty())
                .isFalse();
    }
}
