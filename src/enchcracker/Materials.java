package enchcracker;

public enum Materials {
    BOW("0: Any Bow (not Switchbow, non srp bow, yes BS Crossbow)", 1),
    FISHING_ROD("0: Any Fishing Rod", 1),
    BOOK("0: Book", 1),
    //DEFILED_ITEMS("0: Defiled items", 1),
    //LIVING("0: Living", 1),
    //SENTIENT("0: Sentient", 1),
    //TROLL_WEAPON("0: Troll Weapon", 1),
    //CUSHIONED_BOOTS("1: Cushioned Boots", 4),
    //DEATH_WORM_CHITIN("1: Death Worm Chitin", 4),
    //EXPLOSION_PICKAXE("1: Explosion Pickaxe", 4),
    //ICE("1: Ice", 4),
    //OBSIDIAN("1: Obsidian", 4),
    STONE("1: Stone", 4),
    //VEIN_PICKAXE("1: Vein Pickaxe", 4),
    WOOL("1: Wool", 4),
    //BLINDFOLD("1: Blindfold", 8),
    //BOUND("2: Bound", 8),
    //CINCINNASITE_DIAMOND("2: Cincinnasite-Diamond", 8),
    DIAMOND("2: Diamond", 8),
    //EARPLUGS("2: Earplugs", 8),
    //GAMBLE_PICKAXE("2: Gamble Pickaxe", 8),
    //HASTY_PICKAXE("2: Hasty Pickaxe", 8),
    IRON_ARMOR("2: Iron Armor", 8),
    //LIVING_SENTIENT_BOW("2: Living/Sentient Bow", 8),
    //MYRMEX_TOOL("2: Myrmex (Stinger) Tool", 8),
    //NINJA("2: Ninja", 8),
    //STEEL_ARMOR("2: Steel Armor", 8),
    SWITCH_CROSS_BOW("2: Switch-Cross/Bow", 8),
    //TROLL_LEATHER("2: Troll Leather", 8),
    //X407_PROTOTYPE("2: X407 Prototype", 8),
    //ARCHAEOLOGIST_HAT("3: Archaeologist Hat", 12),
    BRONZE("3: Bronze", 12),
    //CAESTUS("3: Caestus", 12),
    CHAINMAIL("3: Chainmail", 12),
    //CINCINNASITE("3: Cincinnasite", 12),
    //COWLEATHER("3: Cowleather", 12),
    DRAGON_SCALE("3: Dragon Scale", 12),
    //GOLEM("3: Golem", 12),
    IRON_TOOL("3: Iron Tool", 12),
    LEATHER("3: Leather", 12),
    //MYRMEX_ARMOR("3: Myrmex Armor", 12),
    //NEPTUNIUM("3: Neptunium", 12),
    //SCARLITE_WEAPON("3: Scarlite Weapon", 12),
    //SHEEP_DISGUISE("3: Sheep Disguise", 12),
    //BS_BIGSHIELD("3: BS Big Shield", 12),
    //STEEL_TOOL("3: Steel Tool", 12),
    //STUDDED("3: Studded", 12),
    //WIZARD("3: Wizard", 12),
    WOOD("3: Wood", 12),
    BOOK_WYRM_SCALE("4: Book Wyrm Scale", 16),
    SILVER_TOOL("4: Silver Tool", 16),
    //SUMMONING_STAFFS("4: Summoning Staffs", 16),
    //WRAITH_SIGIL("4: Wraith Sigil", 16),
    DRAGONBONE("5: (Blooded) Dragonbone", 20),
    GOLD_TOOL("5: Gold Tool", 20),
    //SCARLITE_ARMOR("5: Scarlite Armor", 20),
    SILVER_ARMOR("5: Silver Armor", 20),
    UMBRIUM("5: Umbrium", 20),
    GOLD_ARMOR("6: Gold Armor", 24),
    GOLDEN_BOOK_WYRM_SCALE("6: Golden Book Wyrm Scale", 24),
    //BS_SMALLSHIELD("6: BS Small Shield", 24),
    TIDE_GUARDIAN("6: Tide Guardian", 24),
    WITCH_HAT("7: Witch Hat", 28),
    ANY("Any available material",25);
    ;

    public final String name;
    public final int enchantability;
    public final int enchantabilityTier;
    Materials(String name, int enchantability) {
        this.name = name;
        this.enchantability = enchantability;
        enchantabilityTier = enchantability/4;
    }

    public static String[] getAllMaterials() {
        String[] ALL_MATERIALS = new String[Materials.values().length];
        int counter = 0;
        for(int i=0; i<Materials.values().length; i++) {
            ALL_MATERIALS[counter] = Materials.values()[i].name;
            counter++;
        }
        return ALL_MATERIALS;
    }

    public static Materials getMaterial(String selectedMaterial) {
        for(Materials i: Materials.values())
            if(i.name.equals(selectedMaterial)) return i;
        return BOOK;
    }

    public String toString() {
        return name;
    }
}