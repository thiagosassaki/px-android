package com.mercadopago.android.px.internal.adapters;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.services.Callback;
import com.mercadopago.android.px.internal.util.ApiUtil;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ErrorHandlingCallAdapter {
    public static class ErrorHandlingCallAdapterFactory extends CallAdapter.Factory {

        @Override
        public CallAdapter<MPCall<?>, MPCallAdapter> get(@NonNull Type returnType, @NonNull Annotation[] annotations,
            @NonNull Retrofit retrofit) {
            TypeToken<?> token = TypeToken.get(returnType);
            if (token.getRawType() != MPCall.class) {
                return null;
            }
            if (!(returnType instanceof ParameterizedType)) {
                throw new IllegalStateException(
                    "MPCall must have generic type (e.g., MPCall<ResponseBody>)");
            }
            final Type responseType = ((ParameterizedType) returnType).getActualTypeArguments()[0];
            return new CallAdapter<MPCall<?>, MPCallAdapter>() {
                @Override
                public Type responseType() {
                    return responseType;
                }

                @Override
                public MPCallAdapter adapt(@NonNull final Call<MPCall<?>> call) {
                    return new MPCallAdapter<>(call);
                }
            };
        }
    }

    /**
     * Adapts a {@link Call} to {@link MPCall}.
     */
    /* default */ static class MPCallAdapter<T> implements MPCall<T> {

        private final Call<T> call;

        /* default */ MPCallAdapter(final Call<T> call) {
            this.call = call;
        }

        @Override
        public void enqueue(final Callback<T> callback) {
            call.enqueue(new retrofit2.Callback<T>() {
                @Override
                public void onResponse(@NonNull final Call<T> call, @NonNull final Response<T> response) {
                    final Response<T> r = response;
                    executeOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            int code = r.code();
                            if (code >= 200 && code < 300) {
                                //Get body
                                final T body = r.body();
                                callback.success(body);
                            } else {
                                callback.failure(ApiUtil.getApiException(r));
                            }
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull final Call<T> call, @NonNull Throwable t) {
                    final Throwable th = t;
                    if (++callback.attempts == 3 || (th instanceof SocketTimeoutException)) {
                        executeOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.failure(ApiUtil.getApiException(th));
                            }
                        });
                    } else {
                        call.clone().enqueue(this);
                    }
                }
            });
        }

        @Override
        public void execute(final Callback<T> callback) {
            try {
                final Response<T> execute = call.execute();
                if (execute.isSuccessful()) {
                    final T body = execute.body();
                    callback.success(body);
                } else {
                    callback.failure(ApiUtil.getApiException(execute));
                }
            } catch (IOException e) {
                callback.failure(ApiUtil.getApiException(e));
            }
        }
    }

    private static void executeOnMainThread(@NonNull final Runnable r) {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(r);
    }
}
