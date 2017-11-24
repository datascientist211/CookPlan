package com.cookplan.recipe.grid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.cookplan.BaseActivity
import com.cookplan.BaseFragment
import com.cookplan.R
import com.cookplan.main.MainActivity
import com.cookplan.models.Recipe
import com.cookplan.recipe.edit.add_info.EditRecipeInfoActivity
import com.cookplan.recipe.import_recipe.search_url.SearchRecipeUrlActivity
import com.cookplan.recipe.view_item.RecipeViewActivity
import com.cookplan.utils.GridSpacingItemDecoration
import com.cookplan.utils.Utils
import com.github.clans.fab.FloatingActionButton

class RecipeGridFragment : BaseFragment(), RecipeGridView {

    private var adapter: RecipeGridRecyclerViewAdapter? = null
    private var presenter: RecipeGridPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        presenter = RecipeGridPresenterImpl(this)
    }

    override fun onStart() {
        super.onStart()
        presenter?.getRecipeList()
    }

    override fun onStop() {
        super.onStop()
        presenter?.onStop()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mainView = inflater.inflate(R.layout.fragment_recipe_list, container, false) as ViewGroup
        val progressBar = mainView?.findViewById<ProgressBar>(R.id.progress_bar)
        progressBar?.visibility = View.VISIBLE

        // Set the adapter
        val recyclerView = mainView?.findViewById<RecyclerView>(R.id.recipe_list_recycler)
        recyclerView?.layoutManager = GridLayoutManager(activity, 2)
        recyclerView?.addItemDecoration(GridSpacingItemDecoration(2, Utils.dpToPx(activity, 16), true))
        recyclerView?.itemAnimator = DefaultItemAnimator()
        adapter = RecipeGridRecyclerViewAdapter(
                { clickRecipe ->
                    if (activity is BaseActivity) {
                        val intent = Intent(activity, RecipeViewActivity::class.java)
                        intent.putExtra(RecipeViewActivity.RECIPE_OBJECT_KEY, clickRecipe)
                        (activity as BaseActivity).startActivityForResultWithLeftAnimation(intent,
                                MainActivity.OPEN_SHOP_LIST_REQUEST)
                    }
                },
                { longClickRecipe ->
                    AlertDialog.Builder(activity as Context, R.style.AppCompatAlertDialogStyle)
                            .setTitle(R.string.attention_title)
                            .setMessage(R.string.are_you_sure_remove_recipe)
                            .setPositiveButton(android.R.string.ok) { dialog, which ->
                                mainView?.findViewById<ProgressBar>(R.id.progress_bar)?.visibility = View.VISIBLE
                                presenter?.removeRecipe(longClickRecipe)
                            }
                            .setNegativeButton(android.R.string.cancel, null)
                            .show()
                    true
                }, activity as Context)
        recyclerView?.adapter = adapter

        val addRecipeFab = mainView?.findViewById<View>(R.id.add_recipe_fab) as FloatingActionButton
        addRecipeFab.setOnClickListener { startNewRecipeActivity() }

        val importRecipeFab = mainView?.findViewById<View>(R.id.import_recipe_fab) as FloatingActionButton
        importRecipeFab.setOnClickListener { startSearchRecipeUrlActivity() }

        return mainView
    }

    internal fun startSearchRecipeUrlActivity() {
        val intent = Intent(activity, SearchRecipeUrlActivity::class.java)
        (activity as BaseActivity).startActivityWithLeftAnimation(intent)
    }

    internal fun startNewRecipeActivity() {
        val intent = Intent(activity, EditRecipeInfoActivity::class.java)
        (activity as BaseActivity).startActivityWithLeftAnimation(intent)
    }

    override fun setEmptyView() {
        val progressBar = mainView?.findViewById<ProgressBar>(R.id.progress_bar)
        progressBar?.visibility = View.GONE
        setEmptyViewVisability(View.VISIBLE)
        setRecyclerViewVisability(View.GONE)
    }

    private fun setRecyclerViewVisability(visability: Int) {
        mainView?.findViewById<View>(R.id.recipe_list_recycler)?.visibility = visability
    }

    override fun setRecipeList(recipeList: List<Recipe>) {
        mainView?.findViewById<ProgressBar>(R.id.progress_bar)?.visibility = View.GONE
        setEmptyViewVisability(View.GONE)
        setRecyclerViewVisability(View.VISIBLE)
        adapter?.updateItems(recipeList)
    }

    companion object {

        @JvmStatic
        fun newInstance(): RecipeGridFragment {
            val fragment = RecipeGridFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
