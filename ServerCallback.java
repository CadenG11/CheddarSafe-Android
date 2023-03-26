package com.cheddarsecurity.cheddarsafe;

import org.json.JSONObject;

/**
 * Interface defining methods what to do on success and on failure.
 */
public interface ServerCallback {
    void onSuccess(JSONObject result);
    void onFailure(JSONObject result);
}
