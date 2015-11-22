package com.wanted.list;

import com.wanted.R;
import com.wanted.entities.Seeker;
import com.wanted.entities.User;

import java.util.ArrayList;

/**
 * Created by ZWX on 2015/11/16.
 */
public final class Constants {

    public User[] followingArray;
    public User[] followerArray;

    public Constants(){
        initFollowingArray();
        initFollowerArray();
    }

    public void initFollowingArray() {
        if (followingArray == null) {
            followingArray = new User[Following_Name.length];
            for (int i = 0 ; i < followingArray.length; ++i) {
                followingArray[i] = new Seeker(Following_Name[i], "pwd", "email", 0);
                followingArray[i].setAvatar(Following_IMAGES[i]);
            }
        }
    }

    public void initFollowerArray() {
        if (followerArray == null) {
            followerArray = new Seeker[Follower_Name.length];
            for (int i = 0 ; i < followerArray.length; ++i) {
                followerArray[i] = new Seeker(Follower_Name[i], "pwd", "email", 0);
                followerArray[i].setAvatar(Follower_IMAGES[i]);
            }
        }
    }

    public static final String[] Follower_Name = new String[] {
            "Bob",
            "Jike",
            "Mike",
            "Tom",
            "Mary",
            "John",
            "Alex",
            "Cap",
            "Felix",
            "Dipan",
            "Maxim",
    };

    public static final String[] Following_Name= new String[] {
            "Ann",
            "Zillow",
            "Frog",
            "Rabbit",
            "Mary",
            "John",
            "Mike",
            "Tom",
            "Felix",
            "Dipan",
            "Maxim",
    };

    public static final String[] Follower_IMAGES = new String[]{
            "drawable://" + R.drawable.people_rose,
            "drawable://" + R.drawable.people_mary,
            "drawable://" + R.drawable.people_ann,
            "drawable://" + R.drawable.people_mary,
            "drawable://" + R.drawable.people_rose,
            "drawable://" + R.drawable.people_tom,
            "drawable://" + R.drawable.people_mike,
            "drawable://" + R.drawable.people_joan,
            "drawable://" + R.drawable.people_ann,
            "drawable://" + R.drawable.people_keith,
            "drawable://" + R.drawable.people_joan,
    };

    public static final String[] Following_IMAGES = new String[]{
            "drawable://" + R.drawable.people_mike,
            "drawable://" + R.drawable.people_john,
            "drawable://" + R.drawable.people_mike,
            "drawable://" + R.drawable.people_mary,
            "drawable://" + R.drawable.people_rose,
            "drawable://" + R.drawable.people_tom,
            "drawable://" + R.drawable.people_keith,
            "drawable://" + R.drawable.people_joan,
            "drawable://" + R.drawable.people_ann,
            "drawable://" + R.drawable.people_mary,
            "drawable://" + R.drawable.people_mike,
    };

}
