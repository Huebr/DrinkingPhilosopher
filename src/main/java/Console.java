/**
 * Created by pedro on 13/08/2015.
 */
import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Console {
    public static ArrayList<ArrayList<Integer>> filosofosAndBottles;
    public static int num_philosophers,num_bottles;

    public static void main(String[] args) {
        loadGarrafas();
        long startTime = System.nanoTime();
        runSimulation();
        System.out.println((TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startTime))+" s");
    }

    private static void runSimulation() {
        if (num_philosophers == 0 || filosofosAndBottles.isEmpty())
            System.out.println("Não é possivel rodar simulacao por falta de filosofos ou garrafas");
        ActorSystem system = ActorSystem.create();
        ActorRef waiter = system.actorOf(Waiter.mkProps(filosofosAndBottles.size()));
        ArrayList<ActorRef> my_philosophers=new ArrayList<>();
        for(int i=1;i<num_philosophers+1;i++){
            my_philosophers.add(system.actorOf(Philosopher.mkProps(("Filosofo "+i),filosofosAndBottles.get(i), waiter)));
        }
        system.awaitTermination();
        System.out.println("Terminou.");
    }



    public static void loadGarrafas() {//C:\Users\pedro\matrix1.txt
        String file_path;
        String line;
        ArrayList<ArrayList<String>> matrix = new ArrayList<>();
        int col = 0;
        Scanner sc = new Scanner(System.in);
        BufferedReader reader;
        while (true) {
            System.out.println("Digite o nome do arquivo");
            file_path = sc.nextLine();
            try {
                reader = new BufferedReader(new FileReader(file_path));
                while ((line = reader.readLine()) != null) {
                    matrix.add(new ArrayList<>());
                    for (char s : line.toCharArray()) {
                        matrix.get(col).add(String.valueOf(s));
                    }
                    col++;
                }
                reader.close();
                break;
            } catch (IOException ex) {
                System.out.println("Erro abertura do arquivo.");
            }
        }
        num_philosophers = matrix.size();
        filosofosAndBottles=new ArrayList<>(num_philosophers);
        for(int i=0;i<num_philosophers;++i)filosofosAndBottles.add(new ArrayList<>());
        int k = 0;
        for (int i = 0; i < matrix.size(); ++i) {
            for (int j = i+1; j < matrix.get(i).size(); ++j) {
                if (matrix.get(i).get(j).equals("1")) {
                   filosofosAndBottles.get(i).add(++k);
                    filosofosAndBottles.get(j).add(k);
                }
            }
        }
        num_bottles=k;
    }
}
