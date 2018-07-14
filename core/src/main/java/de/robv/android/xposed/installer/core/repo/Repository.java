package de.robv.android.xposed.installer.core.repo;

public class Repository {
	public String name;
	public String url;
	public boolean isPartial = false;
	public String partialUrl;
	public String version;

	/*package*/ Repository() {}
}
