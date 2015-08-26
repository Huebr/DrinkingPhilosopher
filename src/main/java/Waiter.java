import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * Created by pedro on 13/08/2015.
 */
import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.Arrays;

public class Waiter extends UntypedActor {

    public static Props mkProps(int bottleCount) {
        return Props.create(Waiter.class, bottleCount);
    }

    private enum BottleState {FREE, USED};
    private BottleState[] mBottle;
    private ActorRef[] Bottleuser;
    private ActorRef[] rBottle;
    private ArrayList<ActorRef> mPhilosophers;
    private int timetostop;

    private Waiter(int bottleCount) {
        mBottle = new BottleState[bottleCount];
        rBottle=new ActorRef[bottleCount];
        Bottleuser=new ActorRef[bottleCount];
        Arrays.fill(Bottleuser,null);
        Arrays.fill(rBottle,null);
        Arrays.fill(mBottle, BottleState.FREE);
        mPhilosophers = new ArrayList<ActorRef>();
        timetostop=0;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Messages.Introduce) {
            String name = ((Messages.Introduce) message).getPhilosopherName();
            System.out.println(name + " joined table. Welcome!");
            mPhilosophers.add(getSender());
            getSender().tell(new Messages.Tranquil(), getSelf());
        }
        else if(message instanceof Messages.Request){
            int idx=((Messages.Request) message).getRb()-1;
            if(mBottle[idx].equals(BottleState.FREE)){
                Bottleuser[idx]=getSender();
                mBottle[idx]=BottleState.USED;
                //System.out.println("Filosofo"+((Messages.Request) message).getId()+" pegou garrafa"+ (idx+1));
                getSender().tell(new Messages.TakeBottle(idx + 1), getSelf());
            }
            else{
                rBottle[idx]=getSender();
                Bottleuser[idx].tell(message,getSelf());
            }

        }
        else if(message instanceof Messages.TakeBottle){
            int idx=((Messages.TakeBottle) message).getMb()-1;
            if(rBottle[idx]!=null){
                Bottleuser[idx]=rBottle[idx];
                //System.out.println("garrafa"+ (idx+1));
                rBottle[idx].tell(new Messages.TakeBottle(idx+1),getSelf());
                rBottle[idx]=null;
            }
            else{
                Bottleuser[idx]=null;
                mBottle[idx]=BottleState.FREE;
            }
        }
        else if(message instanceof Messages.FinishDrinking){
            timetostop++;
            if(timetostop==mPhilosophers.size()){
                //Thread.sleep(10);//garantir que todas as threads acabaram.
                for(ActorRef t1:mPhilosophers){
                    Thread.sleep(10);
                    t1.tell(new Messages.Stop(), getSelf());
                }
                Thread.sleep(10);
                getContext().system().shutdown();
            }
        }
    }
}
