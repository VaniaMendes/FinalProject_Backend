package aor.paj.bean;
import aor.paj.websocket.Notifier;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
@Singleton
public class TimerBean {
    @Inject
    Notifier notifier;
    @Schedule(second="*/30", minute="*/30", hour="*") // this automatic timer is set to expire every 30 seconds

    public void automaticTimer(){
        String msg = "This is just a reminder!";
        System.out.println(msg);
        notifier.send("mytoken",msg);
    }
}