package ch.unstable.ost.about

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem

import ch.unstable.ost.R
import ch.unstable.ost.theme.ThemedActivity


class AboutActivity : ThemedActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_about)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        val viewPager: ViewPager = findViewById(R.id.container)
        viewPager.adapter = mSectionsPagerAdapter

        findViewById<TabLayout>(R.id.tabs).setupWithViewPager(viewPager)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_about, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.action_settings ->
                //NavigationHelper.openSettings(this);
                return true
        }

        return super.onOptionsItemSelected(item)
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            return when (position) {
                0 -> AboutFragment.newInstance()
                1 -> LicenseFragment.newInstance(SOFTWARE_COMPONENTS)
                else -> null
            }
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): String? {
            return when (position) {
                0 -> getString(R.string.tab_about)
                1 -> getString(R.string.tab_licenses)
                else -> null
            }
        }
    }

    companion object {

        /**
         * List of all software components
         */
        private val SOFTWARE_COMPONENTS = listOf(
                //new SoftwareComponent("RxBinding", "2015", "Jake Wharton", "https://github.com/JakeWharton/RxBinding", StandardLicenses.APACHE2)
                SoftwareComponent("RxAndroid", "2015", "RxAndroid authors", "https://github.com/ReactiveX/RxAndroid", StandardLicenses.APACHE2),
                SoftwareComponent("RxJava", "2016-present", "RxJava contributors", "https://github.com/ReactiveX/RxJava", StandardLicenses.APACHE2),
                SoftwareComponent("CustomActivityOnCrash", "2014-2017", "Eduard Ereza Martínez", "https://github.com/Ereza/CustomActivityOnCrash", StandardLicenses.APACHE2),
                SoftwareComponent("Commons IO", "2017", "The Apache Software Foundation", "https://commons.apache.org/", StandardLicenses.APACHE2),
                SoftwareComponent("Gson", "2008", "Google Inc.", "https://github.com/google/gson", StandardLicenses.APACHE2))
    }
}
