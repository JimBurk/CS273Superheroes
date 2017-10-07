package edu.orangecoastcollege.cs273.jburk.cs273superheroes;

/**
 * Created by jimburk on 10/5/17.
 */

public class SuperHeroes {
    private String mUserName;
    private String mName;
    private String mSuperpower;
    private String mOneThing;
    private String mImageName;

    public SuperHeroes(String userName, String name, String superpower, String oneThing) {
        mUserName = userName;
        mName = name;
        mSuperpower = superpower;
        mOneThing = oneThing;
        mImageName = userName + ".png";
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setSuperpower(String superpower) {
        mSuperpower = superpower;
    }

    public void setOneThing(String oneThing) {
        mOneThing = oneThing;
    }

    public void setImageName(String imageName) {
        mImageName = imageName;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getName() {
        return mName;
    }

    public String getSuperpower() {
        return mSuperpower;
    }

    public String getOneThing() {
        return mOneThing;
    }

    public String getImageName() {
        return mImageName;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuperHeroes superHeroes = (SuperHeroes) o;

        if (!mName.equals(superHeroes.mName)) return false;
        if (!mSuperpower.equals(superHeroes.mSuperpower)) return false;
        if (!mOneThing.equals(superHeroes.mOneThing)) return false;
        return mImageName.equals(superHeroes.mImageName);
    }
}
