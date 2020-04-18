package com.agh.bsct.api.models.algorithmcreated;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class AlgorithmCreatedResponseDTO {

    @NonNull
    private AlgorithmTaskIdDTO taskId;

    @NonNull
    private String uri;

    public AlgorithmCreatedResponseDTO(@NonNull AlgorithmTaskIdDTO taskId, @NonNull String uri) {
        this.taskId = taskId;
        this.uri = uri;
    }
}
