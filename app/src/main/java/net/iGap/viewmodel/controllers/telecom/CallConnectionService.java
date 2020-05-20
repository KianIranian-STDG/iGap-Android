package net.iGap.viewmodel.controllers.telecom;

import android.os.Build;
import android.os.Bundle;
import android.telecom.Connection;
import android.telecom.ConnectionRequest;
import android.telecom.ConnectionService;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.M)
public class CallConnectionService extends ConnectionService {

    @Override
    public Connection onCreateOutgoingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        return createSelfManagedConnection(request);
    }

    @Override
    public void onCreateOutgoingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request);
    }

    @Override
    public Connection onCreateIncomingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        return createSelfManagedConnection(request);
    }

    @Override
    public void onCreateIncomingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request);
    }

    private Connection createSelfManagedConnection(ConnectionRequest request) {
        CallConnection connection = new CallConnection(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            connection.setConnectionProperties(Connection.PROPERTY_SELF_MANAGED);
        }
        connection.setAddress(request.getAddress(), TelecomManager.PRESENTATION_ALLOWED);
        connection.setExtras(request.getExtras());

        // Track the phone account handle which created this connection so we can distinguish them
        // in the sample call list later.
        Bundle moreExtras = new Bundle();
        moreExtras.putParcelable(CallConnection.EXTRA_PHONE_ACCOUNT_HANDLE,
                request.getAccountHandle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            connection.putExtras(moreExtras);
        }
        connection.setVideoState(request.getVideoState());
        return connection;
    }

}
