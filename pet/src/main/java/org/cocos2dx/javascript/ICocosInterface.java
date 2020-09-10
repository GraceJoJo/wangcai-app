/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/yuguotao/works/android/2020marathon/jdd_jrapp_pet_wangcai2020/pet/src/main/aidl/org/cocos2dx/ICocosInterface.aidl
 */
package org.cocos2dx.javascript;
public interface ICocosInterface extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements ICocosInterface
{
private static final String DESCRIPTOR = "org.cocos2dx.ICocosInterface";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.cocos2dx.ICocosInterface interface,
 * generating a proxy if needed.
 */
public static ICocosInterface asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof ICocosInterface))) {
return ((ICocosInterface)iin);
}
return new Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
String descriptor = DESCRIPTOR;
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(descriptor);
return true;
}
case TRANSACTION_showPetDialog:
{
data.enforceInterface(descriptor);
this.showPetDialog();
reply.writeNoException();
return true;
}
default:
{
return super.onTransact(code, data, reply, flags);
}
}
}
private static class Proxy implements ICocosInterface
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void showPetDialog() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_showPetDialog, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_showPetDialog = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void showPetDialog() throws android.os.RemoteException;
}
