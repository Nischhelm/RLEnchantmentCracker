package enchcracker;

public enum Items {
	HELMET("Helmet",0),
	CHESTPLATE("Chestplate",1),
	LEGGINGS("Leggings",2),
	BOOTS("Boots",3),
	WOLF_ARMOR("Wolf Armor",4),
	SPARTAN_SHIELD("Spartan Shield",5),
	BS_SHIELD("BS Shield",6),
	PICKAXE("Pickaxe",7),
	SHOVEL_MATTOCK_SAW("Shovel/Mattock/Saw",8),
	AXE("Axe",9),
	BS_DAGGER("BS Dagger",12),
	//BS_HAMMER("BS Hammer",13),
	//BS_NUNCHAKU("BS Nunchaku",14),
	KNIFE("Knife",17),
	WEAPON_WITHOUT("Weapon without sweeping edge",10),
	WEAPON_WITH("Weapon with sweeping edge",15),
	BS_BATTLE_AXE("BS Battle Axe",11),
	SPARTAN_BATTLEAXE("Spartan Battleaxe",16),
	SWITCH_BOW("Switch-Bow",18),
	SWITCH_CROSSBOW("Switch-Crossbow",19),
	BOW("Bow (not Switchbow, yes BS-Crossbow)",20),
	CROSSBOW("Crossbow",31),

	FISHING_ROD("Fishing Rod",21),
	//UMBRA_BLASTER("Umbra Blaster",22),
	//THE_RAVAGER("The Ravager",23),
	//CONCUSSION_SMASHER("Concussion Smasher",24),
	THROWING_WEAPON("Throwing Weapon",25),
	//TEARS("Tears",26),
	//SUMMONING_STAFF("Summoning Staff",27),
	//WRAITH_SIGIL("Wraith Sigil",28),
	//LOCK("Lock",29),
	BOOK("Book",30),
	ANY_ARMOR("Any Armor",32),
	ANY_TOOL("Any Tool",33),
	ANY_WEAPON("Any Weapon",34);
	;

	public final String name;
	public final int id;
	Items(String name, int id) {
		this.name = name;
		this.id = id;
	}

	public static Items getItem(String selectedItem) {
		for(Items i: Items.values())
			if(i.name.equals(selectedItem)) return i;
		return BOOK;
	}

	public static String[] getAllItems() {
		String[] ALL_ITEMS = new String[Items.values().length];
		int counter = 0;
		for(Items i: Items.values()) {
			ALL_ITEMS[counter] = i.name;
			counter++;
		}
		return ALL_ITEMS;
	}

	public String toString() {
		return name;
	}
}