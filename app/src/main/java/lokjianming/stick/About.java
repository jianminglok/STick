package lokjianming.stick;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.aboutlibraries.LibTaskCallback;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.LibsConfiguration;
import com.mikepenz.aboutlibraries.entity.Library;
import com.mikepenz.aboutlibraries.ui.LibsSupportFragment;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

/**
 * Created by mikepenz on 04.06.14.
 */
public class About extends AppCompatActivity {

    private AccountHeader headerResult = null;
    private Drawer drawer = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        //Remove line to test RTL support
        //getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.black)
                .withSavedInstance(savedInstanceState)
                .build();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withItemAnimator(new AlphaCrossFadeAnimator())
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Home").withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(1).withSelectable(false),
                        new PrimaryDrawerItem().withName("Settings").withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(2).withSelectable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("About this app").withIcon(GoogleMaterial.Icon.gmd_info).withIdentifier(3).withSelectable(true)
                )// add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //check if the drawerItem is set.
                        //there are different reasons for the drawerItem to be null
                        //--> click on the header
                        //--> click on the footer
                        //those items don't contain a drawerItem

                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 1) {
                                drawer.setSelection(1, false);
                                intent = new Intent(About.this, MainActivity.class);
                            } else if (drawerItem.getIdentifier() == 2) {
                                drawer.setSelection(2, false);
                                intent = new Intent(About.this, Settings.class);
                            } else if (drawerItem.getIdentifier() == 3) {
                                drawer.setSelection(3, false);
                                intent = new Intent(About.this, About.class);
                            }
                            if (intent != null) {
                                About.this.startActivity(intent);
                            }
                        }

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        //only set the active selection or active profile if we do not recreate the activity
        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 3 (About)
            drawer.setSelection(3, false);
        }

        /*
        //NOTE: This is how you can modify a specific library definition during runtime
        HashMap<String, HashMap<String, String>> libsModification = new HashMap<String, HashMap<String, String>>();
        HashMap<String, String> modifyAboutLibraries = new HashMap<String, String>();
        modifyAboutLibraries.put("name", "_AboutLibraries");
        libsModification.put("aboutlibraries", modifyAboutLibraries);
        .withLibraryModification(libsModification);
        */

        LibsSupportFragment fragment = new LibsBuilder()
                .withAboutIconShown(true)
                .withAboutVersionShown(true)
                .withAboutDescription(
                        "Copyright &#169; 2017 Lok Jian Ming <br /><br />" +
                        "This app is designed specifically for controlling STick, therefore achieving the purpose of taking blinds to wherever they want to go. Until further notice, this app is not intended for real world usage or production purposes, and is solely limited to usage for testing purposes." +
                                "<br /><br /><b>THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n" +
                                "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n" +
                                "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n" +
                                "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n" +
                                "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n" +
                                "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\n" +
                                "SOFTWARE.</b>"
                )
                .withLibraries("crouton", "activeandroid", "actionbarsherlock", "showcaseview")
                .withVersionShown(false)
                .withLicenseShown(false)
                .withLibraryModification("aboutlibraries", Libs.LibraryFields.LIBRARY_NAME, "_AboutLibraries")
                .supportFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

    }

    LibTaskCallback libTaskCallback = new LibTaskCallback() {
        @Override
        public void onLibTaskStarted() {
            Log.e("AboutLibraries", "started");
        }

        @Override
        public void onLibTaskFinished(ItemAdapter fastItemAdapter) {
            Log.e("AboutLibraries", "finished");
        }
    };

    LibsConfiguration.LibsUIListener libsUIListener = new LibsConfiguration.LibsUIListener() {
        @Override
        public View preOnCreateView(View view) {
            return view;
        }

        @Override
        public View postOnCreateView(View view) {
            return view;
        }
    };

    LibsConfiguration.LibsListener libsListener = new LibsConfiguration.LibsListener() {
        @Override
        public void onIconClicked(View v) {
            Toast.makeText(v.getContext(), "We are able to track this now ;)", Toast.LENGTH_LONG).show();
        }

        @Override
        public boolean onLibraryAuthorClicked(View v, Library library) {
            return false;
        }

        @Override
        public boolean onLibraryContentClicked(View v, Library library) {
            return false;
        }

        @Override
        public boolean onLibraryBottomClicked(View v, Library library) {
            return false;
        }

        @Override
        public boolean onExtraClicked(View v, Libs.SpecialButton specialButton) {
            return false;
        }

        @Override
        public boolean onIconLongClicked(View v) {
            return false;
        }

        @Override
        public boolean onLibraryAuthorLongClicked(View v, Library library) {
            return false;
        }

        @Override
        public boolean onLibraryContentLongClicked(View v, Library library) {
            return false;
        }

        @Override
        public boolean onLibraryBottomLongClicked(View v, Library library) {
            return false;
        }
    };

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
