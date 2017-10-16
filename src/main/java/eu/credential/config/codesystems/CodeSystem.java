package eu.credential.config.codesystems;

import eu.credential.wallet.notificationmanagementservice.model.Coding;

public abstract interface CodeSystem {
	
	public abstract Coding getCoding();
	public abstract Coding getCodingFull();
	public abstract String getCode();
	public abstract String getSystem();
	public abstract String getVersion();
	public abstract String getDisplay();

}