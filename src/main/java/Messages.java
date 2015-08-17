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
    public static class FinishDrinking { }
    public static class Thirsty { }
    public static class Stop{}
}
