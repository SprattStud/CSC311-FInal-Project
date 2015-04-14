/*
 * Copyright (c) 2015. Anthony DeDominic
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pw.dedominic.csc311_final_project;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener
{

	private ArrayAdapter<String> PLAYER_LIST;
	private ListView mListView;

	private HttpHandler mHttpHandler = new HttpHandler();
	private HttpService mHttpService = new HttpService(mHttpHandler);


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		PLAYER_LIST = new ArrayAdapter<String>(this, R.layout.activity_text);
		mListView = (ListView) findViewById(R.id.listView);

		mListView.setAdapter(PLAYER_LIST);
		mListView.setOnItemClickListener(this);
		mHttpService.getCSV();
	}

	public void onItemClick(AdapterView<?> adapterView, View view, int arg2, long arg3)
	{
		String view_string = ((TextView) view).getText().toString();

		String MAC_ADDRESS = view_string.substring(view_string.length() - 17);

		Toast.makeText(this, MAC_ADDRESS, Toast.LENGTH_LONG).show();
	}

	/**
	 * Function that takes CSV from HttpService and converts it to, sorted, meaningful data
	 */
	private void processCSV(String raw_data)
	{
		Log.e("string", raw_data);
		String[] CSV = raw_data.split("\n");
		Vector<CSVData> Strings = new Vector<>();

		for (String string : CSV)
		{
			CSVData entry = new CSVData();
			String[] split_string = string.split(",");
			entry.username = split_string[0];
			entry.distance = Double.parseDouble(split_string[1]);
			entry.MAC_ADDR = split_string[3];

			Strings.add(entry);
		}

		// sorts by distance
		Collections.sort(Strings);

		PLAYER_LIST.clear();
		for (CSVData entry : Strings)
		{
			PLAYER_LIST.add(entry.username+"\n"+Double.toString(entry.distance)+"\n"+entry
					.MAC_ADDR);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private class CSVData implements Comparable<CSVData>
	{
		public String username;
		public double distance;
		public String MAC_ADDR;

		@Override
		public int compareTo(CSVData another)
		{
			return Double.compare(this.distance, another.distance);
		}
	}

	class HttpHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			processCSV((String)msg.obj);
		}
	}
}
