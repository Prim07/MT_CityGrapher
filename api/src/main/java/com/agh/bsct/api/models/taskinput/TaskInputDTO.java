package com.agh.bsct.api.models.taskinput;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class TaskInputDTO {

    @NotNull
    private String cityName;

    @NotNull
    private Integer numberOfResults;

    @Nullable
    private String algorithmType;

    @NotNull
    private List<PrioritizedNodeDTO> prioritizedNodes;

    public Optional<String> getAlgorithmType() {
        return Optional.ofNullable(algorithmType);
    }
}
