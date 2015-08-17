/**
 * Created by pedro on 15/08/2015.
 */
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

import java.util.ArrayList;
import java.util.Random;

public class Philosopher extends UntypedActor {
    public static Props mkProps(String aName,ArrayList<Integer> b, ActorRef aWeiter) {
        return Props.create(Philosopher.class, aName, b, aWeiter);
    }

    private String name;
    private ActorRef waiter;
    private static final int DRINKING_TIME = 1000;
    private ArrayList<Integer> mBottles;//myBottles
    private ArrayList<Integer> hBottles;//HoldBottles
    private ArrayList<Integer> nBottles;//needBottles
    private ArrayList<Integer> rBottles;//requestedBottles
    private int maxrec;
    private int snum;

    private Philosopher(String aName,ArrayList<Integer> bottles, ActorRef mywaiter) {
        name = aName;
        waiter=mywaiter;
        mBottles=new ArrayList<>(bottles);
        hBottles=new ArrayList<>();
        nBottles=new ArrayList<>();
        rBottles=new ArrayList<>();
        maxrec=0;
        snum=0;
        // Let`s introduce ourselves to Waiter
        mywaiter.tell(new Messages.Introduce(aName), getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Messages.Tranquil) {
            System.out.println(name + " tranquil.");
            Random r1=new Random(System.currentTimeMillis());
            Thread.sleep(r1.nextInt(3001));
            System.out.println(name + " quer beber.");
            ArrayList<Integer> temp=new ArrayList<>(mBottles);
            int cap=r1.nextInt(temp.size())+1;
            while(cap>0){//será
                int temp_number;
                temp_number=r1.nextInt(temp.size());
                nBottles.add(temp.get(temp_number));
                temp.remove(temp_number);
                snum=maxrec+1;
                cap--;
            }
            System.out.println(name + " " + nBottles);
            waiter.tell(new Messages.Thirsty(), getSelf());
        } else if (message instanceof Messages.Drinking) {
            System.out.println(name + " drinking.");
            Thread.sleep(DRINKING_TIME);
            System.out.println(name + " esta satisfeito.");
            waiter.tell(new Messages.FinishDrinking(), getSelf());
        }else if( message instanceof Messages.Stop){
            System.out.println(name + " pagou a conta e saiu.");
        }
    }
}