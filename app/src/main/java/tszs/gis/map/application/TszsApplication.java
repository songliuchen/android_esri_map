package tszs.gis.map.application;

import android.annotation.SuppressLint;

import tszs.system.TApplication;

public class TszsApplication extends TApplication
{
	@SuppressLint("NewApi")
	@Override
	public void onCreate()
	{
		super.onCreate();

//		//初始化arcmap 许可
//		String licenseCode = "runtimelite,1000,rud7659408794,none,ZZ0RJAY3FY0GEBZNR002";
//		ArcGISRuntimeEnvironment.setLicense(licenseCode);
	}

}