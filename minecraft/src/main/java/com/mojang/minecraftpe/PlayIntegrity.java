package com.mojang.minecraftpe;

/**
 * 13.08.2022
 *
 * @author <a href="https://github.com/timscriptov">timscriptov</a>
 */
public class PlayIntegrity {
    native void nativePlayIntegrityComplete(int errorCode, String packageName, String appRecognitionVerdict, String deviceIntegrity, String appLicensingVerdict);

//    public static Task<IntegrityTokenResponse> requestIntegrityToken(Context context, String nonce) {
//        return IntegrityManagerFactory.create(context).requestIntegrityToken(IntegrityTokenRequest.builder().setNonce(nonce).build());
//    }
}
