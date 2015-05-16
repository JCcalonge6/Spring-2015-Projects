package comjccalonge6.httpsgithub.jccalongescardpricer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    //private static final int EDIT = 0, DELETE = 1;
    private static final int DELETE = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    EditText cardNameTxt;
    ImageView cardImageImgView;
    List<History> searches = new ArrayList<History>();
    ListView searchListView;
    Uri imageUri = Uri.parse("android.resource://comjccalonge6.httpsgithub.jccalongescardpricer/drawable/cfv_back.png");
    DatabaseHandler dbHandler;
    int longClickedItemIndex;
    ArrayAdapter<History> searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        cardNameTxt = (EditText) findViewById(R.id.txtCardName);
        searchListView = (ListView) findViewById(R.id.listView);
        cardImageImgView = (ImageView) findViewById(R.id.imgViewCardImage);
        dbHandler = new DatabaseHandler(getApplicationContext());

        registerForContextMenu(searchListView);

        searchListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longClickedItemIndex = position;
                return false;
            }
        });

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("pricer");
        tabSpec.setContent(R.id.tabPricer);
        tabSpec.setIndicator("Pricer");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("history");
        tabSpec.setContent(R.id.tabHistory);
        tabSpec.setIndicator("History");
        tabHost.addTab(tabSpec);

        final Button pictureBtn = (Button) findViewById(R.id.btnPicture);

        if(!hasCamera())
            pictureBtn.setEnabled(false);
        /*
        pictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Camera is being launched...",Toast.LENGTH_SHORT).show();
            }
        });
        */
        final Button pricerBtn = (Button) findViewById(R.id.btnPricer);

        /*
        pricerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                History history = new History(dbHandler.getSearchesCount(), String.valueOf(cardNameTxt.getText()), imageUri);
                //if (!searchExists(history)) {
                    dbHandler.createHistory(history);
                    searches.add(history);
                    searchAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "Retrieving Prices...\nPlacing in History..." , Toast.LENGTH_SHORT).show();
                    //return;
                //}
                //Toast.makeText(getApplicationContext(), String.valueOf(cardNameTxt.getText()) + " has already been searched.\nPlease check history.", Toast.LENGTH_SHORT).show();
            }
        });
        */

        cardNameTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pricerBtn.setEnabled(String.valueOf(cardNameTxt.getText()).trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cardImageImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Card Image"), 1);
            }
        });

        if (dbHandler.getSearchesCount() != 0)
            searches.addAll(dbHandler.getAllSearches());

        populateList();
    }

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        menu.setHeaderIcon(R.drawable.pencil_icon);
        menu.setHeaderTitle("Search Options");
        //menu.add(Menu.NONE, EDIT, menu.NONE, "Edit Search");
        menu.add(Menu.NONE, DELETE, menu.NONE, "Delete Search");
    }

    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //case EDIT:
            // TODO: Implement editing search
            //break;
            case DELETE:
                dbHandler.deleteSearch(searches.get(longClickedItemIndex));
                searches.remove(longClickedItemIndex);
                searchAdapter.notifyDataSetChanged();
                break;
        }

        return super.onContextItemSelected(item);
    }

    /* Placeholder for when you can use history to search again instead of re-searching card

    private boolean searchExists(History history) {
        String name = history.getCardName();
        int searchCount = searches.size();

        for (int i = 0; i < searchCount; i++) {
            if (name.compareToIgnoreCase(searches.get(i).getCardName()) == 0)
                return true;
        }
        return false;
    }
    */

    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == RESULT_OK) {
            if (reqCode == 1) {
                imageUri = data.getData();
                cardImageImgView.setImageURI(data.getData());
            }
        }
    }
    public void populateList(){
        searchAdapter = new HistoryListAdapter();
        searchListView.setAdapter(searchAdapter);
    }

    private class HistoryListAdapter extends ArrayAdapter<History> {
        public HistoryListAdapter() {
            super(MainActivity.this, R.layout.listview_item, searches);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.listview_item, parent, false);

            History currentSearch = searches.get(position);

            TextView cardNameTV = (TextView) view.findViewById(R.id.txtCardNameHistory);
            cardNameTV.setText(currentSearch.getCardName());
            ImageView imgViewCardImageHistory = (ImageView) view.findViewById(R.id.imgViewCardImageHistory);
            imgViewCardImageHistory.setImageURI(currentSearch.getImageURI());
            return view;
        }
    }

    private boolean hasCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    public void launchCamera(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }


    public void showPrice(View view) {
        AlertDialog.Builder priceAlert = new AlertDialog.Builder(this);
        GetPrice priceFinal = new GetPrice();
        String price="PlaceHolder";
        try {
            price = priceFinal.getInternetData(String.valueOf(cardNameTxt.getText()));
        } catch (Exception e) {
            e.printStackTrace();
            price="Price Retrieval Failed. Check Connection.";
        }
        priceAlert.setMessage(price).setPositiveButton("Save to Search History", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                History history = new History(dbHandler.getSearchesCount(), String.valueOf(cardNameTxt.getText()), imageUri);
                dbHandler.createHistory(history);
                searches.add(history);
                searchAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Placing in History...", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }).setTitle(String.valueOf(cardNameTxt.getText())).create();
        priceAlert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
