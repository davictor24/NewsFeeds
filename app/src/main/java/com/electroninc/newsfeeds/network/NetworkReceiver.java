package com.electroninc.newsfeeds.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkReceiver extends BroadcastReceiver {
    private NetworkReceiverCallback receiverCallback;

    public NetworkReceiver(NetworkReceiverCallback receiverCallback) {
        this.receiverCallback = receiverCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (NetworkUtils.isConnectedOrConnecting(context))
            receiverCallback.onConnected();
        else receiverCallback.onDisconnected();
    }
}
