import akka.actor.ActorRef;

/**
 * Created by pedro on 15/08/2015.
 */
public class Messages {
    private Messages() {}

    public static class Introduce {
        private String mPhilosopher;

        public Introduce(String philosopherName) {
            mPhilosopher = philosopherName;
        }

        public String getPhilosopherName() {
            return mPhilosopher;
        }
    }

    public static class Tranquil {}
    public static class Drinking { }
    public static class Thirsty{}
    public static class FinishDrinking { }
    public static class Request{
        private int msnum;
        private int rb;//requested bottle
        private int did;
        public Request(int dsnum,int reqbottle,int id){
            msnum=dsnum;
            rb=reqbottle;
            did=id;
        }
        public int getSnum(){
            return msnum;
        }
        public int getRb(){
            return rb;
        }
        public int getId(){
            return did;
        }
    }
    public static class TakeBottle{
        private int mb;
        public TakeBottle(int b){
            mb=b;
        }
        public int getMb(){
            return mb;
        }
    }
    public static class Stop{}
}
