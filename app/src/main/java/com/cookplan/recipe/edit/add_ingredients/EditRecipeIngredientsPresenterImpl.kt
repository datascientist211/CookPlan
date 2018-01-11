package com.cookplan.recipe.edit.add_ingredients


import com.cookplan.models.Ingredient
import com.cookplan.models.Recipe
import com.cookplan.providers.IngredientProvider
import com.cookplan.providers.ProviderFactory
import com.cookplan.providers.impl.IngredientProviderImpl
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

/**
 * Created by DariaEfimova on 21.03.17.
 */

class EditRecipeIngredientsPresenterImpl internal constructor(private val mainView: EditRecipeIngredientsView?, private val recipe: Recipe) : EditRecipeIngredientsPresenter {

    private val dataProvider: IngredientProvider
    private val disposables: CompositeDisposable

    init {
        this.dataProvider = ProviderFactory.Companion.ingredientProvider
        disposables = CompositeDisposable()
    }

    override fun getAsyncIngredientList() {
        disposables.add(dataProvider.getIngredientListByRecipeId(recipe.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<List<Ingredient>>() {

                    override fun onNext(ingredients: List<Ingredient>) {
                        mainView?.setIngredientList(ingredients)
                    }

                    override fun onError(e: Throwable) {
                        mainView?.setErrorToast(e.message ?: String())
                    }

                    override fun onComplete() {

                    }
                }))
    }

    override fun removeIngredient(ingredient: Ingredient) {
        dataProvider.removeIngredient(ingredient)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onComplete() {

                    }

                    override fun onError(e: Throwable) {
                        mainView?.setErrorToast(e.message ?: String())
                    }
                })
    }

    override fun onStop() {
        disposables.clear()
    }
}