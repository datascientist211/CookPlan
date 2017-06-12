package com.cookplan.providers;

import com.cookplan.models.Product;

import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by DariaEfimova on 24.04.17.
 */

public interface ProductProvider {

    Observable<List<Product>> getProductList();

    Observable<List<Product>> getCompanyProductList(String companyId);

    Observable<Map<String, List<Product>>> getTheClosestProductsToStrings(List<String> name);

    Single<Product> createProduct(Product product);

    Single<Product> updateProductNames(Product product);

    Completable updateProductCompanies(Product product);

    Observable<Product> getProductByName(String name);

    Completable increaseCountUsages(Product product);
}
