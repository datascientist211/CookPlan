package com.cookplan.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import java.io.Serializable;

/**
 * Created by DariaEfimova on 16.03.17.
 */

public class Recipe implements Serializable {

    private String id;
    private String name;
    private String desc;

    public Recipe() {
    }

    public Recipe(String id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public Recipe(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public RecipeDB getRecipeDB() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        return new RecipeDB(auth.getCurrentUser().getUid(), name, desc);
    }

    public static Recipe getRecipeFromDBObject(DataSnapshot itemSnapshot) {
        RecipeDB object = itemSnapshot.getValue(RecipeDB.class);
        Recipe recipe = new Recipe(itemSnapshot.getKey(), object.getName(), object.getDesc());
        return recipe;
    }

    public static class RecipeDB {

        private String id;
        private String name;
        private String desc;
        private String userId;

        public RecipeDB() {
        }

        public RecipeDB(String userId, String name, String desc) {
            this.name = name;
            this.desc = desc;
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }

        public String getId() {
            return id;
        }

        public String getUserId() {
            return userId;
        }
    }

}
