/**
 * Created by pedro on 15/08/2015.
 */
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Philosopher extends UntypedActor {
    public static Props mkProps(String aName,int muid,int mytime,ArrayList<Integer> b, ActorRef aWeiter) {
        return Props.create(Philosopher.class, aName,muid,mytime, b, aWeiter);
    }

    private String name;
    private String state;
    private ActorRef waiter;
    private static final int DRINKING_TIME = 1000;
    private static final int MAX_TRANQUIL_TIME=3000;
    private ArrayList<Integer> mBottles;//myBottles
    private ArrayList<Integer> hBottles;//HoldBottles
    private ArrayList<Integer> nBottles;//needBottles
    private int maxrec;
    private int snum;
    private int mid;
    private int mtime;
    private int atualtime;
    private long ithirtytime;
    private long cthirstytime;

    private Philosopher(String aName,int id,int mytime,ArrayList<Integer> bottles, ActorRef mywaiter) {
        name = aName;
        waiter=mywaiter;
        mBottles=new ArrayList<>(bottles);
        hBottles=new ArrayList<>();
        nBottles=new ArrayList<>();
        maxrec=0;
        snum=0;
        mid=id;
        mtime=mytime;
        atualtime=0;
        cthirstytime=0;
        // Let`s introduce ourselves to Waiter
        mywaiter.tell(new Messages.Introduce(aName), getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Messages.Tranquil) {
            System.out.println(name + " tranquil.");
            state="Tranquil";
            Random r1 = new Random(System.currentTimeMillis());
            Thread.sleep(r1.nextInt(MAX_TRANQUIL_TIME));
            ArrayList<Integer> temp = new ArrayList<>(mBottles);
            int cap = r1.nextInt(temp.size()) + 1;
            while (cap > 0) {//será
                int temp_number;
                temp_number = r1.nextInt(temp.size());
                nBottles.add(temp.get(temp_number));
                temp.remove(temp_number);
                snum = maxrec + 1;
                cap--;
            }
            System.out.println(name + " quer beber garrafas "+nBottles);
            getSelf().tell(new Messages.Thirsty(), getSelf());
        }else if(message instanceof Messages.Thirsty){
            //try drink
            state="Thirsty";
            ithirtytime=System.nanoTime();
            if(nBottles.equals(hBottles)){
                getSelf().tell(new Messages.Drinking(), getSelf());
            }else{
                for(int i=0;i<nBottles.size();++i){
                    if(!hBottles.contains(nBottles.get(i))){
                        waiter.tell(new Messages.Request(snum,nBottles.get(i),mid),getSelf());
                    }
                }
            }
        }else if(message instanceof Messages.TakeBottle){
            //System.out.println("Filosofo " + mid + " Recebeu Garrafa " + ((Messages.TakeBottle) message).getMb());
            hBottles.add(((Messages.TakeBottle) message).getMb());
            Collections.sort(hBottles);
            Collections.sort(nBottles);
            if(nBottles.equals(hBottles)) getSelf().tell(new Messages.Drinking(), getSelf());
        }
        else if(message instanceof Messages.Request){
            maxrec=Math.max(((Messages.Request) message).getSnum(),snum);
            if(state.equals("Thisty")){//nao coloquei need
                if(((Messages.Request) message).getSnum()<snum){
                    hBottles.remove((Integer)((Messages.Request) message).getRb());
                    waiter.tell(new Messages.TakeBottle(((Messages.Request) message).getRb()),getSelf());
                }
                else if(((Messages.Request) message).getSnum()==snum&&((Messages.Request) message).getId()<mid){
                    hBottles.remove((Integer)((Messages.Request) message).getRb());
                    waiter.tell(new Messages.TakeBottle(((Messages.Request) message).getRb()),getSelf());
                }
            }
        }
        else if (message instanceof Messages.Drinking) {
            cthirstytime+=System.nanoTime()-ithirtytime;
            state="Drinking";
            System.out.println(name + " drinking " + " bottles " + hBottles);
            Thread.sleep(DRINKING_TIME);
            nBottles.clear();
            for(int i=0;i<hBottles.size();++i){
                waiter.tell(new Messages.TakeBottle(hBottles.get(i)),getSelf());
            }
            hBottles.clear();
            System.out.println(name + " esta satisfeito.");
            if(++atualtime<mtime)getSelf().tell(new Messages.Tranquil(), getSelf());
            else waiter.tell(new Messages.FinishDrinking(), getSelf());
        }else if( message instanceof Messages.Stop){
            System.out.println(name+" ficou com sede por "+ TimeUnit.NANOSECONDS.toSeconds(cthirstytime)+" s.");
            System.out.println(name + " pagou a conta e saiu.");
        }
    }
}