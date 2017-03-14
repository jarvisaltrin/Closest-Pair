package com.company;

import javax.sound.sampled.Line;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class LineDetails { 
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }

    public Integer getDistance() {
        return distance;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }

    String data ;
    Integer distance ;

    public boolean isCenter() {
        return IsCenter;
    }
    public void setCenter(boolean center) {
        IsCenter = center;
    }
    boolean IsCenter;

    public int getClusterNum() {
        return clusterNum;
    }
    public void setClusterNum(int clusterNum) {
        this.clusterNum = clusterNum;
    }
    int clusterNum ;

    public List<Integer> getDataInInteger() {
        return dataInInteger;
    }
    public void setDataInInteger(List<Integer> dataInInteger) {
        this.dataInInteger = dataInInteger;
    }
    List<Integer> dataInInteger;//Store the string in integer format

    public LineDetails(String line)
    {
        String [] temp = line.split(":");
        data =  temp[0];
        //Split the string separated by commas and store in integer format
	dataInInteger =  Arrays.stream(data.split(",")).map(x ->  Integer.parseInt(x)).collect(Collectors.toList());
        if(temp.length > 1)
        clusterNum = Integer.parseInt(temp[1]);
        if (temp.length > 2)IsCenter = true;
    }
}
