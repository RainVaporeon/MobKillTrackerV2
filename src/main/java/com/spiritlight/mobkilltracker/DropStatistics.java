package com.spiritlight.mobkilltracker;

public class DropStatistics {
    private int MythicDropped = 0;
    private int FabledDropped = 0;
    private int LegendaryDropped = 0;
    private int RareDropped = 0;
    private int SetDropped = 0;
    private int UniqueDropped = 0;
    private int NormalDropped = 0;
    private int T3Ingredients = 0;
    private int T2Ingredients = 0;
    private int T1Ingredients = 0;
    private int T0Ingredients = 0;
    private boolean allowUpdates = true;
    
    public void clear() {
        MythicDropped = 0;
        FabledDropped = 0;
        LegendaryDropped = 0;
        RareDropped = 0;
        SetDropped = 0;
        UniqueDropped = 0;
        NormalDropped = 0;
        T3Ingredients = 0;
        T2Ingredients = 0;
        T1Ingredients = 0;
        T0Ingredients = 0;
    }

    // 0: all; 1: item; def: ingredients
    public int getTotal(int type) {
        return (type == 0 ? MythicDropped +
                FabledDropped +
                LegendaryDropped +
                RareDropped +
                SetDropped +
                UniqueDropped +
                NormalDropped +
                T3Ingredients +
                T2Ingredients +
                T1Ingredients +
                T0Ingredients : type == 1 ? MythicDropped +
                FabledDropped +
                LegendaryDropped +
                RareDropped +
                SetDropped +
                UniqueDropped +
                NormalDropped : T3Ingredients +
                T2Ingredients +
                T1Ingredients +
                T0Ingredients);
    }

    public boolean doAllowUpdates() { return allowUpdates; }

    public void setAllowUpdates(boolean bool) {
        allowUpdates = bool;
    }

    public int getMythicDropped() {
        return MythicDropped;
    }

    public int getFabledDropped() {
        return FabledDropped;
    }

    public int getLegendaryDropped() {
        return LegendaryDropped;
    }

    public int getRareDropped() {
        return RareDropped;
    }

    public int getSetDropped() {
        return SetDropped;
    }

    public int getUniqueDropped() {
        return UniqueDropped;
    }

    public int getNormalDropped() {
        return NormalDropped;
    }

    public int getT3Ingredients() {
        return T3Ingredients;
    }

    public int getT2Ingredients() {
        return T2Ingredients;
    }

    public int getT1Ingredients() {
        return T1Ingredients;
    }

    public int getT0Ingredients() {
        return T0Ingredients;
    }

    public void addDrop(Tier tier) {
        if(!allowUpdates) {
            // Does not allow updates, whether a totem is not in progress or already ended.
            return;
        }
        if(tier == null) {
            return;
        }
        // Not sure if arranging like this makes things faster, but this basically sorts by literal rarity.
        switch(tier) {
            case INGREDIENT_0:
                T0Ingredients++;
                break;
            case NORMAL:
                NormalDropped++;
                break;
            case INGREDIENT_1:
                T1Ingredients++;
                break;
            case UNIQUE:
                UniqueDropped++;
                break;
            case SET:
                SetDropped++;
                break;
            case RARE:
                RareDropped++;
                break;
            case LEGENDARY:
                LegendaryDropped++;
                break;
            case INGREDIENT_2:
                T2Ingredients++;
                break;
            case FABLED:
                FabledDropped++;
                break;
            case INGREDIENT_3:
                T3Ingredients++;
                break;
            case MYTHIC:
                MythicDropped++;
                break;
            default:
                return;
        }
    }

    public void removeDrop(Tier tier) {
        if(!allowUpdates) {
            // Does not allow updates, whether a totem is not in progress or already ended.
            return;
        }
        if(tier == null) {
            return;
        }
        // Not sure if arranging like this makes things faster, but this basically sorts by literal rarity.
        switch(tier) {
            case INGREDIENT_0:
                T0Ingredients--;
                break;
            case NORMAL:
                NormalDropped--;
                break;
            case INGREDIENT_1:
                T1Ingredients--;
                break;
            case UNIQUE:
                UniqueDropped--;
                break;
            case SET:
                SetDropped--;
                break;
            case RARE:
                RareDropped--;
                break;
            case LEGENDARY:
                LegendaryDropped--;
                break;
            case INGREDIENT_2:
                T2Ingredients--;
                break;
            case FABLED:
                FabledDropped--;
                break;
            case INGREDIENT_3:
                T3Ingredients--;
                break;
            case MYTHIC:
                MythicDropped--;
                break;
            default:
                return;
        }
    }
}
