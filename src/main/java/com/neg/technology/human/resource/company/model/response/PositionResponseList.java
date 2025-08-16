package com.neg.technology.human.resource.company.model.response;

import java.util.List;

public class PositionResponseList {
    private List<PositionResponse> positions;

    public PositionResponseList() {
    }

    public PositionResponseList(List<PositionResponse> positions) {
        this.positions = positions;
    }

    public List<PositionResponse> getPositions() {
        return positions;
    }

    public void setPositions(List<PositionResponse> positions) {
        this.positions = positions;
    }
}
