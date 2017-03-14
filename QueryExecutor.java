package com.company;

import sun.text.resources.el.CollationData_el;

import javax.sound.sampled.Line;
import java.io.IOException;
import java.nio.channels.InterruptedByTimeoutException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import static java.nio.file.Files.lines;

public class QueryExecutor {
      Map<Integer,List<LineDetails>> cache ;
      List<LineDetails> queries = null;

      public QueryExecutor()
      {
          String path = "./" ;
          List<LineDetails> centers = new ArrayList<>();
          BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
          try {
		//Read the query file by skipping the first line which contains the number of lines in the file	      
              queries = reader.lines().skip(1).map(l -> new LineDetails(l)).collect(Collectors.toList());
		//Read the file with all the centers
              Stream<String> streamofLines = lines(Paths.get(path+"centers"+".txt"));
              streamofLines.forEach(line -> centers.add(new LineDetails(line)));
          } catch (IOException e) {
              e.printStackTrace();
          }

          cache  =  new HashMap<>();
	//Calculate the distance with respect to the center          
          queries.forEach( query -> {
              centers.stream().parallel().forEach(x -> {
                  distance(query, x);
              });
		//Sort the centers as per the distance
              centers.sort(Comparator.comparing(LineDetails::getDistance));
              final List<LineDetails> mins3 = new LinkedList<>();
		//Take the 10 least cluster for querying 
              centers.stream().limit(10).parallel().forEach(c -> {
                  List<LineDetails> clusterToQuery = null;
                  try {
                      clusterToQuery = Files.lines(Paths.get(path + c.clusterNum + ".txt")).map(x -> new LineDetails(x)).collect(Collectors.toList());
                      cache.putIfAbsent(c.clusterNum, clusterToQuery);
                  } catch (IOException e) {
                  }
                  if (clusterToQuery != null)
                      clusterToQuery.stream().parallel().forEach(x -> {
                          distance(query, x);
                      });
		      //Store the minimum distance lines of each cluster in the min3 object
                  LineDetails temp = clusterToQuery.stream().min(Comparator.comparing(LineDetails::getDistance)).get();
                  clusterToQuery.stream().filter(x -> x.getDistance() == temp.distance).forEach(x -> mins3.add(x));
              });
	      //Use the min object to output all the lines at minumum distance from the query line
              LineDetails min = mins3.stream().min(Comparator.comparing(LineDetails::getDistance)).get();
              mins3.stream().filter(x -> x.getDistance() == min.distance).forEach(x -> System.out.println(x.getData()));
          });
      }

	//Compute Kendall Tau distance
    private void distance(LineDetails base, LineDetails input) {
        List<Integer> baseData = base.getDataInInteger();
        List<Integer> inputData = input.getDataInInteger();
        Integer[] inverse = new Integer[baseData.size()];
        Stream.iterate(0, x -> x + 1).limit(baseData.size()).forEach(x -> inverse[inputData.get(x)] = x);

        int tau = 0;

        for (int i = 0; i < baseData.size(); i++) {
            for (int j = i + 1; j < baseData.size(); j++) {
                if (inverse[baseData.get(i)] > inverse[baseData.get(j)]) tau++;
            }
        }
        input.setDistance(tau);
    }
}
