package com.spiritlight.mobkilltracker;

/**
 * A list of enum to specify an item's rarity tier.<br><br>
 * If there are any unknown item that you do not wish to process, return {@link Tier#UNKNOWN}.
 */
public enum Tier {
    MYTHIC, FABLED, LEGENDARY, RARE, SET, UNIQUE, NORMAL, INGREDIENT_3, INGREDIENT_2, INGREDIENT_1, INGREDIENT_0, UNKNOWN
}
