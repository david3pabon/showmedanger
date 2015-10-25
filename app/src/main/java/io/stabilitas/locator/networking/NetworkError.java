package io.stabilitas.locator.networking;

/**
 * Created by David Pabon (@david3pabon)
 */
public class NetworkError {

    public static final int PARSING_ERROR = -1;
    public static final int CONNECTION_ERROR = -2;

    private String message;
    private int code;

    private NetworkError(){}

    public static NetworkError newInstance(int code, String message) {
        NetworkError error = new NetworkError();
        error.setCode(code);
        error.setMessage(message);
        return error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
