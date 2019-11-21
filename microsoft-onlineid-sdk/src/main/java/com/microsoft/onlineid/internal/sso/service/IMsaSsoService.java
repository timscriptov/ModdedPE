package com.microsoft.onlineid.internal.sso.service;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IMsaSsoService extends IInterface {

    public static abstract class Stub extends Binder implements IMsaSsoService {
        private static final String DESCRIPTOR = "com.microsoft.onlineid.internal.sso.service.IMsaSsoService";
        static final int TRANSACTION_getAccount = 1;
        static final int TRANSACTION_getAccountById = 2;
        static final int TRANSACTION_getAccountPickerIntent = 4;
        static final int TRANSACTION_getAllAccounts = 3;
        static final int TRANSACTION_getSignInIntent = 5;
        static final int TRANSACTION_getSignOutIntent = 7;
        static final int TRANSACTION_getSignUpIntent = 6;
        static final int TRANSACTION_getTicket = 8;
        static final int TRANSACTION_retrieveBackup = 10;
        static final int TRANSACTION_storeBackup = 9;

        private static class Proxy implements IMsaSsoService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public Bundle getAccount(Bundle params) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    Bundle _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (params != null) {
                        _data.writeInt(Stub.TRANSACTION_getAccount);
                        params.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_getAccount, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (Bundle) Bundle.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public Bundle getAccountById(Bundle params) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    Bundle _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (params != null) {
                        _data.writeInt(Stub.TRANSACTION_getAccount);
                        params.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_getAccountById, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (Bundle) Bundle.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public Bundle getAllAccounts(Bundle params) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    Bundle _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (params != null) {
                        _data.writeInt(Stub.TRANSACTION_getAccount);
                        params.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_getAllAccounts, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (Bundle) Bundle.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public Bundle getAccountPickerIntent(Bundle params) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    Bundle _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (params != null) {
                        _data.writeInt(Stub.TRANSACTION_getAccount);
                        params.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_getAccountPickerIntent, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (Bundle) Bundle.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public Bundle getSignInIntent(Bundle params) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    Bundle _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (params != null) {
                        _data.writeInt(Stub.TRANSACTION_getAccount);
                        params.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_getSignInIntent, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (Bundle) Bundle.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public Bundle getSignUpIntent(Bundle params) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    Bundle _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (params != null) {
                        _data.writeInt(Stub.TRANSACTION_getAccount);
                        params.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_getSignUpIntent, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (Bundle) Bundle.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public Bundle getSignOutIntent(Bundle params) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    Bundle _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (params != null) {
                        _data.writeInt(Stub.TRANSACTION_getAccount);
                        params.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_getSignOutIntent, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (Bundle) Bundle.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public Bundle getTicket(Bundle params) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    Bundle _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (params != null) {
                        _data.writeInt(Stub.TRANSACTION_getAccount);
                        params.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_getTicket, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (Bundle) Bundle.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public Bundle storeBackup(Bundle params) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    Bundle _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (params != null) {
                        _data.writeInt(Stub.TRANSACTION_getAccount);
                        params.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_storeBackup, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (Bundle) Bundle.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public Bundle retrieveBackup(Bundle params) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    Bundle _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (params != null) {
                        _data.writeInt(Stub.TRANSACTION_getAccount);
                        params.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_retrieveBackup, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (Bundle) Bundle.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMsaSsoService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IMsaSsoService)) {
                return new Proxy(obj);
            }
            return (IMsaSsoService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Bundle _arg0;
            Bundle _result;
            switch (code) {
                case TRANSACTION_getAccount /*1*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    _result = getAccount(_arg0);
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(TRANSACTION_getAccount);
                        _result.writeToParcel(reply, TRANSACTION_getAccount);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_getAccountById /*2*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    _result = getAccountById(_arg0);
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(TRANSACTION_getAccount);
                        _result.writeToParcel(reply, TRANSACTION_getAccount);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_getAllAccounts /*3*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    _result = getAllAccounts(_arg0);
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(TRANSACTION_getAccount);
                        _result.writeToParcel(reply, TRANSACTION_getAccount);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_getAccountPickerIntent /*4*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    _result = getAccountPickerIntent(_arg0);
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(TRANSACTION_getAccount);
                        _result.writeToParcel(reply, TRANSACTION_getAccount);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_getSignInIntent /*5*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    _result = getSignInIntent(_arg0);
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(TRANSACTION_getAccount);
                        _result.writeToParcel(reply, TRANSACTION_getAccount);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_getSignUpIntent /*6*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    _result = getSignUpIntent(_arg0);
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(TRANSACTION_getAccount);
                        _result.writeToParcel(reply, TRANSACTION_getAccount);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_getSignOutIntent /*7*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    _result = getSignOutIntent(_arg0);
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(TRANSACTION_getAccount);
                        _result.writeToParcel(reply, TRANSACTION_getAccount);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_getTicket /*8*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    _result = getTicket(_arg0);
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(TRANSACTION_getAccount);
                        _result.writeToParcel(reply, TRANSACTION_getAccount);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_storeBackup /*9*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    _result = storeBackup(_arg0);
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(TRANSACTION_getAccount);
                        _result.writeToParcel(reply, TRANSACTION_getAccount);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_retrieveBackup /*10*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    _result = retrieveBackup(_arg0);
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(TRANSACTION_getAccount);
                        _result.writeToParcel(reply, TRANSACTION_getAccount);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    Bundle getAccount(Bundle bundle) throws RemoteException;

    Bundle getAccountById(Bundle bundle) throws RemoteException;

    Bundle getAccountPickerIntent(Bundle bundle) throws RemoteException;

    Bundle getAllAccounts(Bundle bundle) throws RemoteException;

    Bundle getSignInIntent(Bundle bundle) throws RemoteException;

    Bundle getSignOutIntent(Bundle bundle) throws RemoteException;

    Bundle getSignUpIntent(Bundle bundle) throws RemoteException;

    Bundle getTicket(Bundle bundle) throws RemoteException;

    Bundle retrieveBackup(Bundle bundle) throws RemoteException;

    Bundle storeBackup(Bundle bundle) throws RemoteException;
}
