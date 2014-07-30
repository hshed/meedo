package com.cw.msumit;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

import com.cw.msumit.utils.ContactImageDownloader;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

@ReportsCrashes(formKey = "dGRrWnZLQVZabHdZN2YtQnFsempBalE6MQ")
public class MyApplication extends Application {
	public ImageLoader imageLoader;
	@Override
	public void onCreate() {
		// The following line triggers the initialization of ACRA
		ACRA.init(this);
		super.onCreate();

		// https://github.com/nostra13/Android-Universal-Image-Loader
		/*DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).showStubImage(R.drawable.ic_launcher).build();*/
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).discCacheFileNameGenerator(new Md5FileNameGenerator())
				.imageDownloader(new ContactImageDownloader(getApplicationContext()))
				.build();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);

	}
}
