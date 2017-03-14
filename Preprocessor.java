package com.company;

import javax.sound.sampled.Line;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Preprocessor {

    LineDetails max ;
    int maxIdx ;
    Map<Integer,LineDetails> clusterToCenter;
    public Preprocessor() {
        clusterToCenter = new TreeMap<>();
        String outPath = "./";
	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
	//Read the contents of the file
            Stream<String> streamOfLines = reader.lines();
            final List<LineDetails> stringOfLines = streamOfLines.parallel().map(LineDetails::new).collect(Collectors.toList());//Read each line and pass it to LineDetails class
            max = stringOfLines.get(0);//Set to first line
	    //We form 50 clusters
            IntStream.range(0,50).sequential().forEach(cluster ->
            {
                LineDetails currentCenter = max;
                currentCenter.setCenter(true);
                clusterToCenter.put(cluster,currentCenter);
                max = null;
		//Find the maximum distance among all the lines
                max = stringOfLines.stream()
                        .parallel()
                        .map(l -> { distance(currentCenter,l,cluster); return  l;})
                        .max(Comparator.comparing(LineDetails::getDistance)).get();
            });

            //write the centers to file
            List<String> centers = stringOfLines.stream().filter(x -> x.isCenter()).map(x -> {
                String temp =  x.data+":"+x.clusterNum;
		//append c to depict it is center
                if(x.isCenter())return temp+":c";
                return temp;
            }).collect(Collectors.toList());
            Files.write(Paths.get(outPath+"centers"+".txt"),centers);

            //write the clusters to file
            Map<Integer,List<LineDetails>> grp = stringOfLines.stream().collect(Collectors.groupingBy(LineDetails::getClusterNum,Collectors.toList()));
            grp.forEach((k,v) -> {
                        List<String> output =  v.stream().map(x -> {
                            String temp =  x.data+":"+x.clusterNum;
                            if(x.isCenter())return temp+":c";
                            return temp;
                        }).collect(Collectors.toList());
                        try {
                            Files.write(Paths.get(outPath+k+".txt"),output);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void distance(LineDetails base, LineDetails input, int currentCluster) {
        List<Integer> baseData = base.getDataInInteger();
        List<Integer> inputData = input.getDataInInteger();
        Integer[] inverse = new Integer[baseData.size()];
        //We compute the inverse of the string
	Stream.iterate(0, x -> x + 1).limit(baseData.size()).forEach(x -> inverse[inputData.get(x)] = x);

        int tau = 0;
	//We calculate the Kendall Tau distance
        for (int i = 0; i < baseData.size(); i++) {
            for (int j = i + 1; j < baseData.size(); j++) {
                if (inverse[baseData.get(i)] > inverse[baseData.get(j)]) tau++;
            }
        }
        if (currentCluster == 0)//For first cluster
            input.setDistance(tau);
	    //For subsequent clusters to check if the newly computed distance is less than the existing distance
        if (tau < input.getDistance()) {
            input.setDistance(tau);
            input.setClusterNum(currentCluster);
        }
    }
}

