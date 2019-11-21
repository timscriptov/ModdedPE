package com.microsoft.onlineid.sts.request;

import android.content.Context;
import android.text.TextUtils;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.internal.Applications;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.PackageInfoHelper;
import com.microsoft.onlineid.internal.Scopes;
import com.microsoft.onlineid.internal.transport.TransportFactory;
import com.microsoft.onlineid.sdk.BuildConfig;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import com.microsoft.onlineid.sts.ClockSkewManager;
import com.microsoft.onlineid.sts.DeviceCredentials;
import com.microsoft.onlineid.sts.DeviceIdentity;
import com.microsoft.onlineid.sts.ServerConfig;
import com.microsoft.onlineid.sts.ServerConfig.Endpoint;
import com.microsoft.onlineid.sts.XmlSigner;

public class StsRequestFactory {
    protected final Context _applicationContext;
    private final ClockSkewManager _clockSkewManager;

    public StsRequestFactory(Context applicationContext) {
        this._applicationContext = applicationContext;
        this._clockSkewManager = new ClockSkewManager(applicationContext);
    }

    public StsRequestFactory(Context applicationContext, ClockSkewManager clockSkewManager) {
        this._applicationContext = applicationContext;
        this._clockSkewManager = clockSkewManager;
    }

    public DeviceProvisionRequest createDeviceProvisionRequest(DeviceCredentials credentials) {
        DeviceProvisionRequest request = new DeviceProvisionRequest();
        initializeRequest(request);
        request.setDeviceCredentials(credentials);
        return request;
    }

    public DeviceAuthRequest createDeviceAuthRequest(DeviceIdentity identity) {
        DeviceAuthRequest request = new DeviceAuthRequest();
        initializeRequest(request);
        request.setDeviceCredentials(identity.getCredentials());
        return request;
    }

    public ServiceRequest createServiceRequest(AuthenticatorUserAccount userAccount, DeviceIdentity deviceIdentity, ISecurityScope scope, String packageName, String flowToken) {
        return createServiceRequest(userAccount, deviceIdentity, scope, packageName, flowToken, false);
    }

    public ServiceRequest createServiceRequest(AuthenticatorUserAccount userAccount, DeviceIdentity deviceIdentity, ISecurityScope scope, String packageName, String flowToken, boolean requestFlights) {
        Objects.verifyArgumentNotNull(userAccount, "userAccount");
        Objects.verifyArgumentNotNull(deviceIdentity, "deviceIdentity");
        Objects.verifyArgumentNotNull(scope, Scopes.ScopeParameterName);
        ServiceRequest request = new ServiceRequest();
        initializeRequest(request);
        request.setRequestFlights(requestFlights);
        request.setUserDA(userAccount.getDAToken());
        request.setDeviceDA(deviceIdentity.getDAToken());
        request.addRequest(scope);
        request.setFlowToken(flowToken);
        request.setClientAppUri(Applications.buildClientAppUri(this._applicationContext, packageName));
        request.setTelemetry(buildTelemetry());
        return request;
    }

    public ServiceRequest createServiceRequest(AuthenticatorUserAccount userAccount, DeviceIdentity deviceIdentity, String packageName, ISecurityScope scope) {
        return createServiceRequest(userAccount, deviceIdentity, scope, packageName, null);
    }

    protected void initializeRequest(AbstractStsRequest<?> request) {
        ServerConfig config = getConfig();
        Endpoint endpoint = request.getEndpoint();
        Assertion.check(endpoint != null);
        request.setDestination(config.getUrl(endpoint));
        request.setTransportFactory(new TransportFactory(this._applicationContext));
        request.setClockSkewManager(this._clockSkewManager);
        request.setMsaAppVersionCode(PackageInfoHelper.getCurrentAppVersionCode(this._applicationContext));
        if (request instanceof ISignableRequest) {
            ((ISignableRequest) request).setXmlSigner(new XmlSigner());
        }
    }

    protected ServerConfig getConfig() {
        return new ServerConfig(this._applicationContext);
    }

    private String buildTelemetry() {
        String appStore = this._applicationContext.getPackageManager().getInstallerPackageName(this._applicationContext.getPackageName());
        return TextUtils.isEmpty(appStore) ? BuildConfig.VERSION_NAME : "PackageMarket=" + appStore;
    }
}
