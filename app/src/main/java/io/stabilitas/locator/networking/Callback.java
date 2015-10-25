package io.stabilitas.locator.networking;

/**
 * Created by David Pabon (@david3pabon)
 */
public interface Callback<T> {

    public void onSuccess(T result);
    public void onError(NetworkError error);
}
