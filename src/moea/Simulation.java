package moea;

import java.util.*;

/**
 * Created by Kyrre on 07.05.2016.
 */
public class Simulation {

    private  int iterations;
    private int TOUR_SIZE = 7;
    private int K = 5;
    private float crossoverRate;
    private float mutationRate;

    private float e = 0.3f;
    private List<Tour> childPopulation;
    private List<Tour> adultPopulation;
    private int populationSize;

    public Simulation(int iterations, int populationSize, float crossoverRate, float mutationRate) {
        this.iterations = iterations;
        this.childPopulation = new ArrayList<>();
        this.adultPopulation = new ArrayList<>();
        this.populationSize = populationSize;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
    }

    public void run(){
        init();
        mainLoop();
    }

    private void mainLoop() {
        for (int i = 0; i < iterations; i++) {
            develop();
            adultPopulation.addAll(childPopulation);
            childPopulation.clear();
            fastNonDominatedSort();
            System.out.println("Best individual: "+ adultPopulation.get(0));
            adultPopulation.subList(populationSize, adultPopulation.size()).clear();
            tournamentSelection();
        }
    }

    private void tournamentSelection() {
        for (int i = 0; i < populationSize /2; i++) {
            Tour firstPick = pickChampion();
            Tour secondPick = null;
            boolean duplicate = true;
            while (duplicate){
                secondPick = pickChampion();
                duplicate = secondPick == firstPick;
            }
            mate(firstPick, secondPick);
    }
    }

    private Tour pickChampion() {
        HashSet<Tour> fighters = new HashSet<>();
        Random rnd = new Random();
        while (fighters.size()<K){
            int r = rnd.nextInt(adultPopulation.size());
            fighters.add(adultPopulation.get(r));
        }
        Tour[] fightArray = fighters.toArray(new Tour[fighters.size()]);
        Arrays.sort(fightArray);
        if (1f-e>rnd.nextFloat()){
            return fightArray[0];
        }
        return fightArray[rnd.nextInt(fightArray.length-1)+1];
    }

    private void mate(Tour firstPick, Tour secondPick) {
        childPopulation.addAll(Arrays.asList(firstPick.mate(secondPick)));
    }

    private void fastNonDominatedSort() {
        for (int i = 0; i < adultPopulation.size(); i++) {
            for (int j = i+1; j < adultPopulation.size(); j++) {
                Tour outer = adultPopulation.get(i);
                Tour inner = adultPopulation.get(j);
                if (outer.isDominating(inner)){
                    outer.getDominatingSet().add(inner);
                    inner.incrementDominationCount();
                } else if (inner.isDominating(outer)){
                    inner.getDominatingSet().add(outer);
                    outer.incrementDominationCount();
                }
            }
        }
        ArrayList<Tour> currentFront = new ArrayList<>();
        for (Tour tour: adultPopulation) {
            if (tour.getDominationCount() == 0) {
                currentFront.add(tour);
            }
        }
        int rankCount = 1;
        ArrayList<Tour> nextFront = new ArrayList<>();
        while (!currentFront.isEmpty()){
            for (Tour tour: currentFront) {
                tour.setRank(rankCount);
                for (Tour dominated: tour.getDominatingSet()){
                    if (dominated.decrementDominationCount() == 0){
                        nextFront.add(dominated);
                    }
                }
            }
            currentFront = nextFront;
            nextFront = new ArrayList<>();
            rankCount++;
        }
        Collections.sort(adultPopulation);
    }

    private void develop() {
        childPopulation.forEach(Tour::develop);
    }

    private void init() {
        for (int i = 0; i < populationSize; i++) {
            adultPopulation.add(new Tour(new TspGenom(TOUR_SIZE, crossoverRate, mutationRate)).develop());
        }
    }

    public static void main(String[] args) {
        new Simulation(100, 20, 0.7f, 0.001f).run();
    }
}
