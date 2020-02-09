package com.distributed.response;

import com.distributed.Neighbour;
import com.distributed.NeighbourManager;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class JoinResponseMessage extends ResponseMessage {
    private List<Neighbour> neighbours;
    private int noNodes;
    private String ip;
    private String myip;
    private String port;

    @Override
    public void decodeResponse(String response) {
        StringTokenizer tokenizer = new StringTokenizer(response, " ");
        super.length = tokenizer.nextToken();
        super.type = tokenizer.nextToken();
        ip= tokenizer.nextToken();
        myip= tokenizer.nextToken();
        port =  tokenizer.nextToken();
        //this.noNodes = Integer.parseInt(tokenizer.nextToken());
        addNeighbours(ip,port);


                }




    private void addNeighbours(String ip, String port) {
        int portNumber = Integer.parseInt(port);
        Neighbour neighbour = new Neighbour(ip, portNumber);
        //neighbours.add(neighbour);
        NeighbourManager.neighbours.add(neighbour);
        System.out.println("New neighbour added: " + neighbour.toString());

    }




}