package com.example.nicolab.exam.net;

import android.content.Context;
import android.util.JsonReader;
import android.util.Log;

import com.example.nicolab.exam.R;
import com.example.nicolab.exam.entity.Note;
import com.example.nicolab.exam.mapping.EntityReader;
import com.example.nicolab.exam.mapping.ResourceListReader;
import com.example.nicolab.exam.util.Cancellable;
import com.example.nicolab.exam.util.OnErrorListener;
import com.example.nicolab.exam.util.OnSuccessListener;
import com.example.nicolab.exam.util.ResourceException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by nicolab on 1/25/2017.
 */
public class EntityRestClient {
    public static final String TAG = EntityRestClient.class.getSimpleName();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String APPLICATION_JSON = "application/json";
    public static final String UTF_8 = "UTF-8";
    private static final String LAST_MODIFIED = "Last-Modified";
    private final Context context;
    private final String apiUrl;
    private final String NoteUrl;
    private final OkHttpClient okHttpClient;

    public EntityRestClient(Context app) {
        Log.d(TAG, "constructor");
        this.context = app;
        apiUrl = context.getString(R.string.api_url);
        NoteUrl = apiUrl.concat("/note");
        okHttpClient = new OkHttpClient();
    }

    public Cancellable saveAsync(Note Note,  OnSuccessListener<Note> onSuccessListener, OnErrorListener onErrorListener) {
        Request.Builder builder = new Request.Builder().url(String.format("%s/%s", NoteUrl, Note.getId()));
        Log.d(TAG, Note.toJsonString());
        RequestBody body = RequestBody.create(JSON, Note.toJsonString());
            builder.method("PUT", body);
            Log.d(TAG, "PUT methd");
        return new CancellableOkHttpAsync<Note>(
                builder.build(),
                new ResponseReader<Note>() {
                    @Override
                    public Note read(Response response) throws Exception {
                        Log.d(TAG, String.valueOf(response.code()));
                        if (response.code() == 200) {
                            JsonReader reader = new JsonReader(new InputStreamReader(response.body().byteStream(), UTF_8));
                            return new EntityReader().read(reader);
                        } else { //404 not found
                            return null;
                        }
                    }
                },
                onSuccessListener,
                onErrorListener
        );
    }

    public Cancellable searchAsync(String alimentsLastUpdate, OnSuccessListener<List<Note>> onSuccessListener, OnErrorListener onErrorListener) {
        Request.Builder requestBuilder = new Request.Builder().url(String.format("%s?lastUpdated=%s", NoteUrl, alimentsLastUpdate));
        return new CancellableOkHttpAsync<List<Note>>(
                requestBuilder.build(),
                new ResponseReader<List<Note>>() {
                    @Override
                    public List<Note> read(Response response) throws Exception {
                        JsonReader reader = new JsonReader(new InputStreamReader(response.body().byteStream(), UTF_8));
                        List<Note> result =
                                new ResourceListReader<Note>(new EntityReader()).read(reader);
                        Log.d(TAG, String.valueOf(result.size()));
                        return result;

                    }
                },
                onSuccessListener,
                onErrorListener
        );
    }

    public Cancellable deleteAsync(String name, OnSuccessListener<Note> onSuccessListener, OnErrorListener onErrorListener) {
        Request.Builder builder = new Request.Builder().url(String.format("%s/%s", NoteUrl, name));
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("name", name);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        builder.method("DELETE", body);
        return new CancellableOkHttpAsync<Note>(
                builder.build(),
                new ResponseReader<Note>() {
                    @Override
                    public Note read(Response response) throws Exception {
                        if (response.code() == 200) {
                            JsonReader reader = new JsonReader(new InputStreamReader(response.body().byteStream(), UTF_8));
                            return new EntityReader().read(reader);
                        } else { //404 not found
                            return null;
                        }
                    }
                },
                onSuccessListener,
                onErrorListener
        );
    }

    public Cancellable readAsync(String name, OnSuccessListener<Note> onSuccessListener, OnErrorListener onErrorListener) {
        Request.Builder builder = new Request.Builder().url(String.format("%s/%s", NoteUrl, name));
        return new CancellableOkHttpAsync<Note>(
                builder.build(),
                new ResponseReader<Note>() {
                    @Override
                    public Note read(Response response) throws Exception {
                        if (response.code() == 200) {
                            JsonReader reader = new JsonReader(new InputStreamReader(response.body().byteStream(), UTF_8));
                            return new EntityReader().read(reader);
                        } else { //404 not found
                            return null;
                        }
                    }
                },
                onSuccessListener,
                onErrorListener
        );
    }

    //TODO: ******************************ALTE METODE*********************************************

    private static interface ResponseReader<E> {
        E read(Response response) throws Exception;
    }

    private class CancellableOkHttpAsync<E> implements Cancellable {
        private Call call;

        public CancellableOkHttpAsync(final Request req, final ResponseReader<E> responseReader,
                                      final OnSuccessListener<E> onSuccessListener, final OnErrorListener onErrorListener) {
            try {
                call = okHttpClient.newCall(req);
                Log.d(TAG, "started " + req.method() + " " + req.url());
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        notifyFailure(e, req, onErrorListener);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            //Log.d(TAG, response.body().string());
                            notifySuccess(response, req, onSuccessListener, responseReader);
                        } catch (Exception e) {
                            notifyFailure(e, req, onErrorListener);
                        }
                    }
                });
            } catch (Exception e) {
                Log.d(TAG, e.toString());
                notifyFailure(e, req, onErrorListener);
            }
        }

        @Override
        public void cancel() {
            if (call != null) {
                call.cancel();
            }
        }

        private void notifySuccess(Response response, Request request,
                                   OnSuccessListener<E> successListener, ResponseReader<E> responseReader) throws Exception {
            if (call.isCanceled()) {
                Log.d(TAG, "completed, but cancelled " + request.method() + " " + request.url());
            } else {
                Log.d(TAG, "completed " + request.method() + " " + request.url());
                successListener.onSuccess(responseReader.read(response));
            }
        }

        private void notifyFailure(Exception e, Request request, OnErrorListener errorListener) {
            if (call.isCanceled()) {
                Log.d(TAG, "failed, but cancelled " + request.method() + " " + request.url());
            } else {
                Log.d(TAG, "failed  " + request.method() + " " + request.url());
                errorListener.onError(e instanceof ResourceException ? e : new ResourceException(e));
            }
        }
    }
}
