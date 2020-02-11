package com.distributed.response;

import com.distributed.Neighbour;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class RegisterResponseMessage extends ResponseMessage {

    private int noNodes;
    private List<Neighbour> neighbours;
    private boolean neighboursExist = false;


    @Override
    public void decodeResponse(String response) {
        StringTokenizer tokenizer = new StringTokenizer(response, " ");

        super.length = tokenizer.nextToken();
        super.type = tokenizer.nextToken();
        this.noNodes = Integer.parseInt(tokenizer.nextToken());
        if (noNodes <= 2 && noNodes >= 0) {
            this.neighbours = new ArrayList<>();
            if (noNodes > 0) {
                this.neighboursExist = true;
                for (int i = 0; i < noNodes; i++) {
                    addNeighbours(tokenizer.nextToken(), tokenizer.nextToken());
                }
            }
        }
    }

    private void addNeighbours(String ip, String port) {
        int portNumber = Integer.parseInt(port);
        Neighbour neighbour = new Neighbour(ip, portNumber);
        neighbours.add(neighbour);
        //NeighbourManager.neighbours.add(neighbour);
    }

    public int getNoNodes() {
        return noNodes;
    }

    public void setNoNodes(int noNodes) {
        this.noNodes = noNodes;
    }

    public List<Neighbour> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(List<Neighbour> neighbours) {
        this.neighbours = neighbours;
    }

    public boolean isNeighboursExist() {
        return neighboursExist;
    }

    public void setNeighboursExist(boolean neighboursExist) {
        this.neighboursExist = neighboursExist;
    }
}
