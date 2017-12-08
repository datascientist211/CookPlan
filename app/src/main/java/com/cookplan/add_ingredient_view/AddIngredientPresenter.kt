package com.cookplan.add_ingredient_view

import com.cookplan.models.MeasureUnit
import com.cookplan.models.Product
import com.cookplan.models.ProductCategory
import com.cookplan.models.Recipe

/**
 * Created by DariaEfimova on 20.03.17.
 */

interface AddIngredientPresenter {

    var isNeedToBuy: Boolean

    fun setRecipe(recipe: Recipe)

    fun getAsyncProductList()

    fun saveIngredient(product: Product, amount: Double, measureUnit: MeasureUnit)

    fun saveProductAndIngredient(category: ProductCategory, name: String,
                                 amount: Double, measureUnit: MeasureUnit)

    fun onStop()
}
