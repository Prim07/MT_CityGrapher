package com.agh.bsct.api.entities.graphdata;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class GraphDataDTO {

    @NotNull
    private List<EdgeDTO> edgeDTOS;

    @NotNull
    private List<NodeDTO> nodeDTOS;

    public GraphDataDTO(List<EdgeDTO> edgeDTOS, List<NodeDTO> nodeDTOS) {
        this.edgeDTOS = edgeDTOS;
        this.nodeDTOS = nodeDTOS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphDataDTO that = (GraphDataDTO) o;
        return Objects.equals(edgeDTOS, that.edgeDTOS) &&
                Objects.equals(nodeDTOS, that.nodeDTOS);
    }

    @Override
    public int hashCode() {
        return Objects.hash(edgeDTOS, nodeDTOS);
    }
}
