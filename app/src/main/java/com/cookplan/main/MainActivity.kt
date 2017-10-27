package com.cookplan.main

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.cookplan.BaseActivity
import com.cookplan.R
import com.cookplan.RApplication
import com.cookplan.auth.ui.FirebaseAuthActivity
import com.cookplan.companies.MainCompaniesFragment
import com.cookplan.product_list.ProductListFragment
import com.cookplan.recipe.grid.RecipeGridFragment
import com.cookplan.share.SharePresenter
import com.cookplan.share.SharePresenterImpl
import com.cookplan.share.ShareView
import com.cookplan.share.add_users.AddUserForSharingActivity
import com.cookplan.shopping_list.list_by_dishes.ShopListByDishesFragment
import com.cookplan.shopping_list.total_list.TotalShoppingListFragment
import com.cookplan.todo_list.ToDoListFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener, MainView, ShareView {

    private var mProgressDialog: ProgressDialog? = null

    private var rootView: View? = null
    private var isFamilyModeTurnOn = false

    private var presenter: MainPresenter? = null

    protected var sharePresenter: SharePresenter? = null

    protected var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        rootView = findViewById(R.id.main_snackbar_layout)

        sharePresenter = SharePresenterImpl(this)
        presenter = MainPresenterImpl(this, this)
        val navigationView = findViewById(R.id.nav_view) as NavigationView
        fillNavHeader()
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.setCheckedItem(R.id.nav_shopping_list)
        setShoppingListFragment()
    }

    private fun fillNavHeader() {
        val navigationView = findViewById(R.id.nav_view) as NavigationView
        val headerView = navigationView.getHeaderView(0)
        val user = presenter?.currentUser
        if (user != null) {
            navigationView.menu.findItem(R.id.nav_sign_in).isVisible = user.isAnonymous
            navigationView.menu.findItem(R.id.nav_sign_out).isVisible = !user.isAnonymous

            val photoImageView = headerView.findViewById(R.id.user_photo_imageView) as ImageView
            Glide.with(this)
                    .load(user.photoUrl?.path)
                    .placeholder(R.drawable.main_drawable)
                    .into(photoImageView)

            val nameTextView = headerView.findViewById(R.id.user_name_textView) as TextView
            nameTextView.text = user.displayName
        } else {
            signedOut()
        }
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onStart() {
        sharePresenter?.isFamilyModeTurnOnRequest()
        super.onStart()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val itemId = item.itemId

        if (itemId == R.id.nav_recipe_list) {
            setRecipeListFragment()
        } else if (itemId == R.id.nav_shopping_list) {
            setShoppingListFragment()
        } else if (itemId == R.id.nav_vocabulary) {
            setProductListFragment()
        } else if (itemId == R.id.nav_todo_list) {
            setTODOListFragment()
        } else if (itemId == R.id.nav_companies) {
            setCompaniesListFragment()
        } else if (itemId == R.id.nav_sign_out) {
            presenter?.signOut()
        } else if (itemId == R.id.nav_sign_in) {
            presenter?.signIn()
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    internal fun setRecipeListFragment() {
        setFamilyModeMenuOptions()

        val tabsLayout = findViewById(R.id.main_tabs_layout)
        tabsLayout.visibility = View.GONE
        val viewPager = findViewById(R.id.main_tabs_viewpager)
        viewPager.visibility = View.GONE

        //        FrameLayout mainConteinerView = (FrameLayout) findViewById(R.id.fragment_container);
        //        mainConteinerView.setVisibility(View.VISIBLE);

        setTitle(getString(R.string.recipe_list_menu_title))
        val pointListFragment = RecipeGridFragment.newInstance()
        val transaction = supportFragmentManager.beginTransaction()
        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            transaction.add(R.id.fragment_container, pointListFragment)
        } else {
            transaction.replace(R.id.fragment_container, pointListFragment)
        }
        transaction.commit()
    }

    internal fun setShoppingListFragment() {
        setFamilyModeMenuOptions()

        val tabsLayout = findViewById(R.id.main_tabs_layout)
        tabsLayout.visibility = View.VISIBLE

        val viewPager = findViewById(R.id.main_tabs_viewpager) as ViewPager
        viewPager.visibility = View.VISIBLE

        val adapter = ViewPagerTabsAdapter(supportFragmentManager)
        adapter.addFragment(TotalShoppingListFragment.newInstance(), getString(R.string.tab_all_ingredients_title))
        adapter.addFragment(ShopListByDishesFragment.newInstance(), getString(R.string.tab_ingredients_by_dish_title))
        //        adapter.addFragment(new ThreeFragment(), "THREE");
        viewPager.adapter = adapter

        val tabLayout = findViewById(R.id.main_tabs_layout) as TabLayout
        tabLayout.setupWithViewPager(viewPager)
        setTitle(getString(R.string.shopping_list_title))
    }

    internal fun setProductListFragment() {
        setFamilyModeMenuOptions()

        val tabsLayout = findViewById(R.id.main_tabs_layout)
        tabsLayout.visibility = View.GONE
        val viewPager = findViewById(R.id.main_tabs_viewpager)
        viewPager.visibility = View.GONE

        //        FrameLayout mainConteinerView = (FrameLayout) findViewById(R.id.fragment_container);
        //        mainConteinerView.setVisibility(View.VISIBLE);

        setTitle(getString(R.string.product_vocabulary_title))

        val pointListFragment = ProductListFragment.newInstance()
        val transaction = supportFragmentManager.beginTransaction()
        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            transaction.add(R.id.fragment_container, pointListFragment)
        } else {
            transaction.replace(R.id.fragment_container, pointListFragment)
        }
        transaction.commit()
    }

    internal fun setTODOListFragment() {
        menu?.findItem(R.id.app_bar_share_on)?.isVisible = false
        menu?.findItem(R.id.app_bar_share_off)?.isVisible = false

        val tabsLayout = findViewById(R.id.main_tabs_layout)
        tabsLayout.visibility = View.GONE
        val viewPager = findViewById(R.id.main_tabs_viewpager)
        viewPager.visibility = View.GONE

        //        FrameLayout mainConteinerView = (FrameLayout) findViewById(R.id.fragment_container);
        //        mainConteinerView.setVisibility(View.VISIBLE);

        setTitle(getString(R.string.todo_list_title))

        val fragment = ToDoListFragment.newInstance()
        val transaction = supportFragmentManager.beginTransaction()
        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            transaction.add(R.id.fragment_container, fragment)
        } else {
            transaction.replace(R.id.fragment_container, fragment)
        }
        transaction.commit()
    }

    internal fun setCompaniesListFragment() {
        menu?.findItem(R.id.app_bar_share_on)?.isVisible = false
        menu?.findItem(R.id.app_bar_share_off)?.isVisible = false

        val tabsLayout = findViewById(R.id.main_tabs_layout)
        tabsLayout.visibility = View.GONE
        val viewPager = findViewById(R.id.main_tabs_viewpager)
        viewPager.visibility = View.GONE

        setTitle(getString(R.string.companies_list_title))

        val fragment = MainCompaniesFragment.newInstance()
        val transaction = supportFragmentManager.beginTransaction()
        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            transaction.add(R.id.fragment_container, fragment)
        } else {
            transaction.replace(R.id.fragment_container, fragment)
        }
        transaction.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
        dismissDialog()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_SHOP_LIST_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                setShoppingListFragment()
                val navigationView = findViewById(R.id.nav_view) as NavigationView
                navigationView.setCheckedItem(R.id.nav_shopping_list)
            }
        } else if (requestCode == SHARE_USER_LIST_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val emailList = data.getStringArrayListExtra(SHARE_USER_EMAIL_LIST_KEY)
                if (emailList != null) {
                    if (emailList.isEmpty()) {
                        sharePresenter?.turnOffFamilyMode()
                    } else {
                        sharePresenter?.shareData(emailList)
                    }
                }
            }
        } else {
            presenter?.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun showSnackbar(messageRes: Int) {
        Snackbar.make(rootView!!, messageRes, Snackbar.LENGTH_LONG).show()
    }

    override fun showLoadingDialog(message: String) {
        dismissDialog()
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog?.isIndeterminate = true
            mProgressDialog?.setTitle("")
        }

        mProgressDialog?.setMessage(message)
        mProgressDialog?.show()
    }

    override fun signedInWithAnonymous() {
        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.menu.findItem(R.id.nav_sign_in).isVisible = true
        navigationView.menu.findItem(R.id.nav_sign_out).isVisible = false
    }

    override fun signedInWithGoogle() {
        fillNavHeader()
        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.menu.findItem(R.id.nav_sign_in).isVisible = false
        navigationView.menu.findItem(R.id.nav_sign_out).isVisible = true
    }

    override fun signedInFailed() {
        dismissDialog()
        showSnackbar(R.string.unknown_sign_in_response)
    }

    override fun signedOut() {
        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.menu.findItem(R.id.nav_sign_in).isVisible = false
        navigationView.menu.findItem(R.id.nav_sign_out).isVisible = false
        val intent = Intent()
        RApplication.saveAnonymousPossibility(false)
        intent.setClass(this, FirebaseAuthActivity::class.java)
        startActivityWithLeftAnimation(intent)
        finish()
    }

    override fun showLoadingDialog(@StringRes stringResource: Int) {
        showLoadingDialog(getString(stringResource))
    }

    override fun dismissDialog() {
        mProgressDialog?.dismiss()
        mProgressDialog = null
    }

    override fun onCreateOptionsMenu(_menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, _menu)
        menu = _menu
        setFamilyModeMenuOptions()
        return super.onCreateOptionsMenu(_menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (!FirebaseAuth.getInstance().currentUser!!.isAnonymous && (id == R.id.app_bar_share_off || id == R.id.app_bar_share_on)) {
            if (id == R.id.app_bar_share_off) {
                val intent = Intent(this, AddUserForSharingActivity::class.java)
                startActivityForResultWithLeftAnimation(intent, SHARE_USER_LIST_REQUEST)
                return true
            } else {
                AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle)
                        .setTitle(R.string.attention_title)
                        .setMessage(R.string.turn_off_family_mode_question)
                        .setPositiveButton(R.string.change_users_faminy_mode_title) { dialog, _ ->
                            dialog.dismiss()
                            val intent = Intent(this, AddUserForSharingActivity::class.java)
                            startActivityForResultWithLeftAnimation(intent, SHARE_USER_LIST_REQUEST)
                        }
                        .setNegativeButton(R.string.turn_off_faminy_mode) { dialog, _ ->
                            sharePresenter?.turnOffFamilyMode()
                            dialog.dismiss()
                        }
                        .show()
                return true
            }
        } else if (FirebaseAuth.getInstance().currentUser != null && FirebaseAuth.getInstance().currentUser!!.isAnonymous) {
            AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle)
                    .setTitle(R.string.attention_title)
                    .setMessage(R.string.need_to_auth)
                    .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
                    .show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setShareIcon(_isFamilyModeTurnOn: Boolean) {
        isFamilyModeTurnOn = _isFamilyModeTurnOn
        setFamilyModeMenuOptions()
    }

    private fun setFamilyModeMenuOptions() {
        menu?.findItem(R.id.app_bar_share_on)?.isVisible = isFamilyModeTurnOn
        menu?.findItem(R.id.app_bar_share_off)?.isVisible = !isFamilyModeTurnOn
    }

    override fun setShareError(errorResourceId: Int) {
        Toast.makeText(this, errorResourceId, Toast.LENGTH_LONG).show()
    }

    companion object {
        val OPEN_SHOP_LIST_REQUEST = 10
        val SHARE_USER_LIST_REQUEST = 11
        val SHARE_USER_EMAIL_LIST_KEY = "SHARE_USER_EMAIL_LIST_KEY"
    }
}