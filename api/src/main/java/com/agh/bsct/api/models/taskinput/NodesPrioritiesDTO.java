package com.agh.bsct.api.models.taskinput;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@Setter
@AllArgsConstructor
public class NodesPrioritiesDTO {

    @NotNull
    private boolean areManualWeightsUsed;

    @NotNull
    private boolean areVoronoiWeightsUsed;

    @NotNull
    private List<PrioritizedNodeDTO> prioritizedNodes;

    public boolean areManualWeightsUsed() {
        return areManualWeightsUsed;
    }

    public boolean areVoronoiWeightsUsed() {
        return areVoronoiWeightsUsed;
    }

    public List<PrioritizedNodeDTO> getPrioritizedNodes() {
        return prioritizedNodes;
    }
}
