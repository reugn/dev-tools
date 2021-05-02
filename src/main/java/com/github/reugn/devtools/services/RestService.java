package com.github.reugn.devtools.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.github.reugn.devtools.controllers.RestAPIController;
import com.github.reugn.devtools.models.Request;
import com.github.reugn.devtools.models.RestResponse;

import javafx.application.Platform;

public class RestService {

	private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
	public static final List<Request> REQ_HISTORY_LIST = new ArrayList<>();
	private static RestAPIController CONTROLLER;
	
    private RestService() {
    }

    private static final int defaultConnectTimeout = 60000;

    public static void addToHistoryReqList(List<Request> reqs) {
    	REQ_HISTORY_LIST.addAll(reqs);
    }
    
	private static RestResponse request(String requestMethod, String uri, Map<String, String> headers, String body) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) buildURL(uri).openConnection();
		conn.setRequestMethod(requestMethod);
		for (Map.Entry<String, String> h : headers.entrySet()) {
			conn.setRequestProperty(h.getKey(), h.getValue());
		}
		conn.setConnectTimeout(defaultConnectTimeout);
		conn.setInstanceFollowRedirects(false);

		String responseBody = "";
		int status;
		long t1 = System.currentTimeMillis();
		if (!body.isEmpty()) {
			conn.setDoOutput(true);
			setRequestBody(conn.getOutputStream(), body);
		}
		try {
			status = conn.getResponseCode();
			responseBody = getResponseBody(conn.getInputStream());
		} catch (IOException e) {
			status = conn.getResponseCode();
			responseBody = getResponseBody(conn.getErrorStream());
		}
		long t2 = System.currentTimeMillis() - t1;

		String responseHeaders = getResponseHeaders(conn.getHeaderFields());
		conn.disconnect();

		Request req = new Request(uri, requestMethod, headers, body);
		if (REQ_HISTORY_LIST.stream().filter(x -> x.equals(req)).findFirst().orElse(null) == null) {
			REQ_HISTORY_LIST.add(req);
			Platform.runLater(() -> CONTROLLER.getHistoryListView().getItems().add(req));
		}
		return new RestResponse(status, responseBody, responseHeaders, t2);

	}

	public static void requestAsync(Request request, ResponseRunnable onComplete, ExceptionRunnable onError) {
		EXECUTOR.execute(() -> {
			try {
				RestResponse response = RestService.request(request.getMethod(), request.getUrl(), request.getHeaders(), request.getBody());
				if (onComplete != null)
					onComplete.run(response);
			} catch (Exception e) {
				Logger.getLogger(RestService.class).error(e.getMessage(), e);
				if (onError != null)
					onError.run(e);
			}
		});
	}
    
    public static final List<Request> getReqHistory() {
    	return REQ_HISTORY_LIST;
    }

    public static void registerController(RestAPIController controller) {
    	CONTROLLER = controller;
    }
    
    private static void setRequestBody(OutputStream os, String body) throws IOException {
        OutputStreamWriter outStreamWriter = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        outStreamWriter.write(body);
        outStreamWriter.flush();
        outStreamWriter.close();
        os.close();
    }

    private static String getResponseBody(InputStream is) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder buff = new StringBuilder();
        while ((line = in.readLine()) != null) {
            buff.append(line);
        }
        in.close();
        return buff.toString();
    }

    private static String getResponseHeaders(Map<String, List<String>> fields) {
        return fields.entrySet().stream()
                .map(e -> {
                    if (e.getKey() == null)
                        return String.join(", ", e.getValue());
                    else
                        return e.getKey() + ": " + String.join(", ", e.getValue());
                })
                .collect(Collectors.joining("\n"));
    }

    private static URL buildURL(String uri) throws MalformedURLException {
        URL url;
        try {
            url = new URL(uri);
        } catch (MalformedURLException e) {
            if (uri.startsWith("http"))
                throw e;
            return buildURL("http://" + uri);
        }
        return url;
    }
}
