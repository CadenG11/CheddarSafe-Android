package com.cheddarsecurity.cheddarsafe;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cheddarsecurity.cheddarsafe.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles the API calls through Volley and token verification.
 */
public class TokenHandler {
    /** Variables **/
    public static String api_token = "";
    public static String loggedUsername = "";

    public static String api_url = "https://csapi.compsg.dev/api/v1";

    public static String recaptchaKey = "6LcgQO4kAAAAAASnzMC2PeM8kPzQOqHCTdjVvRrL";

    /**
     * Checks the payload and extracts the token. If the status equals succeeded, sets the token.
     * If the status is failed, checks the token value and Toasts messages depending on the value.
     * @param context The context from which this is called.
     * @param res The JSONObject that contains the status and the token to be checked.
     * @return Returns a boolean whether the token succeeded or not.
     */
    private static boolean checkToken(Context context, JSONObject res) {
        String status = "", token = "";
        try {
            status = res.getString("status");
            token = res.getString("token");
        } catch (JSONException je) {
            Log.e("JSONException", je.toString());
            return false;
        }
        if (status.equals("Succeeded")) {
            api_token = token;
            return true;
        }
        else {
            switch (token) {
                /* Various Errors */
                case "NoCombo":
                    Toast.makeText(context, "The provided credentials are incorrect.", Toast.LENGTH_SHORT).show();
                    break;
                case "AccountExists":
                    Toast.makeText(context, "The account already exists.", Toast.LENGTH_SHORT).show();
                    break;
                case "FolderExists":
                    Toast.makeText(context, "The folder already exists.", Toast.LENGTH_SHORT).show();
                    break;
                case "UserExists":
                    Toast.makeText(context, "The user already exists.", Toast.LENGTH_SHORT).show();
                    break;
                case "EmailExists":
                    api_token = "";
                    Toast.makeText(context, "A user with this email already exists.", Toast.LENGTH_SHORT).show();
                    break;
                case "RateLimitExceeded":
                    api_token = "";
                    Toast.makeText(context, "Rate limit exceeded. Please try again shortly.", Toast.LENGTH_SHORT).show();
                    break;
                /* Token Errors */
                case "DecodeErr":
                    api_token = "";
                    Toast.makeText(context, "Failed to decode token.", Toast.LENGTH_SHORT).show();
                    break;
                case "TokenErr":
                    api_token = "";
                    Toast.makeText(context, "A token error has occurred.", Toast.LENGTH_SHORT).show();
                    break;
                case "RecaptchaFailed":
                    api_token = "";
                    Toast.makeText(context, "Recaptcha failed.", Toast.LENGTH_SHORT).show();
                    break;
                case "Expired":
                    api_token = "";
                    Toast.makeText(context, "This session has expired." +
                            " Please log in.", Toast.LENGTH_SHORT).show();
                    Intent login = new Intent(context, LoginActivity.class);
                    context.startActivity(login);
                    break;
            }
            return false;
        }
    }

    /**
     * Method to send a volley request to the API.
     *
     * @param context The context of which this method was called from.
     * @param type The type of method to be used for the request.
     * @param url The url of the API to call.
     * @param data The JSONObject to send to the API.
     * @param callback The callback method to run on success and on failure.
     */
    public static void sendVolley(Context context, int type, String url, JSONObject data, ServerCallback callback) {
        try {
            JsonObjectRequest jsonRequest = new JsonObjectRequest(type, url, data,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.R)
                        @Override
                        public void onResponse(JSONObject response) {
                            if (checkToken(context, response)) {
                                callback.onSuccess(response);
                            }
                            else {
                                callback.onFailure(response);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error response", error.toString());
                        }
                    })
            {
                /**
                 * Passing some request headers*
                 */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    headers.put("token", api_token);
                    return headers;
                }
            };
            /* Adds the request to the queue. */
            RequestQueue queue = CSSingleton.getInstance(context.getApplicationContext()).getRequestQueue();
            CSSingleton.getInstance(context).addToRequestQueue(jsonRequest);
        }
        catch (Exception e) {
            Log.e("Error", e.toString());
        }
    }
}
