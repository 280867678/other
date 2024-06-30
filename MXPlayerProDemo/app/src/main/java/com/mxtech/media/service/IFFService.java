package com.mxtech.media.service;

import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.dex */
public interface IFFService extends IInterface {
    void r_cancel(long j) throws RemoteException;

    long r_create(String str, boolean z) throws RemoteException;

    int r_displayHeight(long j) throws RemoteException;

    int r_displayWidth(long j) throws RemoteException;

    int r_duration(long j) throws RemoteException;

    Bitmap r_extractThumb(long j, int i, int i2, int i3, boolean z) throws RemoteException;

    int r_frameTime(long j) throws RemoteException;

    String r_getFormat(long j, int i) throws RemoteException;

    String r_getMetadata(long j, int i, String str) throws RemoteException;

    int r_getStreamBitRate(long j, int i) throws RemoteException;

    int r_getStreamChannelCount(long j, int i) throws RemoteException;

    long r_getStreamChannelLayout(long j, int i) throws RemoteException;

    String r_getStreamCodec(long j, int i) throws RemoteException;

    int r_getStreamCodecId(long j, int i) throws RemoteException;

    int r_getStreamCount(long j) throws RemoteException;

    int r_getStreamDisplayHeight(long j, int i) throws RemoteException;

    int r_getStreamDisplayWidth(long j, int i) throws RemoteException;

    int r_getStreamDisposition(long j, int i) throws RemoteException;

    int r_getStreamFrameTime(long j, int i) throws RemoteException;

    int r_getStreamHeight(long j, int i) throws RemoteException;

    String r_getStreamMetadata(long j, int i, int i2, String str) throws RemoteException;

    String r_getStreamProfile(long j, int i) throws RemoteException;

    int r_getStreamSampleRate(long j, int i) throws RemoteException;

    int r_getStreamType(long j, int i) throws RemoteException;

    int[] r_getStreamTypes(long j) throws RemoteException;

    int r_getStreamWidth(long j, int i) throws RemoteException;

    boolean r_hasEmbeddedSubtitle(long j) throws RemoteException;

    int r_height(long j) throws RemoteException;

    int r_isInterlaced(long j) throws RemoteException;

    void r_release(long j) throws RemoteException;

    int r_rotation(long j) throws RemoteException;

    int r_width(long j) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IFFService {
        private static final String DESCRIPTOR = "com.mxtech.media.service.IFFService";
        static final int TRANSACTION_r_cancel = 3;
        static final int TRANSACTION_r_create = 1;
        static final int TRANSACTION_r_displayHeight = 10;
        static final int TRANSACTION_r_displayWidth = 9;
        static final int TRANSACTION_r_duration = 5;
        static final int TRANSACTION_r_extractThumb = 13;
        static final int TRANSACTION_r_frameTime = 4;
        static final int TRANSACTION_r_getFormat = 14;
        static final int TRANSACTION_r_getMetadata = 15;
        static final int TRANSACTION_r_getStreamBitRate = 27;
        static final int TRANSACTION_r_getStreamChannelCount = 30;
        static final int TRANSACTION_r_getStreamChannelLayout = 29;
        static final int TRANSACTION_r_getStreamCodec = 24;
        static final int TRANSACTION_r_getStreamCodecId = 23;
        static final int TRANSACTION_r_getStreamCount = 31;
        static final int TRANSACTION_r_getStreamDisplayHeight = 20;
        static final int TRANSACTION_r_getStreamDisplayWidth = 19;
        static final int TRANSACTION_r_getStreamDisposition = 22;
        static final int TRANSACTION_r_getStreamFrameTime = 26;
        static final int TRANSACTION_r_getStreamHeight = 18;
        static final int TRANSACTION_r_getStreamMetadata = 16;
        static final int TRANSACTION_r_getStreamProfile = 25;
        static final int TRANSACTION_r_getStreamSampleRate = 28;
        static final int TRANSACTION_r_getStreamType = 21;
        static final int TRANSACTION_r_getStreamTypes = 32;
        static final int TRANSACTION_r_getStreamWidth = 17;
        static final int TRANSACTION_r_hasEmbeddedSubtitle = 12;
        static final int TRANSACTION_r_height = 7;
        static final int TRANSACTION_r_isInterlaced = 11;
        static final int TRANSACTION_r_release = 2;
        static final int TRANSACTION_r_rotation = 6;
        static final int TRANSACTION_r_width = 8;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IFFService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IFFService)) {
                return (IFFService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0 = data.readString();
                    boolean _arg1 = data.readInt() != 0;
                    long _result = r_create(_arg0, _arg1);
                    reply.writeNoException();
                    reply.writeLong(_result);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg02 = data.readLong();
                    r_release(_arg02);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg03 = data.readLong();
                    r_cancel(_arg03);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg04 = data.readLong();
                    int _result2 = r_frameTime(_arg04);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg05 = data.readLong();
                    int _result3 = r_duration(_arg05);
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg06 = data.readLong();
                    int _result4 = r_rotation(_arg06);
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg07 = data.readLong();
                    int _result5 = r_height(_arg07);
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg08 = data.readLong();
                    int _result6 = r_width(_arg08);
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg09 = data.readLong();
                    int _result7 = r_displayWidth(_arg09);
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg010 = data.readLong();
                    int _result8 = r_displayHeight(_arg010);
                    reply.writeNoException();
                    reply.writeInt(_result8);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg011 = data.readLong();
                    int _result9 = r_isInterlaced(_arg011);
                    reply.writeNoException();
                    reply.writeInt(_result9);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg012 = data.readLong();
                    boolean _result10 = r_hasEmbeddedSubtitle(_arg012);
                    reply.writeNoException();
                    reply.writeInt(_result10 ? 1 : 0);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg013 = data.readLong();
                    int _arg12 = data.readInt();
                    int _arg2 = data.readInt();
                    int _arg3 = data.readInt();
                    boolean _arg4 = data.readInt() != 0;
                    Bitmap _result11 = r_extractThumb(_arg013, _arg12, _arg2, _arg3, _arg4);
                    reply.writeNoException();
                    if (_result11 != null) {
                        reply.writeInt(1);
                        _result11.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg014 = data.readLong();
                    int _arg13 = data.readInt();
                    String _result12 = r_getFormat(_arg014, _arg13);
                    reply.writeNoException();
                    reply.writeString(_result12);
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg015 = data.readLong();
                    int _arg14 = data.readInt();
                    String _arg22 = data.readString();
                    String _result13 = r_getMetadata(_arg015, _arg14, _arg22);
                    reply.writeNoException();
                    reply.writeString(_result13);
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg016 = data.readLong();
                    int _arg15 = data.readInt();
                    int _arg23 = data.readInt();
                    String _arg32 = data.readString();
                    String _result14 = r_getStreamMetadata(_arg016, _arg15, _arg23, _arg32);
                    reply.writeNoException();
                    reply.writeString(_result14);
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg017 = data.readLong();
                    int _arg16 = data.readInt();
                    int _result15 = r_getStreamWidth(_arg017, _arg16);
                    reply.writeNoException();
                    reply.writeInt(_result15);
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg018 = data.readLong();
                    int _arg17 = data.readInt();
                    int _result16 = r_getStreamHeight(_arg018, _arg17);
                    reply.writeNoException();
                    reply.writeInt(_result16);
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg019 = data.readLong();
                    int _arg18 = data.readInt();
                    int _result17 = r_getStreamDisplayWidth(_arg019, _arg18);
                    reply.writeNoException();
                    reply.writeInt(_result17);
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg020 = data.readLong();
                    int _arg19 = data.readInt();
                    int _result18 = r_getStreamDisplayHeight(_arg020, _arg19);
                    reply.writeNoException();
                    reply.writeInt(_result18);
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg021 = data.readLong();
                    int _arg110 = data.readInt();
                    int _result19 = r_getStreamType(_arg021, _arg110);
                    reply.writeNoException();
                    reply.writeInt(_result19);
                    return true;
                case 22:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg022 = data.readLong();
                    int _arg111 = data.readInt();
                    int _result20 = r_getStreamDisposition(_arg022, _arg111);
                    reply.writeNoException();
                    reply.writeInt(_result20);
                    return true;
                case 23:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg023 = data.readLong();
                    int _arg112 = data.readInt();
                    int _result21 = r_getStreamCodecId(_arg023, _arg112);
                    reply.writeNoException();
                    reply.writeInt(_result21);
                    return true;
                case 24:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg024 = data.readLong();
                    int _arg113 = data.readInt();
                    String _result22 = r_getStreamCodec(_arg024, _arg113);
                    reply.writeNoException();
                    reply.writeString(_result22);
                    return true;
                case 25:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg025 = data.readLong();
                    int _arg114 = data.readInt();
                    String _result23 = r_getStreamProfile(_arg025, _arg114);
                    reply.writeNoException();
                    reply.writeString(_result23);
                    return true;
                case 26:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg026 = data.readLong();
                    int _arg115 = data.readInt();
                    int _result24 = r_getStreamFrameTime(_arg026, _arg115);
                    reply.writeNoException();
                    reply.writeInt(_result24);
                    return true;
                case 27:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg027 = data.readLong();
                    int _arg116 = data.readInt();
                    int _result25 = r_getStreamBitRate(_arg027, _arg116);
                    reply.writeNoException();
                    reply.writeInt(_result25);
                    return true;
                case 28:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg028 = data.readLong();
                    int _arg117 = data.readInt();
                    int _result26 = r_getStreamSampleRate(_arg028, _arg117);
                    reply.writeNoException();
                    reply.writeInt(_result26);
                    return true;
                case 29:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg029 = data.readLong();
                    int _arg118 = data.readInt();
                    long _result27 = r_getStreamChannelLayout(_arg029, _arg118);
                    reply.writeNoException();
                    reply.writeLong(_result27);
                    return true;
                case 30:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg030 = data.readLong();
                    int _arg119 = data.readInt();
                    int _result28 = r_getStreamChannelCount(_arg030, _arg119);
                    reply.writeNoException();
                    reply.writeInt(_result28);
                    return true;
                case 31:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg031 = data.readLong();
                    int _result29 = r_getStreamCount(_arg031);
                    reply.writeNoException();
                    reply.writeInt(_result29);
                    return true;
                case 32:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg032 = data.readLong();
                    int[] _result30 = r_getStreamTypes(_arg032);
                    reply.writeNoException();
                    reply.writeIntArray(_result30);
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IFFService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.mxtech.media.service.IFFService
            public long r_create(String path, boolean localFileOnly) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(path);
                    _data.writeInt(localFileOnly ? 1 : 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public void r_release(long context) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public void r_cancel(long context) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public int r_frameTime(long context) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public int r_duration(long context) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public int r_rotation(long context) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public int r_height(long context) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public int r_width(long context) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public int r_displayWidth(long context) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public int r_displayHeight(long context) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public int r_isInterlaced(long context) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public boolean r_hasEmbeddedSubtitle(long context) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public Bitmap r_extractThumb(long context, int width, int height, int iteration, boolean allowBlanc) throws RemoteException {
                Bitmap _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    _data.writeInt(iteration);
                    _data.writeInt(allowBlanc ? 1 : 0);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (Bitmap) Bitmap.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public String r_getFormat(long context, int mode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    _data.writeInt(mode);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public String r_getMetadata(long context, int key, String lang3) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    _data.writeInt(key);
                    _data.writeString(lang3);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public String r_getStreamMetadata(long context, int streamIndex, int key, String lang3) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    _data.writeInt(streamIndex);
                    _data.writeInt(key);
                    _data.writeString(lang3);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public int r_getStreamWidth(long context, int stream) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    _data.writeInt(stream);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public int r_getStreamHeight(long context, int stream) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    _data.writeInt(stream);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public int r_getStreamDisplayWidth(long context, int stream) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    _data.writeInt(stream);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public int r_getStreamDisplayHeight(long context, int stream) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    _data.writeInt(stream);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public int r_getStreamType(long context, int stream) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    _data.writeInt(stream);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public int r_getStreamDisposition(long context, int stream) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    _data.writeInt(stream);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public int r_getStreamCodecId(long context, int stream) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    _data.writeInt(stream);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public String r_getStreamCodec(long context, int stream) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    _data.writeInt(stream);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public String r_getStreamProfile(long context, int stream) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    _data.writeInt(stream);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public int r_getStreamFrameTime(long context, int stream) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    _data.writeInt(stream);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public int r_getStreamBitRate(long context, int stream) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    _data.writeInt(stream);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public int r_getStreamSampleRate(long context, int stream) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    _data.writeInt(stream);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public long r_getStreamChannelLayout(long context, int stream) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    _data.writeInt(stream);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public int r_getStreamChannelCount(long context, int stream) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    _data.writeInt(stream);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public int r_getStreamCount(long context) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.mxtech.media.service.IFFService
            public int[] r_getStreamTypes(long context) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(context);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
