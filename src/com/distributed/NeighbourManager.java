package com.distributed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NeighbourManager {

    private static final List<Neighbour> neighbours = new ArrayList<>();


    public static List<Neighbour> getNeighbours() {
        return neighbours;
    }

    public static void addNeighbour(Neighbour neighbour) throws IOException {
        neighbours.add(neighbour);
    }

    public static Neighbour removeNeighbour(Neighbour neighbour){
        neighbours.remove(neighbour);
        return neighbour;
    }

    public static void clearNeighbourList(){
        neighbours.clear();
    }

}
