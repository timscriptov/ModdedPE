package com.microsoft.onlineid.sts;

import android.content.Context;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.onlineid.internal.ApiResult;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import com.microsoft.onlineid.sdk.R;
import com.microsoft.onlineid.sts.exception.InvalidResponseException;
import com.microsoft.onlineid.sts.exception.RequestThrottledException;
import com.microsoft.onlineid.sts.exception.StsException;
import com.microsoft.onlineid.sts.request.DeviceProvisionRequest;
import com.microsoft.onlineid.sts.request.StsRequestFactory;
import com.microsoft.onlineid.sts.response.DeviceAuthResponse;
import com.microsoft.onlineid.sts.response.DeviceProvisionResponse;

public class DeviceIdentityManager {
    static final int MaxProvisionAttemptsPerCall = 3;
    private final Context _applicationContext;
    private DeviceCredentialGenerator _credentialGenerator;
    private StsRequestFactory _requestFactory;
    private final TypedStorage _storage;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode = new int[StsErrorCode.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[StsErrorCode.PP_E_K_ERROR_DB_MEMBER_DOES_NOT_EXIST.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[StsErrorCode.PPCRL_REQUEST_E_BAD_MEMBER_NAME_OR_PASSWORD.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[StsErrorCode.PP_E_K_ERROR_DB_MEMBER_EXISTS.ordinal()] = DeviceIdentityManager.MaxProvisionAttemptsPerCall;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public DeviceIdentityManager(Context applicationContext) {
        this._applicationContext = applicationContext;
        this._storage = new TypedStorage(applicationContext);
        this._requestFactory = null;
        this._credentialGenerator = null;
    }

    DeviceIdentityManager(TypedStorage storage, DeviceCredentialGenerator credentialGenerator, StsRequestFactory factory) {
        this._applicationContext = null;
        this._storage = storage;
        this._credentialGenerator = credentialGenerator;
        this._requestFactory = factory;
    }

    public DeviceIdentity getDeviceIdentity(boolean forceReauthenticate) throws NetworkException, InvalidResponseException, StsException {
        DeviceIdentity identity = this._storage.readDeviceIdentity();
        if (identity != null && identity.getDAToken() != null && !forceReauthenticate) {
            return identity;
        }
        DeviceAuthResponse response;
        if (identity != null) {
            response = (DeviceAuthResponse) getRequestFactory().createDeviceAuthRequest(identity).send();
            if (response.succeeded()) {
                identity.setDAToken(response.getDAToken());
                this._storage.writeDeviceIdentity(identity);
                return identity;
            }
            StsError error = response.getError();
            switch (AnonymousClass1.$SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[error.getCode().ordinal()]) {
                case R.styleable.StyledTextView_isUnderlined /*1*/:
                case ApiResult.ResultUINeeded /*2*/:
                    break;
                default:
                    throw new StsException("Failed to authenticate device", error);
            }
        }
        identity = provisionNewDevice();
        response = (DeviceAuthResponse) getRequestFactory().createDeviceAuthRequest(identity).send();
        if (response.succeeded()) {
            identity.setDAToken(response.getDAToken());
            this._storage.writeDeviceIdentity(identity);
            return identity;
        }
        throw new StsException("Failed to authenticate device", response.getError());
    }

    private DeviceCredentialGenerator getCredentialGenerator() {
        if (this._credentialGenerator == null) {
            this._credentialGenerator = new DeviceCredentialGenerator();
        }
        return this._credentialGenerator;
    }

    private StsRequestFactory getRequestFactory() {
        if (this._requestFactory == null) {
            this._requestFactory = new StsRequestFactory(this._applicationContext);
        }
        return this._requestFactory;
    }

    private DeviceIdentity provisionNewDevice() throws NetworkException, InvalidResponseException, StsException {
        this._storage.deleteDeviceIdentity();
        DeviceProvisionRequest request = null;
        for (int count = 1; count <= MaxProvisionAttemptsPerCall; count++) {
            DeviceCredentials credentials = getCredentialGenerator().generate();
            if (request == null) {
                request = getRequestFactory().createDeviceProvisionRequest(credentials);
            } else {
                request.setDeviceCredentials(credentials);
            }
            DeviceProvisionResponse response = (DeviceProvisionResponse) request.send();
            if (checkProvisionResponse(count, response)) {
                DeviceIdentity identity = new DeviceIdentity(credentials, response.getPuid(), null);
                this._storage.writeDeviceIdentity(identity);
                return identity;
            }
        }
        return null;
    }

    private boolean checkProvisionResponse(int count, DeviceProvisionResponse response) throws RequestThrottledException, StsException {
        if (response.succeeded()) {
            return true;
        }
        StsError error = response.getError();
        switch (AnonymousClass1.$SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[error.getCode().ordinal()]) {
            case ApiResult.ResultUINeeded /*2*/:
            case MaxProvisionAttemptsPerCall /*3*/:
                if (count == MaxProvisionAttemptsPerCall) {
                    String message = "provisionNewDevice() exceeded allowable number of retry attempts.";
                    Logger.error("provisionNewDevice() exceeded allowable number of retry attempts.");
                    throw new RequestThrottledException("provisionNewDevice() exceeded allowable number of retry attempts.");
                }
                Logger.warning("Device provision request failed due to invalid credentials. Trying again.");
                return false;
            default:
                throw new StsException("Unable to provision device", error);
        }
    }
}
