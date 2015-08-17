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
    private ArrayList<ActorRef> mPhilosophers;
    private int timetostop;

    private Waiter(int bottleCount) {
        mBottle = new BottleState[bottleCount];
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
        else if(message instanceof Messages.Thirsty){
            getSender().tell(new Messages.Drinking(),getSelf());
        }
        else if(message instanceof Messages.FinishDrinking){
            timetostop++;
            getSender().tell(new Messages.Stop(),getSelf());
            if(timetostop==mPhilosophers.size())getContext().system().shutdown();
        }
    }
}
