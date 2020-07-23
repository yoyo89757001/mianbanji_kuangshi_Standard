package megvii.testfacepass.pa.utils;

public interface MqCaback {

    public void receivedMessage(String msg);
    public void cancelMessage(String tag);
}
