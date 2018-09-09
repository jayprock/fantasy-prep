package com.bitbus.fantasyprep.lineup;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class LineupService {

    public List<LineupPosition> getDefaultLineupPositions() {
        List<LineupPosition> lineupPositions = new ArrayList<>();
        lineupPositions.add(LineupPosition.QB);
        lineupPositions.add(LineupPosition.WR);
        lineupPositions.add(LineupPosition.WR);
        lineupPositions.add(LineupPosition.RB);
        lineupPositions.add(LineupPosition.RB);
        lineupPositions.add(LineupPosition.TE);
        lineupPositions.add(LineupPosition.FLEX);
        return lineupPositions;
    }
}
