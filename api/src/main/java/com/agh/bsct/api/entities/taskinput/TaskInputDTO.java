package com.agh.bsct.api.entities.taskinput;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Getter
@NoArgsConstructor
public class TaskInputDTO {

    @NotNull
    private String cityName;

    @NotNull
    private Integer numberOfResults;

    @Nullable
    private String algorithmType;

    public Optional<String> getAlgorithmType() {
        return Optional.ofNullable(algorithmType);
    }
}
