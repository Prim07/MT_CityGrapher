package com.agh.bsct.api.models.algorithmcreated;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class AlgorithmTaskIdDTO {

    @NonNull
    private String taskId;

    public AlgorithmTaskIdDTO(@NonNull String taskId) {
        this.taskId = taskId;
    }
}
