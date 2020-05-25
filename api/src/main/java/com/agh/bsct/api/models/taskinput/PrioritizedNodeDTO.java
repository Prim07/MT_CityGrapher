package com.agh.bsct.api.models.taskinput;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PrioritizedNodeDTO {

    @NotNull
    private Long priorityValue;

    @NotNull
    private List<Long> geographicalNodeDTOIds;

}
