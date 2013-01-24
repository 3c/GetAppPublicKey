package com.cx.testappkey;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	// Use ArrayList to store the installed non-system apps
	ArrayList<AppInfo> appList = new ArrayList<AppInfo>();
	ListView app_listView;
	TextView tv_key;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);

		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			AppInfo tmpInfo = new AppInfo();
			tmpInfo.appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
			tmpInfo.packageName = packageInfo.packageName;
			tmpInfo.versionName = packageInfo.versionName;
			tmpInfo.versionCode = packageInfo.versionCode;
			tmpInfo.appIcon = packageInfo.applicationInfo.loadIcon(getPackageManager());
			// Only display the non-system app info
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				appList.add(tmpInfo);
			}

		}
		// for (int i = 0; i < appList.size(); i++) {
		// System.out.println(appList.get(i));
		// }

		// Populate data to listView
		app_listView = (ListView) findViewById(R.id.lv_app);
		tv_key = (TextView) findViewById(R.id.tv_key);
		AppAdapter appAdapter = new AppAdapter(MainActivity.this, appList);

		app_listView.setAdapter(appAdapter);
		app_listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String key = GetPublicKey.getSignInfo(MainActivity.this, appList.get(position).packageName);
				if (key != null) {
					System.out.println("public key is " + key);
					tv_key.setText(key);
					write2Sd(key);
				}

			}
		});

	}

	private void write2Sd(String key) {

		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
			Log.d("TestFile", "SD card is not avaiable/writeable right now.");
			showResult("SD card is not avaiable/writeable right now.");
			return;
		}
		try {
			String fileName = "publickey.txt";
			String pathName = "/sdcard/";
			File path = new File(pathName);
			File file = new File(pathName + fileName);
			if (!path.exists()) {
				Log.d("TestFile", "Create the path:" + pathName);
				path.mkdir();
			}
			if (!file.exists()) {
				Log.d("TestFile", "Create the file:" + fileName);
				file.createNewFile();
			}
			FileOutputStream stream = new FileOutputStream(file);
			String s = key;
			byte[] buf = s.getBytes();
			stream.write(buf);
			stream.close();
			showResult("get Success.key is in File\n" + file.getAbsolutePath());
		} catch (Exception e) {
			Log.e("TestFile", "Error on writeFilToSD.");
			showResult("Error on writeFilToSD.");
			e.printStackTrace();
		}

	}

	private void showResult(String text) {
		Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
	}

	public class AppAdapter extends BaseAdapter {

		Context context;
		ArrayList<AppInfo> dataList = new ArrayList<AppInfo>();

		public AppAdapter(Context context, ArrayList<AppInfo> inputDataList) {
			this.context = context;
			dataList.clear();
			for (int i = 0; i < inputDataList.size(); i++) {
				dataList.add(inputDataList.get(i));
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HolderView holder;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				convertView = inflater.inflate(R.layout.app_row, null);
				holder = new HolderView();
				holder.iv_appIcon = (ImageView) convertView.findViewById(R.id.icon);
				holder.tv_appName = (TextView) convertView.findViewById(R.id.appName);
				convertView.setTag(holder);
			} else {
				holder = (HolderView) convertView.getTag();
			}

			holder.iv_appIcon.setImageDrawable(dataList.get(position).appIcon);
			holder.tv_appName.setText(dataList.get(position).appName);
			return convertView;
		}
	}

	class HolderView {
		TextView tv_appName;
		ImageView iv_appIcon;

	}
}