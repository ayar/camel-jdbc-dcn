package com.ayar.tools.jpa.dcn;

import java.util.Observable;

public  abstract class NotificationRegistration extends Observable {
	public abstract  void register(String query,boolean discardOld);

public abstract void start();

public  abstract void stop();

public abstract void unregister();
}
