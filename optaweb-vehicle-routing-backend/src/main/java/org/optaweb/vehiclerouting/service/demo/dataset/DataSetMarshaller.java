package org.optaweb.vehiclerouting.service.demo.dataset;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.LocationData;
import org.optaweb.vehiclerouting.domain.RoutingProblem;
import org.optaweb.vehiclerouting.domain.VehicleData;
import org.optaweb.vehiclerouting.domain.VehicleFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Data set marshaller using the YAML format.
 */
@ApplicationScoped
public class DataSetMarshaller {

    private final ObjectMapper mapper;

    /**
     * Create marshaller using the default object mapper, which is set up to use YAML format.
     */
    DataSetMarshaller() {
        mapper = new ObjectMapper(new YAMLFactory());
    }

    /**
     * Constructor for testing purposes.
     *
     * @param mapper usually a mock object mapper
     */
    DataSetMarshaller(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Unmarshal routing problem from a reader.
     *
     * @param reader a reader
     * @return routing problem
     */
    public RoutingProblem unmarshal(Reader reader) {
        // TODO throw a checked exception that will force the caller to handle the reading problem
        //      (e.g. a bad format) and report it to the user or log an error
        return toDomain(unmarshalToDataSet(reader));
    }

    /**
     * Marshal routing problem to string.
     *
     * @param routingProblem routing problem
     * @return string containing the marshaled routing problem
     */
    public String marshal(RoutingProblem routingProblem) {
        return marshal(toDataSet(routingProblem));
    }

    DataSet unmarshalToDataSet(Reader reader) {
        try {
            return mapper.readValue(reader, DataSet.class);
        } catch (IOException e) {
            throw new IllegalStateException("Can't read demo data set", e);
        }
    }

    String marshal(DataSet dataSet) {
        try {
            return mapper.writeValueAsString(dataSet);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to marshal data set (" + dataSet.getName() + ")", e);
        }
    }

    static DataSet toDataSet(RoutingProblem routingProblem) {
        DataSet dataSet = new DataSet();
        dataSet.setName(routingProblem.name());
        dataSet.setDepot(routingProblem.depot().map(DataSetMarshaller::toDataSet).orElse(null));
        dataSet.setVehicles(routingProblem.vehicles().stream()
                .map(DataSetMarshaller::toDataSet)
                .collect(toList()));
        dataSet.setVisits(routingProblem.visits().stream()
                .map(DataSetMarshaller::toDataSet)
                .collect(toList()));
        return dataSet;
    }

    static DataSetLocation toDataSet(LocationData locationData) {
        return new DataSetLocation(
                locationData.description(),
                locationData.coordinates().latitude().doubleValue(),
                locationData.coordinates().longitude().doubleValue());
    }

    static DataSetVehicle toDataSet(VehicleData vehicleData) {
        return new DataSetVehicle(vehicleData.name(), vehicleData.capacity());
    }

    static RoutingProblem toDomain(DataSet dataSet) {
        return new RoutingProblem(
                Optional.ofNullable(dataSet.getName()).orElse(""),
                Optional.ofNullable(dataSet.getVehicles()).orElse(Collections.emptyList())
                        .stream()
                        .map(DataSetMarshaller::toDomain)
                        .collect(toList()),
                Optional.ofNullable(dataSet.getDepot()).map(DataSetMarshaller::toDomain).orElse(null),
                Optional.ofNullable(dataSet.getVisits()).orElse(Collections.emptyList())
                        .stream()
                        .map(DataSetMarshaller::toDomain)
                        .collect(toList()));
    }

    static LocationData toDomain(DataSetLocation dataSetLocation) {
        return new LocationData(
                Coordinates.of(dataSetLocation.getLatitude(), dataSetLocation.getLongitude()),
                dataSetLocation.getLabel());
    }

    static VehicleData toDomain(DataSetVehicle dataSetVehicle) {
        return VehicleFactory.vehicleData(dataSetVehicle.name, dataSetVehicle.capacity);
    }
}
