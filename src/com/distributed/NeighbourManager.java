package com.distributed;

import java.util.ArrayList;
import java.util.List;

public class NeighbourManager {

    private static final List<Neighbour> neighbours = new ArrayList<>();


    public static List<Neighbour> getNeighbours() {
        return neighbours;
    }

    public static Neighbour removeNeighbour(Neighbour neighbour){
        neighbours.remove(neighbour);
        return neighbour;
    }

}
