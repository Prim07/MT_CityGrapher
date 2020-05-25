package com.agh.bsct.api.models.algorithmcreated;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class AlgorithmTaskIdDTO {

    private String taskId;

    public AlgorithmTaskIdDTO(String taskId) {
        this.taskId = taskId;
    }
}
