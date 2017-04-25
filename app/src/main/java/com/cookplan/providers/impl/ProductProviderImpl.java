package com.cookplan.providers.impl;

import com.cookplan.R;
import com.cookplan.RApplication;
import com.cookplan.models.CookPlanError;
import com.cookplan.models.Product;
import com.cookplan.providers.ProductProvider;
import com.cookplan.utils.DatabaseConstants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by DariaEfimova on 24.04.17.
 */

public class ProductProviderImpl implements ProductProvider {


    private DatabaseReference database;

    private BehaviorSubject<List<Product>> subjectProductList;

    public ProductProviderImpl() {
        this.database = FirebaseDatabase.getInstance().getReference();
        subjectProductList = BehaviorSubject.create();
        getFirebaseAllProductList();
    }

    private void getFirebaseAllProductList() {
        Query items = database.child(DatabaseConstants.DATABASE_PRODUCT_TABLE);
        items.orderByChild(DatabaseConstants.DATABASE_PRODUCT_COUNT_USING_FIELD)
                .addValueEventListener(new ValueEventListener() {
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Product> products = new ArrayList<>();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                            Product product = Product.parseProductFromDB(itemSnapshot);
                            if (product != null && user != null) {
                                if (product.getUserId() == null || product.getUserId().equals(user.getUid())) {
                                    products.add(product);
                                }
                            }
                        }
                        if (subjectProductList != null) {
                            subjectProductList.onNext(products);
                        }
                    }

                    public void onCancelled(DatabaseError databaseError) {
                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            if (subjectProductList != null) {
                                subjectProductList.onError(new CookPlanError(databaseError));
                            }
                        }
                    }
                });
    }

    @Override
    public Observable<List<Product>> getProductList() {
        return subjectProductList;
    }

    //emitter.onError(new CookPlanError(RApplication.getAppContext().getString(R.string.recipe_doesnt_exist)));
    @Override
    public Single<Product> createProduct(Product product) {
        return Single.create(emitter -> {
            DatabaseReference productRef = database.child(DatabaseConstants.DATABASE_PRODUCT_TABLE);
            productRef.push().setValue(product, (databaseError, reference) -> {
                if (databaseError != null) {
                    emitter.onError(new CookPlanError(databaseError));
                } else {
                    product.setId(reference.getKey());
                    emitter.onSuccess(product);
                }
            });
        });
    }

    @Override
    public Completable increaseCountUsages(Product product) {
        return Completable.create(emitter -> {
            if (product != null && product.getId() != null) {
                DatabaseReference productRef = database.child(DatabaseConstants.DATABASE_PRODUCT_TABLE);
                productRef.child(product.getId())
                        .child(DatabaseConstants.DATABASE_PRODUCT_COUNT_USING_FIELD)
                        .setValue(product.increasingCount(), (databaseError, reference) -> {
                            if (emitter != null) {
                                if (databaseError != null) {
                                    emitter.onError(new CookPlanError(databaseError));
                                } else {
                                    emitter.onComplete();
                                }
                            }
                        });
            } else {
                emitter.onError(new CookPlanError(RApplication.getAppContext().getString(R.string.product_doesnt_exist)));
            }
        });
    }
}