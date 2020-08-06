package megvii.testfacepass.pa.beans;

public class ResultBean {

    private int result;
    private String msg;
    private Object resultData;
    private String token;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ResultBean(int result, String msg, Object resultData) {
        this.result = result;
        this.msg = msg;
        this.resultData = resultData;
    }

    public ResultBean(int result, String msg, Object resultData, String token) {
        this.result = result;
        this.msg = msg;
        this.resultData = resultData;
        this.token = token;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResultData() {
        return resultData;
    }

    public void setResultData(Object resultData) {
        this.resultData = resultData;
    }
}
