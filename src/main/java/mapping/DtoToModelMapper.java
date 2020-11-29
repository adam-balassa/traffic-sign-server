package mapping;

import hu.bme.aut.trafficsigns.api.response.DetectionResult;
import hu.bme.aut.trafficsigns.api.response.model.Detection;
import hu.bme.aut.trafficsigns.model.DetectedSign;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface DtoToModelMapper {
    DtoToModelMapper INSTANCE = Mappers.getMapper(DtoToModelMapper.class);

    @Mapping(source = "sign.signClass", target="classification.serial")
    Detection modelToDto(DetectedSign sign);

    List<Detection> modelToDto(List<DetectedSign> sign);

    @Mapping(source = "detection.classification.serial", target="signClass")
    DetectedSign dtoToModel(Detection detection, Double lat, Double lon);

    default List<DetectedSign> dtoToModel(DetectionResult result, Double lat, Double lon) {
        return result.getObjects().stream().map(
                detection -> dtoToModel(detection, lat, lon)
                ).collect(Collectors.toList());
    }

}
